package rs.ac.singidunum.fssbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rs.ac.singidunum.fssbackend.entity.User;
import rs.ac.singidunum.fssbackend.model.UserModel;
import rs.ac.singidunum.fssbackend.repository.IUserRepository;

import java.util.List;
import java.util.Locale;

@Service
public class UserService implements IUserService, UserDetailsService {

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private AutoMapperService autoMapperService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(String id) {
        return this.userRepository.findUserById(id);
    }

    @Override
    public List<User> findAllBySlug(String slug) {
        return userRepository.findAllBySlug(slug);
    }

    @Override
    public User insert(UserModel model) {

        if (this.findByUsername(model.getUsername()) == null) {
            model.setSlug((model.getFirstName() + " " + model.getLastName()).toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9-]", "-"));

            var sameSlugs = findAllBySlug(model.getSlug());

            if (sameSlugs.size() > 0) {
                model.setSlug(model.getSlug() + "-" + sameSlugs.size());
            }

            return userRepository.insert(autoMapperService.map(model, User.class));
        }
        else {
            return null;
        }
    }

    public User update(UserModel model) {
        User userHelper = this.findById(model.getId());

        if (model.getEmail() != null) { userHelper.setEmail(model.getEmail()); }

        if (model.getPassword() != null) { userHelper.setPassword(passwordEncoder.encode(model.getPassword())); }

        if (model.getFirstName() != null) { userHelper.setFirstName(model.getFirstName()); }

        if (model.getLastName() != null) { userHelper.setLastName(model.getLastName()); }

        return userRepository.save(userHelper);
    }

    @Override
    public User insert(User entity) {
        return userRepository.insert(entity);
    }

    // ugradjena metoda, koristi se za jwt
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return this.findByUsername(s);
    }
}
