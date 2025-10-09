package com.dev.safwan.service;

import com.dev.safwan.Exceptions.UserAlreadyExistsException;
import com.dev.safwan.Exceptions.WrongPasswordException;
import com.dev.safwan.dtos.SendEmailMessageDTO;
import com.dev.safwan.models.Session;
import com.dev.safwan.models.SessionStatus;
import com.dev.safwan.models.User;
import com.dev.safwan.repositories.SessionRepository;
import com.dev.safwan.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class AuthService {
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private SecretKey key = Jwts.SIG.HS256.key().build();
    private final SessionRepository sessionRepository;
    public AuthService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.sessionRepository = sessionRepository;
    }


    public  Boolean signUp(String email,String password) throws UserAlreadyExistsException {
        if(userRepository.findByEmail(email).isPresent()){
            throw new UserAlreadyExistsException("User with"+email+" already exists ");
        }
        User user=new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        User saveduser=userRepository.save(user);
        SendEmailMessageDTO message=new SendEmailMessageDTO();
        message.setTo(email);
        message.setSubject("Welcome to our application");
        message.setBody("Thank you for signing up to our application");
        try{
            kafkaTemplate.send("sendEmail",objectMapper.writeValueAsString(message));
            }
        catch (Exception e){}

        System.out.println("This is the Saved User"+saveduser);
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
            String token= createJwtToken(userOptional.get().getId(),new ArrayList<>(),userOptional.get().getEmail());
            Session session=new Session();
            session.setToken(token);
            session.setUser(userOptional.get());

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 30);
            Date datePlus30 = calendar.getTime();

            session.setExpiringAt(datePlus30);
            session.setSessionStatus(SessionStatus.ACTIVE);

            sessionRepository.save(session);
        }
        else {
            throw new WrongPasswordException("Wrong password");
        }
        return createJwtToken(userOptional.get().getId(),new ArrayList<>(),userOptional.get().getEmail());
    }
    private String createJwtToken(Long userId, List<String> roles,String email){
        Map<String,Object>dataInJwt=new HashMap<>();
        dataInJwt.put("user_id",userId);
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

    public boolean validate(String token){
try {
    Jws<Claims> claims=Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token);
    Date expiryAt=claims.getPayload().getExpiration();
    Long userId=claims.getPayload().get("user_id", Long.class);


    return true;
} catch (Exception e) {
return false;
}

    }
}
