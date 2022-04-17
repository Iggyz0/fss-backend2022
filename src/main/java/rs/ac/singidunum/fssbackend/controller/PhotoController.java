package rs.ac.singidunum.fssbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rs.ac.singidunum.fssbackend.entity.Photo;
import rs.ac.singidunum.fssbackend.service.PhotoService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("photos")
public class PhotoController {
    @Autowired
    private PhotoService photoService;

    @CrossOrigin(origins = "*")
    @GetMapping("findallbyusername")
    public List<Photo> findAllByUsername(@RequestParam("username") String username) { return photoService.findAllByUsername(username); }

    @CrossOrigin(origins = "*")
    @PostMapping("uploadphoto")
    public Photo uploadPhoto(@RequestParam("file") MultipartFile file, @RequestParam("username") String username) {
        if (file != null)
            return photoService.insertPhoto(file, username);
        return null;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("myphotos/downloadphoto") // download by file name and username
    public ResponseEntity<Resource> downloadPhoto(@RequestParam("fileName") String fileName, @RequestParam("username") String username, HttpServletRequest request) {
        return this.photoService.downloadPhotoFromServer(fileName, username, request);
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("myphotos/deletephoto")
    public void deletePhoto(@RequestParam("fileName") String fileName, @RequestParam("username") String username) {
        this.photoService.deleteByFileNameAndUser_Username(fileName, username);
    }
}
