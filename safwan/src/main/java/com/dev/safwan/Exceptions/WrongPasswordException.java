package com.dev.safwan.Exceptions;

public class WrongPasswordException extends Exception{
    public WrongPasswordException(String message){
        super(message);
    }
}
