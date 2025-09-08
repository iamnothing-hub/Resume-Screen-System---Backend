package com.resume.screening.auth.exception;

import lombok.Builder;

@Builder
public class UserNotFoundException extends RuntimeException {

    String message;

    public UserNotFoundException(){
        super("User with given id is not found on server !!");
    }

    public UserNotFoundException(String message){
        super(message);

    }

}
