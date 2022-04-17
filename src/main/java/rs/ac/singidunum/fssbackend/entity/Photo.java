package rs.ac.singidunum.fssbackend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Data
@Document(collection = "photos")
public class Photo {
    @Id
    private String id;
    @Field("fileName")
    private String fileName;
    @Field("user")
    private User user;
    @Field("downloadPath")
    private String downloadPath;
    @Field("fileType")
    private String fileType;
    @Field("fileSize")
    private String fileSize;
    @Field("dateUploaded")
    private LocalDate dateUploaded;
}
