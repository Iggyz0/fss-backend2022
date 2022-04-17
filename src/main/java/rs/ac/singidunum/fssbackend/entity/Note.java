package rs.ac.singidunum.fssbackend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Data
@Document(collection = "notes")
public class Note {
    @Id
    private String id;
    @Field("title")
    private String title;
    @Field("tags")
    private String[] tags;
    @Field("slug")
    private String slug;
    @Field("user")
    private User user;
    @Field("content")
    private String content;
    @Field("dateCreated")
    private LocalDate dateCreated;
    @Field("dateUpdated")
    private LocalDate dateUpdated;
}
