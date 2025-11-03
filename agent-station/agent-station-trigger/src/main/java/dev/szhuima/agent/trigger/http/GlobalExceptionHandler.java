package dev.szhuima.agent.trigger.http;

import dev.szhuima.agent.api.Response;
import dev.szhuima.agent.api.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 捕获所有未处理的异常
    @ExceptionHandler(Exception.class)
    public Response handleException(Exception ex) {
        log.warn("捕获全局未知异常:{}", ex.getMessage());
        // 打印异常栈轨迹
        ex.printStackTrace();
        return Response.fail(ResponseCode.UN_ERROR);
    }


    // 捕获参数校验异常
    @ExceptionHandler(IllegalArgumentException.class)
    public Response handleValidationException(IllegalArgumentException ex) {
        log.warn("捕获全局非法参数异常:{}", ex.getMessage());
        // 打印异常栈轨迹
        ex.printStackTrace();
        return Response.fail(ResponseCode.ILLEGAL_PARAMETER, ex.getMessage());
    }
}
