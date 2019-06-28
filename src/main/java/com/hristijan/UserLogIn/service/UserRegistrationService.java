package com.hristijan.UserLogIn.service;

import com.hristijan.UserLogIn.model.Exception.*;
import com.hristijan.UserLogIn.model.User;

import javax.mail.MessagingException;

public interface UserRegistrationService {


     User createUserWithPrimaryData(String email, String password, String firstName, String lastName) throws UserAlreadyExistsException, MessagingException;

    // User sendVerificationMailToUser(Integer userId, Token token);

     void verifyUserWithToken(String tokenId) throws TokenNotFoundException, UserNotFoundException, TokenIsExpiredException;

     void resendToken(String email) throws MessagingException, UserNotFoundException, TokenNotFoundException, TokenIsExpiredException;




}
