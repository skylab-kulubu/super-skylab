package com.skylab.superapp.core.utilities.mail;

public interface EmailService {

    boolean sendMail(String to, String subject, String body);

    void sendEmailAsync(String to, String subject, String body);

}
