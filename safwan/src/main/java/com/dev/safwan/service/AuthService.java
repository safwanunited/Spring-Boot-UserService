package com.dev.safwan.service;

import com.dev.safwan.Exceptions.UserAlreadyExistsException;
import com.dev.safwan.Exceptions.WrongPasswordException;
import com.dev.safwan.models.User;
import com.dev.safwan.repositories.UserRepository;
import io.jsonwebtoken.Jwts;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    SecretKey key = Jwts.SIG.HS256.key().build();

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    public  Boolean signUp(String email,String password) throws UserAlreadyExistsException {
        if(userRepository.findByEmail(email).isPresent()){
            throw new UserAlreadyExistsException("User with"+email+" already exists ");
        }
        User user=new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);
        return true;
    }
    public  String login(String email, String password) throws UserAlreadyExistsException, WrongPasswordException
    {
        Optional<User>userOptional=userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            throw new UserAlreadyExistsException("User With" + email +" does Not Exists ");
        }
        boolean matches=bCryptPasswordEncoder.matches(password,userOptional.get().getPassword());
        if(matches){
            return createJwtToken(userOptional.get().getId(),new ArrayList<>(),userOptional.get().getPassword()) ;
        }
        else {
            throw new WrongPasswordException("Wrong Password Exception");
        }
    }
    private String createJwtToken(Long userId, List<String> roles,String email){
        Map<String, Object> dataInJwt=new HashMap<>();
        dataInJwt.put("user id",userId);
        dataInJwt.put("roles",roles);
        dataInJwt.put("email",email);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        Date datePlus30 = calendar.getTime();

        String token=Jwts.builder()
                .claims(dataInJwt)
                .expiration(datePlus30)
                .issuedAt(new Date())
                .signWith(key)
                .compact();
return token;

    }
}
