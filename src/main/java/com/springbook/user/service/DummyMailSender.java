package com.springbook.user.service;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class DummyMailSender implements MailSender {

    String host;

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public void send(SimpleMailMessage simpleMessage) throws MailException {
        System.out.printf("******메일 발송 기능 테스트******\nDummyMailSender send() 메서드 동작 확인\nhost: %s\n", this.host);
    }

    @Override
    public void send(SimpleMailMessage... simpleMessages) throws MailException {
    }
}
