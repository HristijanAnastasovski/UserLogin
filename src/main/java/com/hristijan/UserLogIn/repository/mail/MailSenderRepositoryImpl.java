package com.hristijan.UserLogIn.repository.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Repository
public class MailSenderRepositoryImpl implements  MailSenderRepository{

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    public String sender;

    public MailSenderRepositoryImpl(JavaMailSender jms)
    {
        this.mailSender=jms;
    }

    @Override
    @Async
    public void sendMail(String to, String subject, String body) throws MessagingException {
       /* SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(to);
        mail.setFrom(sender);
        mail.setSubject(subject);
        mail.setText(body);


        mailSender.send(mail);*/

        MimeMessage mimeMsg = mailSender.createMimeMessage();
        //MimeMessageHelper msgHelper = new MimeMessageHelper(mimeMsg, false, "utf-8");
        //msgHelper.setText(body, true);
        mimeMsg.setSubject(subject);
        mimeMsg.setRecipients(Message.RecipientType.TO,to);
        mimeMsg.setContent(body, "text/html; charset=utf-8");
        mimeMsg.saveChanges();
        mailSender.send(mimeMsg);
    }
}
