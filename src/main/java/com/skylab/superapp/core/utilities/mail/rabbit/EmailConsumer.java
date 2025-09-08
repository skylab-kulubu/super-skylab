package com.skylab.superapp.core.utilities.mail.rabbit;

import com.skylab.superapp.core.config.RabbitMQConfig;
import com.skylab.superapp.core.utilities.mail.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class EmailConsumer {

    private final EmailService emailService;


    public EmailConsumer(EmailService emailService) {
        this.emailService = emailService;
    }



    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void processEmailMessage(@Payload EmailMessage emailMessage){
        try{
            boolean success = emailService.sendMail(
                    emailMessage.getTo(),
                    emailMessage.getSubject(),
                    emailMessage.getBody()
            );

            if (success){
                System.out.println("Email sent successfully to: " + emailMessage.getTo());
            } else {
                System.err.println("Failed to send email to: " + emailMessage.getTo());
            }
        }catch (Exception e){
            //will be logging these errors in the future
            System.out.println("Error processing email message: " + e.getMessage());
        }
    }
}
