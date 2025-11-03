package dev.szhuima.agent.domain.workflow.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * * @Author: szhuima
 * * @Date    2025/10/1 13:02
 * * @Description
 **/
@Data
public class WorkflowTriggerHttpConfigDO {

    private String url;
    private String method;
    private Map<String, String> headers;
    private List<Field> params;
    private List<Field> body;


    @Data
    public static class Field {
        private String key;
        private String type;
        private Boolean required;
        private String defaultValue;
    }

}
