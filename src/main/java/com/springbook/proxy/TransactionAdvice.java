package com.springbook.proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TransactionAdvice implements MethodInterceptor {
    // -> 스프링의 어드바이스 인터페이스 구현

    PlatformTransactionManager transactionManager;

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // -> target을 호출하는 기능을 가진 콜백 객체를 프록시로부터 받는다.
        //    덕분에 어드바이스는 특정 target에 의존하지 않고 재사용 가능하다.
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            // 콜백을 호출해서 target의 메서드를 실행한다. target 메서드 호출 전후로 필요한 부가기능을 넣을 수 있다.
            // 경우에 따라서 target이 아예 호출되지 않게 하거나 재시도를 위한 반복적인 호출도 가능하다.
            Object ret = invocation.proceed();
            this.transactionManager.commit(status);
            return ret;
        } catch (RuntimeException e) {
            // JDK 다이나믹 프록시가 제공하는 Method와는 달리
            // 스프링의 MethodInvocation을 통한 target 호출은 예외가 포장되지 않고 target에서 보낸 그대로 전달된다.
            this.transactionManager.rollback(status);
            throw e;
        }
    }

}
