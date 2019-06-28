package com.hristijan.UserLogIn.repository.jpa;

import com.hristijan.UserLogIn.model.Token;
import com.hristijan.UserLogIn.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TokenRepository extends JpaRepository<Token,String> {
    List<Token> findByExpiryDateBefore(LocalDateTime time);
    Token findByUser(User user);
}
