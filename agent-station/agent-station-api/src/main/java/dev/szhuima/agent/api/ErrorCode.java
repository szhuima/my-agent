package dev.szhuima.agent.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ErrorCode {

    SUCCESS("0000", "成功"),
    UN_ERROR("0001", "未知失败"),
    BIZ_ERROR("0002", "业务失败"),
    LOGIN_FAILED("0003", "登录失败"),
    ;

    private String code;
    private String info;

}
