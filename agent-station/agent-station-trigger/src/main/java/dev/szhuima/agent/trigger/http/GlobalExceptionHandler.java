package dev.szhuima.agent.trigger.http;

import dev.szhuima.agent.api.ErrorCode;
import dev.szhuima.agent.api.Response;
import dev.szhuima.agent.domain.support.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    // 捕获业务异常
    @ExceptionHandler(BizException.class)
    public Response handleBizException(BizException ex) {
        log.warn("捕获全局业务异常:{}", ex.getMessage());
        // 打印异常栈轨迹
        ex.printStackTrace();
        return Response.fail(ErrorCode.BIZ_ERROR, ex.getMessage());
    }

    // 捕获所有未处理的异常
    @ExceptionHandler(Exception.class)
    public Response handleException(Exception ex) {
        log.warn("捕获全局未知异常:{}", ex.getMessage());
        // 打印异常栈轨迹
        ex.printStackTrace();
        return Response.fail(ErrorCode.UN_ERROR);
    }
}
