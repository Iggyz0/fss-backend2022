package rs.ac.singidunum.fssbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rs.ac.singidunum.fssbackend.entity.Note;
import rs.ac.singidunum.fssbackend.model.NoteModel;
import rs.ac.singidunum.fssbackend.service.NoteService;

import java.util.List;

@RestController
@RequestMapping("notes")
public class NoteController {
    @Autowired
    private NoteService noteService;

    @CrossOrigin(origins = "*")
    @GetMapping("findallbyusername")
    public List<Note> findAllByUserUsername(@RequestParam("username") String username) {
        return noteService.findAllByUsername(username);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("insert")
    public Note insert(@RequestBody NoteModel model) {
        return noteService.insertNote(model);
    }

    @CrossOrigin(origins = "*")
    @PutMapping("update")
    public Note update(@RequestBody NoteModel model) {
        return noteService.update(model);
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("delete/{id}")
    public void delete(@PathVariable("id") String id) {
        this.noteService.deleteById(id);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("findbyid")
    public Note findNoteById(@RequestParam("id") String id) {
        return this.noteService.findNoteById(id);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("findallbysearch")
    public List<Note> findAllBySearch(@RequestParam String search, @RequestParam String username) {
        return this.noteService.findAllBySearch(search, username);
    }

}
