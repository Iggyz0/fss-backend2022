package rs.ac.singidunum.fssbackend.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import rs.ac.singidunum.fssbackend.entity.Photo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IPhotoService {
    Photo insertPhoto(MultipartFile file, String username);
    void deleteByFileNameAndUser_Username(String fileName, String username);
    ResponseEntity<Resource> downloadPhotoFromServer(String fileName, String username, HttpServletRequest request);
    List<Photo> findAllByUsername(String username);
    Photo findByFileNameAndUser_Username(String fileName, String username);
}
