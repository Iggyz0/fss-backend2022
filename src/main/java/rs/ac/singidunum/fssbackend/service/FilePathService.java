package rs.ac.singidunum.fssbackend.service;

import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FilePathService {

    private String filePath = "src/main/resources/user_resource_files/";

    public String userFilePathString(String typeOfFile, String username) {
        return this.filePath+username+"/"+typeOfFile;
    }

    public Path userFilePath(String typeOfFile, String username) {
        return Paths.get(this.userFilePathString(typeOfFile, username));
    }
}
