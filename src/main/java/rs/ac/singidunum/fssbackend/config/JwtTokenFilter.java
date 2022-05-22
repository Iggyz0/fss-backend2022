package rs.ac.singidunum.fssbackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import rs.ac.singidunum.fssbackend.service.UserService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static org.apache.logging.log4j.util.Strings.isEmpty;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    @Autowired
    JwtToken jwtToken;

    @Autowired
    UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION );

        String token = "";

        if( isEmpty(header) || !header.startsWith("Bearer ") )
        {
            if (httpServletRequest.getRequestURI().equals("/audio/myaudio/showaudio") || httpServletRequest.getRequestURI().equals("/photos/myphotos/showphoto") || httpServletRequest.getRequestURI().equals("/files/myfiles/showfile")) {
                token = httpServletRequest.getParameter("access_token");
                if (token.isEmpty() || token.trim().equals("")) {
                    filterChain.doFilter(httpServletRequest, httpServletResponse);
                    return;
                }
            } else {
                filterChain.doFilter(httpServletRequest, httpServletResponse);
                return;
            }
//            filterChain.doFilter(httpServletRequest, httpServletResponse);
//            return;
        }
        else {
            token = header.split(" ")[1].trim();
        }

        if( !jwtToken.validate(token) ){
            filterChain.doFilter(httpServletRequest,httpServletResponse);
        }

        var user_details = userService.loadUserByUsername( jwtToken.getUsername(token) );

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(  user_details , null, user_details == null ? List.of():user_details.getAuthorities() );

        authenticationToken.setDetails( new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }
}
