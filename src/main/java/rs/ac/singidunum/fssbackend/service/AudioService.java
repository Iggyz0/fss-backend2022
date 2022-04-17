package rs.ac.singidunum.fssbackend.service;

import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import rs.ac.singidunum.fssbackend.entity.Audio;
import rs.ac.singidunum.fssbackend.model.AudioModel;
import rs.ac.singidunum.fssbackend.repository.IAudioRepository;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@Service
public class AudioService implements IAudioService {
    @Autowired
    AutoMapperService autoMapperService;
    @Autowired
    IAudioRepository audioRepository;
    @Autowired
    FilePathService filePathService;

    private final Set<String> allowedAudioExtensions = Set.of(
            "mp3", "wav", "aac", "flac", "m4a"
    );

    @Override
    public List<Audio> findAllByUsername(String username) { return this.audioRepository.findAllByUser_Username(username); }

    @Override
    public Audio findByFileNameAndUsername(String fileName, String username) {
        return this.audioRepository.findByFileNameAndUser_Username(fileName, username);
    }

    @Override
    public Audio insertAudio(MultipartFile file, String username) {

        String fileName;

        if (file != null && !file.isEmpty()) {
            fileName = file.getOriginalFilename();
        } else return null;

        final Path storageLocation = this.filePathService.userFilePath("audio", username);

        Tika tika = new Tika();
        try {
            String detectedFormat = (tika.detect(file.getBytes())).toLowerCase(Locale.ROOT); // automatski detektuje tip fajla cak i kada se promeni ekstenzija -> dobija se audio/mpeg npr.
            assert fileName != null;
            String extension = (fileName.substring(fileName.lastIndexOf(".") + 1)).toLowerCase(Locale.ROOT); // ekstenzija fajla preko naziva samog fajle -> audio.mp3 = mp3

            if (!this.allowedAudioExtensions.contains(extension)) {
                throw new Exception("Audio extension not supported.");
            }
            if (!detectedFormat.contains("audio")) {
                throw new Exception("The file is not an audio.");
            }
        }
        catch (Exception e) {
            return null;
        }

        try {
            Files.createDirectories(storageLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }

        AudioModel newAudioEntity = autoMapperService.map(file, AudioModel.class);



        // citanje metadata
        Metadata metadata = new Metadata();
        try {
            InputStream streamFile = file.getInputStream();
            BodyContentHandler handler = new BodyContentHandler();
            Parser parser = new AutoDetectParser();
            ParseContext parseCtx = new ParseContext();
            parser.parse(streamFile, handler, metadata, parseCtx);
            streamFile.close();

        } catch (Exception e) {
            System.out.println("Parser metadate ne radi");
        }

        newAudioEntity = this.autoMapperService.mapUserNameToAudioModel(username, newAudioEntity, metadata, fileName);

        try {
            Audio alreadyExists = this.findByFileNameAndUsername(fileName, username);
            if(this.storeFile(file, storageLocation) != null && alreadyExists != null) {
                return this.updateInDatabaseOnly(alreadyExists);
            }
            return audioRepository.insert(autoMapperService.map(newAudioEntity, Audio.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String storeFile(MultipartFile file, Path storageLocation) throws Exception {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            if(fileName.contains("..")) {
                throw new Exception("Sorry! Filename contains invalid path sequence " + fileName);
            }
            Path targetLocation = storageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private Audio updateInDatabaseOnly(Audio audio) {

        audio.setDateUploaded(LocalDate.now());

        return this.audioRepository.save(audio);
    }

    @Override
    public ResponseEntity<Resource> downloadAudioFromServer(String fileName, String username, HttpServletRequest request) {
        final Path storageLocation = this.filePathService.userFilePath("audio", username);

        Resource audio = this.loadFileAsResource(fileName, storageLocation);

        if (audio == null) {
            return ResponseEntity.notFound().build();
        }

        String contentType = null;

        try {
            contentType = request.getServletContext().getMimeType(audio.getFile().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + audio.getFilename() + "\"")
                .body(audio);

    }

    private Resource loadFileAsResource(String fileName, Path storageLocation) {
        try {
            Path filePath = storageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new Exception("Audio file " + fileName + " not found!");
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void deleteByFileName(String fileName, String username) {
        if (this.deleteAudioFromStorage(fileName, username)) {
            this.audioRepository.deleteByFileNameAndUser_Username(fileName, username);
        }
    }

    private boolean deleteAudioFromStorage(String fileName, String username) {
        final Path storageLocation = this.filePathService.userFilePath("audio", username);

        try {
            Path filePath = storageLocation.resolve(fileName).normalize();
            Files.delete(filePath);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Audio> findAllBySearch(String search, String username) {
        return this.audioRepository.findAllByCustomSearch(username, search, search, search);
    }
}
