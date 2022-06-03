package rs.ac.singidunum.fssbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rs.ac.singidunum.fssbackend.entity.User;
import rs.ac.singidunum.fssbackend.model.UserModel;
import rs.ac.singidunum.fssbackend.service.UserService;

@RestController
@RequestMapping("users")
public class UserController {
    @Autowired
    private UserService userService;

    @CrossOrigin(origins = "*")
    @GetMapping("findbyid")
    public User findById(@RequestParam("id") String id) { return userService.findById(id); }

    @CrossOrigin(origins = "*")
    @GetMapping("findbyusername")
    public User findByUsername(@RequestParam("username") String username) { return userService.findByUsername(username); }

    @CrossOrigin(origins = "*")
    @PutMapping("update")
    public User update(@RequestBody UserModel model) {
        return userService.update(model);
    }

}
