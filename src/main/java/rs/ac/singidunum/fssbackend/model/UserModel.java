package rs.ac.singidunum.fssbackend.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserModel {
    private String id;
    private String[] tags;
    private String slug;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String city;
    private String country;
    private String email;
}
