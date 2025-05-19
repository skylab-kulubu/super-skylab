package com.skylab.superapp.core.utilities.mail;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class JavaMailSenderManager implements EmailService{

    private final JavaMailSender javaMailsender;

    public JavaMailSenderManager(JavaMailSender javaMailsender) {
        this.javaMailsender = javaMailsender;
    }

    @Value("${spring.mail.username}")
    private String from;


    @Override
    public boolean sendMail(String to, String subject, String body) {

        MimeMessage mimeMessage = javaMailsender.createMimeMessage();

        try{
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            javaMailsender.send(mimeMessage);
            return true;
        }catch (Exception e){
            // in the future we should be using custom exceptions like "throw new EmailSendUnsuccessfullException("Email could not be sent ", e);" for better code and easier bugfixes -yus
           return false;
        }


    }
}
