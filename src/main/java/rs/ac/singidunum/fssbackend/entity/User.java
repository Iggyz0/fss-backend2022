package rs.ac.singidunum.fssbackend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Data
@Document(collection = "users")
public class User implements Serializable, UserDetails {
    @Id
    private String id;
    @Field("tags")
    private String[] tags;
    @Field("slug")
    private String slug;
    @Field("firstName")
    private String firstName;
    @Field("username")
    private String username;
    @Field("password")
    private String password;
    @Field("lastName")
    private String lastName;
    @Field("dateOfBirth")
    private LocalDate dateOfBirth;
    @Field("city")
    private String city;
    @Field("country")
    private String country;
    @Field("email")
    private String email;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
