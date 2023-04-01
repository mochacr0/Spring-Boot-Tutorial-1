package com.example.tutorial.service;

public interface MailService {
    public void sendMail(String mailFrom, String mailTo, String subject, String message);
}
