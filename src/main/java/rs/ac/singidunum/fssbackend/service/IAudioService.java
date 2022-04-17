package rs.ac.singidunum.fssbackend.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import rs.ac.singidunum.fssbackend.entity.Audio;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IAudioService {
    List<Audio> findAllByUsername(String username);
    Audio insertAudio(MultipartFile file, String username);
    ResponseEntity<Resource> downloadAudioFromServer(String fileName, String username, HttpServletRequest request);
    void deleteByFileName(String fileName, String username);
    Audio findByFileNameAndUsername(String fileName, String username);
}
