package dev.szhuima.agent.domain.support.exception;

public class BizException extends RuntimeException {
    private final String message;

    public BizException(String message) {
        super(message);
        this.message = message;
    }


    @Override
    public String getMessage() {
        return message;
    }

    public static BizException of(String message) {
        return new BizException(message);
    }

}
