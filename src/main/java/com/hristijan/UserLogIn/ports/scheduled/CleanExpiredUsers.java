package com.hristijan.UserLogIn.ports.scheduled;

import com.hristijan.UserLogIn.model.Exception.UserNotFoundException;
import com.hristijan.UserLogIn.model.Token;
import com.hristijan.UserLogIn.model.User;
import com.hristijan.UserLogIn.repository.jpa.TokenRepository;
import com.hristijan.UserLogIn.repository.jpa.UserRepository;
import com.hristijan.UserLogIn.service.UserManagementService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class CleanExpiredUsers {
    private final UserRepository userRepository;
    private final UserManagementService userManagementService;
    private final TokenRepository tokenRepository;


    public CleanExpiredUsers(UserRepository userRepository, UserManagementService userManagementService, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.userManagementService = userManagementService;
        this.tokenRepository = tokenRepository;
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void cleanExpiredUsers(){
        List<Token> expiredTokens = this.tokenRepository.findByExpiryDateBefore(LocalDateTime.now());
        if(!expiredTokens.isEmpty()) {
            for (Token t :
                    expiredTokens) {
                try {
                    User u = this.userRepository.findById(t.user.userId).orElseThrow(UserNotFoundException::new);
                    this.tokenRepository.delete(t);
                    this.userManagementService.deleteUserWithId(u.userId);

                } catch (UserNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
