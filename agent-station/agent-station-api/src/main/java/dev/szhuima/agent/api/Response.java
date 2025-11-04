package dev.szhuima.agent.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> implements Serializable {

    private static final long serialVersionUID = 7000723935764546321L;

    private String code;
    private String info;
    private T data;


    public static Response success(Object data) {
        Response<Object> response = Response.builder()
                .code(ErrorCode.SUCCESS.getCode())
                .data(data)
                .build();
        return response;
    }

    public static Response illegalParameter(String info) {
        return Response.builder()
                .code(ErrorCode.BIZ_ERROR.getCode())
                .info(info)
                .data("")
                .build();
    }

    public static Response fail(ErrorCode code) {
        Response<Object> response = Response.builder()
                .code(code.getCode())
                .info(code.getInfo())
                .build();
        return response;
    }

    public static Response fail(ErrorCode code, String info) {
        Response<Object> response = Response.builder()
                .code(code.getCode())
                .info(info)
                .build();
        return response;
    }
}
