package rs.ac.singidunum.fssbackend.service;

import rs.ac.singidunum.fssbackend.entity.Note;
import rs.ac.singidunum.fssbackend.model.NoteModel;

import java.util.List;

public interface INoteService {
    Note insertNote(NoteModel model);
    List<Note> findAllBySlug(String slug);
    void deleteById(String id);
    Note findNoteById(String id);
    Note update(NoteModel model);
    List<Note> findAllByUsername(String username);
    List<Note> findAllBySearch(String search, String username);
}
