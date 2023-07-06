package com.springbook.learningtest.jdk.factoryBean;

public class Message {

    String text;

    private Message(String text) { // 생성자가 private으로 선언되어 있어서 외부에서 생성자를 통해 객체를 만들 수 없다.
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static Message newMessage(String text) {
        return new Message(text);
    }
}
