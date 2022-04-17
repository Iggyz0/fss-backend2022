package rs.ac.singidunum.fssbackend.service;

import rs.ac.singidunum.fssbackend.entity.User;
import rs.ac.singidunum.fssbackend.model.UserModel;

import java.util.List;

public interface IUserService {
    User findByUsername(String username);
    List<User> findAll();
    User insert(UserModel model);
    User insert(User entity);
    List<User> findAllBySlug(String slug);
}
