package com.example.tutorial.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class MailServiceImpl implements MailService {
//    private JavaMailSender sender;
////    private MessageSource messages;
//    Configuration freeMarketConfig;

    @Override
    public void sendMail(String mailFrom, String mailTo, String subject, String message) {
        JavaMailSenderImpl sender = configMailSender();
        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
        try {
            messageHelper.setFrom(mailFrom);
            messageHelper.setTo(mailTo);
            messageHelper.setSubject(subject);
            messageHelper.setText(message);
            sender.send(messageHelper.getMimeMessage());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private JavaMailSenderImpl configMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("smtp.gmail.com");
        sender.setPort(587);
        sender.setUsername("mochacr0@gmail.com");
        sender.setPassword("dhyzxjmmdajmeaev");
        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        sender.setJavaMailProperties(props);
        return sender;
    }

}
