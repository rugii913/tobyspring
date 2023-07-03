package com.springbook.learningtest.template;

import java.io.BufferedReader;
import java.io.IOException;

public interface BufferedReaderCallback { // BufferedReader를 전달받는 콜백 인터페이스
    Integer doSomethingWithReader(BufferedReader br) throws IOException;
}
