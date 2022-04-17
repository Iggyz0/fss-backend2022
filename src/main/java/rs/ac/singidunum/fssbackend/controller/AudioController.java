package rs.ac.singidunum.fssbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rs.ac.singidunum.fssbackend.entity.Audio;
import rs.ac.singidunum.fssbackend.service.AudioService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("audio")
public class AudioController {
    @Autowired
    AudioService audioService;

    @CrossOrigin(origins = "*")
    @GetMapping("findallbyusername")
    public List<Audio> findAllByUsername(@RequestParam("username") String username) { return audioService.findAllByUsername(username); }

    @CrossOrigin(origins = "*")
    @PostMapping("uploadaudio")
    public Audio uploadAudio(@RequestParam("file") MultipartFile file, @RequestParam("username") String username) {
        if (file != null)
            return audioService.insertAudio(file, username);
        return null;
    }

    @CrossOrigin(origins = "*", exposedHeaders = HttpHeaders.CONTENT_DISPOSITION)
    @GetMapping("myaudio/downloadaudio") // download by file name and username
    public ResponseEntity<Resource> downloadAudio(@RequestParam("fileName") String fileName, @RequestParam("username") String username, HttpServletRequest request) {
        return this.audioService.downloadAudioFromServer(fileName, username, request);
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("myaudio/deleteaudio")
    public void deleteAudio(@RequestParam("fileName") String fileName, @RequestParam("username") String username) { this.audioService.deleteByFileName(fileName, username); }

    @CrossOrigin(origins = "*")
    @GetMapping("myaudio/search")
    public List<Audio> findAllBySearch(@RequestParam String search, @RequestParam String username) { return this.audioService.findAllBySearch(search, username); }
}
