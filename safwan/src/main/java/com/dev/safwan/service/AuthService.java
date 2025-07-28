package com.dev.safwan.service;

import com.dev.safwan.Exceptions.UserAlreadyExistsException;
import com.dev.safwan.Exceptions.WrongPasswordException;
import com.dev.safwan.models.User;
import com.dev.safwan.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
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
            return "token";
        }
        else {
            throw new WrongPasswordException("Wrong Password Exception");
        }
    }
}
