package com.hristijan.UserLogIn.repository.mail;

import javax.mail.MessagingException;

public interface MailSenderRepository {
    public void sendMail(String to, String subject, String body) throws MessagingException;
}
