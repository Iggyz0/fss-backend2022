package rs.ac.singidunum.fssbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import rs.ac.singidunum.fssbackend.config.JwtToken;
import rs.ac.singidunum.fssbackend.entity.User;
import rs.ac.singidunum.fssbackend.model.UserModel;
import rs.ac.singidunum.fssbackend.service.AutoMapperService;
import rs.ac.singidunum.fssbackend.service.UserService;

@RestController
@RequestMapping("auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AutoMapperService autoMapperService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtToken jwtToken;


    @PostMapping("createaccount")
    @CrossOrigin(origins = "*")
    public User createAccount(@RequestBody UserModel model)
    {
        model.setPassword(passwordEncoder.encode(model.getPassword()));

        return userService.insert(model);
    }

    @PostMapping("login")
    @CrossOrigin(origins = "*")
    public ResponseEntity<UserModel> login(@RequestBody UserModel userModel)
    {
        try{
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userModel.getUsername(), userModel.getPassword()
                    )
            );

            User user = (User) authenticate.getPrincipal();

            return ResponseEntity.ok()
                    .header("Access-Control-Expose-Headers", HttpHeaders.AUTHORIZATION)
                    .header("Access-Control-Allow-Headers", HttpHeaders.AUTHORIZATION)
                    .header(HttpHeaders.AUTHORIZATION, jwtToken.generateAccessToken(user) )
                    .body( autoMapperService.map( user, UserModel.class ) );
        } catch (BadCredentialsException ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
