package rs.ac.singidunum.fssbackend.model;

import lombok.Data;
import rs.ac.singidunum.fssbackend.entity.User;

import java.time.LocalDate;

@Data
public class NoteModel {
    private String id;
    private String title;
    private String[] tags;
    private String slug;
    private User user;
    private String content;
    private LocalDate dateCreated;
    private LocalDate dateUpdated;
}
