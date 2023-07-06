package com.springbook.user.service;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.util.ArrayList;
import java.util.List;

public class MockMailSender implements MailSender {

    String host;
    public void setHost(String host) {
        this.host = host;
    }

    // UserService로부터 전송 요청을 받은 메일 주소를 저장해두고 이를 읽을 수 있게 한다.
    private List<String> requests = new ArrayList<>();
    public List<String> getRequests() {
        return requests;
    }

    @Override
    public void send(SimpleMailMessage simpleMessage) throws MailException {
        requests.add(simpleMessage.getTo()[0]); // -> 전송 요청을 받은 이메일 주소를 저장해둔다. 간단하게 첫번째 수신자 메일 주소만 저장.
    }

    @Override
    public void send(SimpleMailMessage... simpleMessages) throws MailException {
    }
}
