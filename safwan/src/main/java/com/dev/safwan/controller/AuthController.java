package com.dev.safwan.controller;

import com.dev.safwan.dtos.*;
import com.dev.safwan.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/sign_up")
    public ResponseEntity<signupResponseDto> signUp(signupRequestdto request){
        signupResponseDto response=new signupResponseDto();

        if(authService.signUp(request.getEmail(), request.getEmail())){
            response.setRequestStatus(RequestStatus.SUCCESS);

        }
        else{

            response.setRequestStatus(RequestStatus.FAILURE);
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<loginResponseDto> login(loginRequestDto request){

        String token=authService.login(request.getEmail(), request.getPassword());
        loginResponseDto loginDto=new loginResponseDto();
        loginDto.setRequestStatus(RequestStatus.SUCCESS);

        MultiValueMap<String,String>headers=new LinkedMultiValueMap<>();
        headers.add("AUTH_TOKEN",token);

        ResponseEntity<loginResponseDto>response=new ResponseEntity<>(loginDto,headers, HttpStatus.OK);
        return response;
    }
}
