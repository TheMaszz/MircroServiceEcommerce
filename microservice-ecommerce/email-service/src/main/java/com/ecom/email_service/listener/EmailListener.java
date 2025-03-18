package com.ecom.email_service.listener;

import com.ecom.common.bean.EmailRequest;
import com.ecom.email_service.service.EmailService;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class EmailListener {

    private final EmailService emailService;

    public EmailListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "reset-password")
    public void listenForResetPassword(EmailRequest request) {
        log.info("Kafka Received " + request.getTo());
        log.info(request.getContent());

        emailService.send(request.getTo(), request.getSubject(), request.getContent());
    }
}
