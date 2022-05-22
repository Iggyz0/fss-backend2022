package rs.ac.singidunum.fssbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rs.ac.singidunum.fssbackend.entity.FileFromUser;
import rs.ac.singidunum.fssbackend.model.AllFilesWrap;
import rs.ac.singidunum.fssbackend.service.FileService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("files")
public class FileController {

    @Autowired
    private FileService fileService;

    @CrossOrigin(origins = "*")
    @GetMapping("findallbyusername")
    public AllFilesWrap findAllByUserName(@RequestParam("username") String username) {
        return this.fileService.findAllByUsername(username);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("uploadfiles")
    public List<FileFromUser> uploadFiles(@RequestParam("files") MultipartFile[] files, @RequestParam("username") String username) {
        return this.fileService.uploadFiles(files, username);
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("myfiles/deletefile")
    public void deleteFile(@RequestParam("id") String id, @RequestParam("username") String username) { this.fileService.deleteById(id, username); }

    @CrossOrigin(origins = "*", exposedHeaders = HttpHeaders.CONTENT_DISPOSITION)
    @GetMapping("myfiles/downloadfile")
    public ResponseEntity<Resource> downloadFile(@RequestParam("id") String id, @RequestParam("username") String username, HttpServletRequest request) {
        return this.fileService.downloadFileFromServer(id, username, request);
    }

    @CrossOrigin(origins = "*", exposedHeaders = HttpHeaders.CONTENT_DISPOSITION)
    @GetMapping("myfiles/showfile")
    public ResponseEntity<Resource> showFile(@RequestParam("id") String id, @RequestParam("username") String username, HttpServletRequest request) {
        return this.fileService.downloadFileFromServer(id, username, request);
    }
}
