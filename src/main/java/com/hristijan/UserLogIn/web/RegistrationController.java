package com.hristijan.UserLogIn.web;

import com.hristijan.UserLogIn.model.Exception.TokenIsExpiredException;
import com.hristijan.UserLogIn.model.Exception.TokenNotFoundException;
import com.hristijan.UserLogIn.model.Exception.UserAlreadyExistsException;
import com.hristijan.UserLogIn.model.Exception.UserNotFoundException;
import com.hristijan.UserLogIn.repository.mail.MailSenderRepository;
import com.hristijan.UserLogIn.model.User;
import com.hristijan.UserLogIn.service.UserRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class RegistrationController {
    //public MailSenderRepository sendVerificationToUser;
    public final UserRegistrationService userRegistrationService;



    RegistrationController(UserRegistrationService userRegistrationService)
    {
        this.userRegistrationService=userRegistrationService;
        //this.sendVerificationToUser=sendVerificationToUser;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public String registerUser(@RequestParam("email") String email, @RequestParam("password") String password, @RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName) throws UserAlreadyExistsException, MessagingException
    {

        userRegistrationService.createUserWithPrimaryData(email,password,firstName,lastName);


        return "Confirmation e-mail sent";
    }

    @RequestMapping(value = "/verifyUser/{tokenId}", method = RequestMethod.GET)
    public String verifyUser(@PathVariable("tokenId") String tokenId) throws UserNotFoundException, TokenIsExpiredException, TokenNotFoundException {

            userRegistrationService.verifyUserWithToken(tokenId);
            return "Verified";
    }

    @RequestMapping(value = "/manuallyVerifyUser", method = RequestMethod.POST)
    public String manuallyVerifyUser(@RequestParam("token") String token) throws UserNotFoundException, TokenNotFoundException, TokenIsExpiredException {
        try {
            userRegistrationService.verifyUserWithToken(token);
        }catch (Exception e)
        {
            return "Problem verifying the token";
        }
        return "Verified";
    }

    @RequestMapping(value = "/resendToken", method = RequestMethod.POST)
    public String resendToken(@RequestParam("email") String email) throws MessagingException, TokenIsExpiredException, TokenNotFoundException, UserNotFoundException {
        userRegistrationService.resendToken(email);
        return "Token sent again";
    }




    @RequestMapping(value = "/authorizedUser", method = RequestMethod.GET)
    public String authorizedUser(){
        return "You are authorized to see this";

    }








}
