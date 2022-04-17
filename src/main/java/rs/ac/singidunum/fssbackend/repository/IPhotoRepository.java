package rs.ac.singidunum.fssbackend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import rs.ac.singidunum.fssbackend.entity.Photo;

import java.util.List;


public interface IPhotoRepository extends MongoRepository<Photo, String> {
    List<Photo> findAllByUser_Username(String username);

    void deleteByFileNameAndUser_Username(String fileName, String username);

    Photo findByFileNameAndUser_Username(String fileName, String username);
}
