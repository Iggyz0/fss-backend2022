package rs.ac.singidunum.fssbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import rs.ac.singidunum.fssbackend.entity.Photo;
import rs.ac.singidunum.fssbackend.model.PhotoModel;
import rs.ac.singidunum.fssbackend.repository.IPhotoRepository;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class PhotoService implements IPhotoService {
    @Autowired
    private IPhotoRepository photoRepository;
    @Autowired
    AutoMapperService autoMapperService;
    @Autowired
    FilePathService filePathService;

    @Override
    public List<Photo> findAllByUsername(String username) { return photoRepository.findAllByUser_Username(username); }

    @Override
    public Photo findByFileNameAndUser_Username(String fileName, String username) {
        return this.photoRepository.findByFileNameAndUser_Username(fileName, username);
    }

    @Override
    public Photo insertPhoto(MultipartFile file, String username) {
        final Path storageLocation = this.filePathService.userFilePath("photo", username);

        // proveravamo da li je fajl slika [ prepoznaje samo BMP, GIF, JPG, PNG ]
        try (InputStream input = file.getInputStream()) {
            try {
                if(ImageIO.read(input).toString() == null){ return null; }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Files.createDirectories(storageLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String fileName = file.getOriginalFilename();

        PhotoModel newPhotoEntity = autoMapperService.map(file, PhotoModel.class);

        newPhotoEntity = this.autoMapperService.mapUserNameToPhotoModel(username, newPhotoEntity);

        newPhotoEntity.setFileName(fileName);

        newPhotoEntity.setDownloadPath(ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/photos/myphotos/downloadphoto?fileName=").path(fileName)
                .path("&username=" + username)
                .toUriString());

        newPhotoEntity.setDateUploaded(LocalDate.now());

        var test352 = newPhotoEntity;

        try {
            Photo alreadyExists = this.findByFileNameAndUser_Username(fileName, username);
            if (alreadyExists != null) {
                alreadyExists.setFileSize(file.getSize() + "");
            }
            if(this.storeFile(file, storageLocation) != null && alreadyExists != null) {
                return this.updateInDatabaseOnly(alreadyExists);
            }
            return photoRepository.insert(autoMapperService.map(newPhotoEntity, Photo.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String storeFile(MultipartFile file, Path storageLocation) throws Exception {
        // Normalize file name
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            if(fileName.contains("..")) {
                throw new Exception("Sorry! Filename contains invalid path sequence " + fileName);
            }
            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = storageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private Photo updateInDatabaseOnly(Photo photo) {

        photo.setDateUploaded(LocalDate.now());

        return this.photoRepository.save(photo);
    }

    @Override
    public ResponseEntity<Resource> downloadPhotoFromServer(String fileName, String username, HttpServletRequest request) {
        final Path storageLocation = this.filePathService.userFilePath("photo", username);

        Resource photo = this.loadFileAsResource(fileName, storageLocation);

        if (photo == null) {
            return ResponseEntity.notFound().build();
        }

        String contentType = null;

        try {
            contentType = request.getServletContext().getMimeType(photo.getFile().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + photo.getFilename() + "\"")
                .body(photo);

    }

    private Resource loadFileAsResource(String fileName, Path storageLocation) {
        try {
            Path filePath = storageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new Exception("Photo " + fileName + " not found!");
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void deleteByFileNameAndUser_Username(String fileName, String username) {
        if (this.deletePhotoFromStorage(fileName, username)) {
            this.photoRepository.deleteByFileNameAndUser_Username(fileName, username);
        }
    }

    private boolean deletePhotoFromStorage(String fileName, String username) {
        final Path storageLocation = this.filePathService.userFilePath("photo", username);

        try {
            Path filePath = storageLocation.resolve(fileName).normalize();
            Files.delete(filePath);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
