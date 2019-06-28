package com.hristijan.UserLogIn.service.Impl;

import com.hristijan.UserLogIn.repository.jpa.TokenRepository;
import com.hristijan.UserLogIn.repository.jpa.UserRepository;
import com.hristijan.UserLogIn.repository.mail.MailSenderRepository;
import com.hristijan.UserLogIn.model.Exception.*;
import com.hristijan.UserLogIn.model.Token;
import com.hristijan.UserLogIn.model.User;
import com.hristijan.UserLogIn.model.UserFactory;
import com.hristijan.UserLogIn.service.UserRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

@Transactional
@Service
public class UserRegistrationServiceImpl implements UserRegistrationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final MailSenderRepository mailSenderRepository;
    @Autowired
    public KafkaTemplate<String,User> kafkaTemplate;


    public UserRegistrationServiceImpl(UserRepository userRepository, TokenRepository tokenRepository, MailSenderRepository mailSenderRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.mailSenderRepository = mailSenderRepository;
    }

    @Override
    public User createUserWithPrimaryData(String email, String password, String firstName, String lastName) throws UserAlreadyExistsException, MessagingException {
        if (this.userRepository.findByEmail(email) == null) {
            User user = UserFactory.create(email,password,firstName,lastName);
            this.userRepository.save(user);

            Token token = Token.createTokenWithExpiryDate(user);
            this.tokenRepository.save(token);
            String body = "Your token id is: "+token.tokenId+"<br>Click <a href=\"http://localhost:8080/verifyUser/"+token.tokenId+"\">here</a> to activate your account <br>" +
                    "Or Register your account manually by clicking <a href=\"http://localhost:8080/tokenValidation.html\">here</a> ";
            mailSenderRepository.sendMail(email,"Token for account activation",body);
            return user;
        }
        throw new UserAlreadyExistsException();

    }


    @Override
    public void verifyUserWithToken(String tokenId) throws TokenNotFoundException, UserNotFoundException, TokenIsExpiredException {
        Token token = this.tokenRepository.findById(tokenId).orElseThrow(TokenNotFoundException::new);
        if(token.isExpired())
            throw new TokenIsExpiredException();
        User user = this.userRepository.findById(token.user.userId).orElseThrow(UserNotFoundException::new);
        user.activateUser();
        this.tokenRepository.delete(token);
        kafkaTemplate.send("USER_TOPIC", user);
    }

    @Override
    public void resendToken(String email) throws MessagingException, UserNotFoundException, TokenNotFoundException, TokenIsExpiredException {
        User user = this.userRepository.findByEmail(email);
        if(user == null)
           throw new UserNotFoundException();
        Token token = this.tokenRepository.findByUser(user);
        if(token == null)
            throw new TokenNotFoundException();
        if(token.isExpired())
            throw new TokenIsExpiredException();
        String body = "Your token id is: "+token.tokenId+"<br>Click <a href=\"http://localhost:8080/verifyUser/"+token.tokenId+"\">here</a> to activate your account <br>" +
        "Or Register your account manually by clicking <a href=\"http://localhost:8080/tokenValidation.html\">here</a> ";
        mailSenderRepository.sendMail(email,"Token for account activation",body);




    }


}
