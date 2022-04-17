package rs.ac.singidunum.fssbackend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import rs.ac.singidunum.fssbackend.entity.User;

import java.util.List;

public interface IUserRepository extends MongoRepository<User, String> {
    @Query(value = "{'username': ?0 }")
    User findByUsername(String username);

    @Query(value = "{'slug': {$regex : ?0, $options: 'i'}}")
    List<User> findAllBySlug(String slug);

    User findUserById(String id);

    User findBySlug(String slug);
}
