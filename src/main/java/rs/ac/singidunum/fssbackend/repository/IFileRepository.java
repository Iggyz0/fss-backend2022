package rs.ac.singidunum.fssbackend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import rs.ac.singidunum.fssbackend.entity.FileFromUser;

import java.util.ArrayList;

public interface IFileRepository extends MongoRepository<FileFromUser, String> {

    ArrayList<FileFromUser> findAllByFileNameInAndUser_Username(ArrayList<String> fileName, String username);

    ArrayList<FileFromUser> findAllByUser_Username(String username);

    void deleteById(String id);

    @Query(value = "{'_id': ?0 }")
    FileFromUser findFileFromUserById(String id);
}
