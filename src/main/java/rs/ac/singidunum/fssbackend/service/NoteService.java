package rs.ac.singidunum.fssbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.singidunum.fssbackend.entity.Note;
import rs.ac.singidunum.fssbackend.model.NoteModel;
import rs.ac.singidunum.fssbackend.repository.INoteRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
public class NoteService implements INoteService {
    @Autowired
    INoteRepository noteRepository;
    @Autowired
    AutoMapperService autoMapperService;

    @Override
    public List<Note> findAllByUsername(String username) {
        return this.noteRepository.findAllByUser_Username(username);
    }

    @Override
    public List<Note> findAllBySearch(String search, String username) {
        return this.noteRepository.findAllBySearch(search, username);
    }

    @Override
    public List<Note> findAllBySlug(String slug) {
        return noteRepository.findAllBySlug(slug);
    }

    @Override
    public Note insertNote(NoteModel model) {
        if (model.getUser().getUsername() == "") {
            return null;
        }

        model.setSlug(model.getTitle().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9-]", "-"));

        var sameSlugs = findAllBySlug(model.getSlug());

        if (sameSlugs.size() > 0) {
            model.setSlug(model.getSlug() + "-" + sameSlugs.size());
        }

        model.setDateCreated(LocalDate.now());
        model.setDateUpdated(LocalDate.now());

        return noteRepository.insert(autoMapperService.map(model, Note.class));
    }

    @Override
    public void deleteById(String id) {
        noteRepository.deleteById(id);
    }

    @Override
    public Note findNoteById(String id) {
        return noteRepository.findNoteById(id);
    }

    @Override
    public Note update(NoteModel model) {
        Note noteHelper = this.findNoteById(model.getId());

        if (model.getTitle() != null) { noteHelper.setTitle(model.getTitle()); }

        if (model.getTags() != null) { noteHelper.setTags(model.getTags()); }

        if (model.getContent() != null) { noteHelper.setContent(model.getContent()); }

        noteHelper.setDateUpdated(LocalDate.now());

        return this.noteRepository.save(noteHelper);
    }
}
