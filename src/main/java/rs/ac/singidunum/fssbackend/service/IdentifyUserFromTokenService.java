package rs.ac.singidunum.fssbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.singidunum.fssbackend.config.JwtToken;
import rs.ac.singidunum.fssbackend.entity.User;
import rs.ac.singidunum.fssbackend.repository.IUserRepository;

import javax.servlet.http.HttpServletRequest;

@Service
public class IdentifyUserFromTokenService {
    @Autowired
    private JwtToken jwtToken;
    @Autowired
    private IUserRepository userRepository;

    public String getUserIdFromReq(HttpServletRequest request) {
        return this.jwtToken.getUserId(request.getHeader("authorization").split(" ")[1]);
    }

    public String getUsernameFromReq(HttpServletRequest request) {
        return this.jwtToken.getUsername(request.getHeader("authorization").split(" ")[1]);
    }

    public User getFullUser(HttpServletRequest request) {
        return this.userRepository.findByUsername(this.getUsernameFromReq(request));
    }
}
