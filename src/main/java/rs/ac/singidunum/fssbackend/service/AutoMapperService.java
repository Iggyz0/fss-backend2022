package rs.ac.singidunum.fssbackend.service;

import org.apache.tika.metadata.Metadata;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import rs.ac.singidunum.fssbackend.entity.User;
import rs.ac.singidunum.fssbackend.model.AudioModel;
import rs.ac.singidunum.fssbackend.model.FileFromUserModel;
import rs.ac.singidunum.fssbackend.model.PhotoModel;
import rs.ac.singidunum.fssbackend.model.UserModel;

import java.time.LocalDate;

@Service
public class AutoMapperService implements IAutoMapperService {
    @Autowired
    private ModelMapper modelMapper;

    public FileFromUserModel mapIncomingFileToFileFromUserModel(MultipartFile source, String username) {
        FileFromUserModel dest = new FileFromUserModel();
        UserModel userModel = new UserModel();
        userModel.setUsername(username);

        dest.setUser(this.map(userModel, User.class));
        dest.setFileName(source.getOriginalFilename());
        dest.setExtension(source.getContentType());
        dest.setFileSize(source.getSize() + "");
        dest.setDateUploaded(LocalDate.now());
        dest.setDownloadPath(ServletUriComponentsBuilder.fromCurrentContextPath().path("/files/myfiles/downloadfile?fileName=").path(source.getOriginalFilename()).toUriString());
        return dest;
    }


    public PhotoModel mapUserNameToPhotoModel(String username, PhotoModel photoModel) {
        UserModel userModel = new UserModel();
        userModel.setUsername(username);

        photoModel.setUser(this.map(userModel, User.class));

        return photoModel;
    }

    public AudioModel mapUserNameToAudioModel(String username, AudioModel audioModel, Metadata metadata, String fileName) {
//        String[] metadataNames = metadata.names();
//
//        for(String name : metadataNames) {
//            System.out.println(name + ": " + metadata.get(name));
//        }

        UserModel userModel = new UserModel();
        userModel.setUsername(username);

        audioModel.setUser(this.map(userModel, User.class));

        audioModel.setFileSize(this.formatSize(audioModel.getFileSize()));

        try {
            audioModel.setDuration(this.convertSecondsToDurationFormated(metadata.get("xmpDM:duration")));
        } catch (Exception e) {
            audioModel.setDuration("");
        }

        try {
            audioModel.setSampleRate(metadata.get("xmpDM:audioSampleRate") + " Hz");
        } catch (Exception e) {
            audioModel.setSampleRate("");
        }

        try {
            audioModel.setAudioChannels(metadata.get("channels"));
        } catch (Exception e) {
            audioModel.setAudioChannels("");
        }

        try {
            audioModel.setChannelType(metadata.get("xmpDM:audioChannelType"));
        } catch (Exception e) {
            audioModel.setChannelType("");
        }

        try {
            audioModel.setArtist(metadata.get("xmpDM:artist"));
        } catch (Exception e) {
            audioModel.setArtist("");
        }

        try {
            audioModel.setGenre(metadata.get("xmpDM:genre"));
        } catch (Exception e) {
            audioModel.setGenre("");
        }

        audioModel.setFileName(fileName);

        audioModel.setDownloadPath(ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/audio/myaudio/downloadaudio?fileName=").path(fileName)
                .path("&username=" + username)
                .toUriString());

        audioModel.setDateUploaded(LocalDate.now());

        return audioModel;
    }

    private String convertSecondsToDurationFormated(String time) {
        var timeConverted = Double.parseDouble(time);
        if (timeConverted <= 0) return "";
        int h = (int) (timeConverted / 3600);
        int m = (int) ( (timeConverted % 3600) / 60 );
        int s = (int) (timeConverted % 60);

        String timeToReturn = "";
        if (h > 0)
            timeToReturn += "" + h + ":" + (m < 10 ? "0" : "");
        timeToReturn += "" + m + ":" + (s < 10 ? "0" : "");
        timeToReturn += "" + s;

        return timeToReturn;

    }

    private String formatSize(String stringOfBytes) {
        long v = Long.parseLong(stringOfBytes);

        if (v < 1024) return v + " B";
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.2f %sB", (double)v / (1L << (z*10)), " KMGTPE".charAt(z));
    }

    @Override
    public <T> T map(Object model, Class<T> entity) {
        return modelMapper.map(model, entity);
    }
}