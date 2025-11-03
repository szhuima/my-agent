package dev.szhuima.agent.domain.agent.model;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 代理装配输入
 * * @Author: szhuima
 * * @Date    2025/9/11 17:51
 * * @Description
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgentAssemblyInput {

    /**
     * 指定装配的客户端ID列表
     */
    private List<Long> clientIdList;

    /**
     * 指定装配的处理类名称
     */
    private String assemblerName;


    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
