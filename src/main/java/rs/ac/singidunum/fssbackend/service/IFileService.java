package rs.ac.singidunum.fssbackend.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import rs.ac.singidunum.fssbackend.entity.FileFromUser;
import rs.ac.singidunum.fssbackend.model.AllFilesWrap;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public interface IFileService {
    AllFilesWrap findAllByUsername(String username);
    List<FileFromUser> uploadFiles(MultipartFile[] files, String username);
    ArrayList<FileFromUser> findAllByNamesAndUsername(ArrayList<FileFromUser> files, String username);
    void deleteById(String id, String username);
    ResponseEntity<Resource> downloadFileFromServer(String fileName, String username, HttpServletRequest request);
}
