package com.hristijan.UserLogIn.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "token")
public class Token {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(updatable = false, nullable = false)
    public String tokenId;

    @OneToOne
    public User user;

    public LocalDateTime expiryDate;

    public static Token createTokenWithExpiryDate(User user)
    {
        Token token = new Token();
        token.user = user;
        token.expiryDate = LocalDateTime.now().plusHours(24);
        return token;
    }

    public boolean isExpired(){
        return LocalDateTime.now().isAfter(this.expiryDate);
    }


}
