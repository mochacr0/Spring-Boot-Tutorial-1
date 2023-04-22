package com.example.tutorial.service;

public interface MailService {
    public void sendActivationMail(String mailTo, String activateLink);
    public void sendTemplateMail();
    public void sendMail(String mailFrom, String mailTo, String subject, String message);
    public void sendMailAsync(String mailFrom, String mailTo, String subject, String message);
}
