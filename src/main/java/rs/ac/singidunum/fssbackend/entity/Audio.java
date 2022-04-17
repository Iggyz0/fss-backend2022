package rs.ac.singidunum.fssbackend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Data
@Document(collection = "audio")
public class Audio {
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
    @Field("duration")
    private String duration;
    @Field("sampleRate")
    private String sampleRate;
    @Field("genre")
    private String genre;
    @Field("audioChannels")
    private String audioChannels;
    @Field("channelType")
    private String channelType;
    @Field("artist")
    private String artist;
}
