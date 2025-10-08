package com.dev.safwan.controller;

import com.dev.safwan.Exceptions.UserAlreadyExistsException;
import com.dev.safwan.Exceptions.WrongPasswordException;
import com.dev.safwan.dtos.*;
import com.dev.safwan.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<signupResponseDto> signUp(@RequestBody signupRequestdto request) throws UserAlreadyExistsException {
        signupResponseDto response=new signupResponseDto();

        if(authService.signUp(request.getEmail(), request.getPassword())){
            response.setRequestStatus(RequestStatus.SUCCESS);

        }
        else{
            response.setRequestStatus(RequestStatus.FAILURE);
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<loginResponseDto> login(@RequestBody loginRequestDto request)  {
    try {
        String token=authService.login(request.getEmail(), request.getPassword());
        loginResponseDto loginDto=new loginResponseDto();
        loginDto.setRequestStatus(RequestStatus.SUCCESS);
        MultiValueMap<String,String>headers=new LinkedMultiValueMap<>();
        headers.add("AUTH_TOKEN",token);
        ResponseEntity<loginResponseDto>response=new ResponseEntity<>(loginDto,headers, HttpStatus.OK);
        return response;
    }
catch (Exception e){
    loginResponseDto loginDto=new loginResponseDto();
    loginDto.setRequestStatus(RequestStatus.FAILURE);
    ResponseEntity<loginResponseDto>response=new ResponseEntity<>(loginDto,null,HttpStatus.BAD_REQUEST);
    return response;
}
    }
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validate(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(value = "token", required = false) String token) {

        String finalToken = (authHeader != null && !authHeader.isEmpty()) ? authHeader : token;

        if (finalToken == null || finalToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }

        boolean isValid = authService.validate(finalToken);
        return ResponseEntity.ok(isValid);
    }




}
