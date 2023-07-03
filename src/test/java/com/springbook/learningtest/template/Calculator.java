package com.springbook.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {

     public Integer fileReadTemplate(String filepath, BufferedReaderCallback callback) throws IOException {
        // 이전의 calcSum과 거의 같다. 달라지는 부분만 체크
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(filepath));
            int ret = callback.doSomethingWithReader(br);
            // -> 콜백 오브젝트 호출, 템플릿에서 만든 컨텍스트 정보인 BufferedReader를 전달해주고 콜백의 작업 결과를 받아둔다.
            return ret;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        } finally {
            if (br != null) {
                try { br.close(); }
                catch (IOException e) { System.out.println(e.getMessage()); }
            }
        }
     }

    /* // 이전의 calcSum 코드
    public Integer calcSum(String filepath) throws IOException {

        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(filepath)); // 한 줄씩 읽기 편하게 BufferedReader로 파일을 가져온다.
            Integer sum = 0;
            String line = null;
            while ((line = br.readLine()) != null) { // 마지막라인까지 한 줄씩 읽어서 숫자를 더함 // readLine() 결과가 null이 아닐 때까지 반복
                sum += Integer.valueOf(line);
            }
            return sum;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        } finally {
            if (br != null) { // BufferedReader 오브젝트가 생성되기 전에 예외가 발생할 수도 있으므로 반드시 null 체크 먼저
                try { br.close(); }
                catch (IOException e) { System.out.println(e.getMessage()); }
            }
        }
    }
    */

    public Integer calcSum(String filepath) throws IOException {
        return fileReadTemplate(filepath, bufferedReader -> {
            Integer sum = 0;
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sum += Integer.valueOf(line);
            }
            return sum;
        });
    }
}
