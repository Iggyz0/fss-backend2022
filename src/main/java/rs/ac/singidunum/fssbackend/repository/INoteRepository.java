package rs.ac.singidunum.fssbackend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import rs.ac.singidunum.fssbackend.entity.Note;

import java.util.List;

public interface INoteRepository extends MongoRepository<Note, String> {
    @Query(value = "{'slug': ?0 }")
    List<Note> findAllBySlug(String slug);

    Note findNoteById(String id);

    List<Note> findAllByUser_Username(String username);

    @Query(value = "{'title': {$regex : ?0, $options: 'i'}, 'user.username': {$regex : ?1} }")
    List<Note> findAllBySearch(String search, String username);

}
