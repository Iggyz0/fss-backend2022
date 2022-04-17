package rs.ac.singidunum.fssbackend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import rs.ac.singidunum.fssbackend.entity.Audio;

import java.util.List;

public interface IAudioRepository extends MongoRepository<Audio, String> {
    List<Audio> findAllByUser_Username(String username);

    Audio findByFileNameAndUser_Username(String fileName, String username);

    void deleteByFileNameAndUser_Username(String fileName, String username);

    @Query(value = "{'user.username': {$regex: ?0}, $or: [ {'artist': {$regex: ?1, $options: 'i'}}, {'fileName': {$regex: ?2, $options: 'i'}}, {'genre': {$regex: ?2, $options: 'i'}}  ]  }")
    List<Audio> findAllByCustomSearch(String username, String artist, String fileName, String genre);
}
