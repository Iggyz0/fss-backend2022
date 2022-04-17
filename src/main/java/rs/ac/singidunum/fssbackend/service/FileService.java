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
import rs.ac.singidunum.fssbackend.entity.FileFromUser;
import rs.ac.singidunum.fssbackend.model.FileFromUserModel;
import rs.ac.singidunum.fssbackend.repository.IFileRepository;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class FileService implements IFileService {
    @Autowired
    private IFileRepository fileRepository;
    @Autowired
    AutoMapperService autoMapperService;
    @Autowired
    FilePathService filePathService;

    @Override
    public ArrayList<FileFromUser> findAllByUsername(String username) {
        return this.fileRepository.findAllByUser_Username(username);
    }

    @Override
    public ArrayList<FileFromUser> findAllByNamesAndUsername(ArrayList<FileFromUser> files, String username) {
        ArrayList<String> getThese = new ArrayList<>();
        for(FileFromUser file : files)
            getThese.add(file.getFileName());
        return this.fileRepository.findAllByFileNameInAndUser_Username(getThese, username);
    }

    @Override
    public void deleteById(String id, String username) {
        try {
            FileFromUser fileFromUser = this.fileRepository.findFileFromUserById(id);
            if (this.deleteFileFromStorage(fileFromUser.getFileName(), username)) {
                this.fileRepository.deleteById(id);
            }
            else {
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean deleteFileFromStorage(String fileName, String username) {
        final Path storageLocation = this.filePathService.userFilePath("file", username);

        try {
            Path filePath = storageLocation.resolve(fileName).normalize();
            Files.delete(filePath);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<FileFromUser> uploadFiles(MultipartFile[] files, String username) {
        final Path storageLocation = this.filePathService.userFilePath("file", username);

        try {
            Files.createDirectories(storageLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (files.length <= 0) {
            return null;
        }
        ArrayList<FileFromUser> incomingFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            FileFromUserModel fileConverted = this.autoMapperService.mapIncomingFileToFileFromUserModel(file, username);
            incomingFiles.add(this.autoMapperService.map(fileConverted, FileFromUser.class));
        }

        ArrayList<FileFromUser> overlapping = this.findAllByNamesAndUsername(incomingFiles, username);

        if (overlapping.size() > 0) {
            for (FileFromUser o: overlapping) {
                for (FileFromUser i: incomingFiles) {
                    if (o.getFileName().equals(i.getFileName())) {
                        o.setFileSize(i.getFileSize());
                    }
                }
            }

            incomingFiles.removeIf(inc ->
                    overlapping.stream()
                            .map(FileFromUser::getFileName)
                            .anyMatch((fname) ->
                                    fname.equals(inc.getFileName())
                            )
            );
        }

        try {
//            var test35 = overlapping;
//            var test25 = incomingFiles;

            if (overlapping.size() > 0) {
                this.updateInDatabaseOnlyWithReplace(overlapping, username);
            }
            for (MultipartFile file : files) {
                this.storeFile(file, storageLocation);
            }
            if (incomingFiles.size() > 0)
                return this.fileRepository.insert(incomingFiles);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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

    private List<FileFromUser> updateInDatabaseOnlyWithReplace(List<FileFromUser> filesFromUser, String username) {

        for (FileFromUser file : filesFromUser) {
            file.setDateUploaded(LocalDate.now());
        }

        return this.fileRepository.saveAll(filesFromUser);
    }

    @Override
    public ResponseEntity<Resource> downloadFileFromServer(String id, String username, HttpServletRequest request) {

        final Path storageLocation = this.filePathService.userFilePath("file", username);

        Resource file = this.loadFileAsResource(this.fileRepository.findFileFromUserById(id).getFileName(), storageLocation);

        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        String contentType = null;

        try {
            contentType = request.getServletContext().getMimeType(file.getFile().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);

    }

    private Resource loadFileAsResource(String fileName, Path storageLocation) {
        try {
            Path filePath = storageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new Exception("File " + fileName + " not found!");
            }
        } catch (Exception e) {
            return null;
        }
    }

}
