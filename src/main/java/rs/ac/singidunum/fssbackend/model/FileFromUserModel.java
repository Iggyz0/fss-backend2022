package rs.ac.singidunum.fssbackend.model;

import lombok.Data;
import rs.ac.singidunum.fssbackend.entity.User;

import java.time.LocalDate;

@Data
public class FileFromUserModel {
    private String id;
    private String fileName;
    private User user;
    private String downloadPath;
    private String extension;
    private String fileSize;
    private LocalDate dateUploaded;
}
