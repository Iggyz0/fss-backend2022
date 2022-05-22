package rs.ac.singidunum.fssbackend.model;

import lombok.Data;
import rs.ac.singidunum.fssbackend.entity.Audio;
import rs.ac.singidunum.fssbackend.entity.FileFromUser;
import rs.ac.singidunum.fssbackend.entity.Photo;

import java.util.List;

@Data
public class AllFilesWrap {
    private List<Audio> audios;
    private List<Photo> photos;
    private List<FileFromUser> files;

    public List<Audio> getAudios() {
        return audios;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public List<FileFromUser> getFiles() {
        return files;
    }

    public void setAudios(List<Audio> audios) {
        this.audios = audios;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public void setFiles(List<FileFromUser> files) {
        this.files = files;
    }
}
