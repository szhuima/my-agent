CREATE DATABASE `agent-station` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */

create table `agent-station`.tb_admin_user
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    user_id     varchar(64)                          not null comment '用户ID（唯一标识）',
    username    varchar(50)                          not null comment '用户名（登录账号）',
    password    varchar(128)                         not null comment '密码（加密存储）',
    status      tinyint(1) default 1                 null comment '状态(0:禁用,1:启用,2:锁定)',
    create_time datetime   default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_user_id
        unique (user_id)
)
    comment '管理员用户表' charset = utf8mb4;

create index idx_create_time
    on `agent-station`.tb_admin_user (create_time);

create index idx_status
    on `agent-station`.tb_admin_user (status);

create table `agent-station`.tb_agent
(
    id            bigint auto_increment comment '主键ID'
        primary key,
    tenant_id     varchar(32) default '10001'           not null,
    agent_name    varchar(50)                           not null comment '客户端名称',
    client_type   varchar(16) default 'DEFAULT'         not null comment '客户端类型',
    description   varchar(1024)                         null comment '描述',
    model_id      bigint                                not null comment '模型ID',
    system_prompt text                                  null comment '系统提示词',
    memory_size   int         default 0                 not null comment '记忆大小',
    return_format text                                  null comment '模型响应格式描述信息',
    status        tinyint(1)  default 1                 null comment '状态(0:禁用,1:启用)',
    create_time   datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    created_by    varchar(32) default '10001'           not null
)
    comment 'AI客户端配置表' charset = utf8mb4;

create table `agent-station`.tb_agent_knowledge_config
(
    id           bigint auto_increment
        primary key,
    tenant_id    varchar(32) not null,
    client_id    bigint      not null comment '客户端ID',
    knowledge_id bigint      not null comment '知识库ID'
)
    comment '智能体客户端和知识库关联表';

create table `agent-station`.tb_knowledge
(
    id            bigint auto_increment comment '主键ID'
        primary key,
    tenant_id     varchar(32) default '10001'           not null,
    rag_name      varchar(50)                           not null comment '知识库名称',
    knowledge_tag varchar(50)                           not null comment '知识标签',
    content       longtext                              not null comment '知识库内容',
    status        tinyint(1)  default 1                 null comment '状态(0:禁用,1:启用)',
    create_time   datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    created_by    varchar(32) default '10001'           not null,
    constraint uk_rag_name
        unique (rag_name)
)
    comment '知识库表' charset = utf8mb4;

create table `agent-station`.tb_mcp
(
    id               bigint auto_increment comment '主键ID'
        primary key,
    mcp_name         varchar(50)                           not null comment 'MCP名称',
    transport_type   varchar(20)                           not null comment '传输类型(sse/stdio)',
    transport_config varchar(1024)                         null comment '传输配置(sse/stdio)',
    request_timeout  int         default 180               null comment '请求超时时间(分钟)',
    status           tinyint(1)  default 1                 null comment '状态(0:禁用,1:启用)',
    create_time      datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_time      datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    created_by       varchar(32) default '10001'           not null,
    tenant_id        varchar(32) default '10001'           not null
)
    comment 'MCP客户端配置表' charset = utf8mb4;

create table `agent-station`.tb_model_api
(
    id               bigint auto_increment comment '主键ID'
        primary key,
    model_api_name   varchar(50)                                not null comment '模型API名称',
    base_url         varchar(255)                               not null comment '基础URL',
    api_key          varchar(255)                               not null comment 'API密钥',
    completions_path varchar(100) default 'v1/chat/completions' null comment '完成路径',
    embeddings_path  varchar(100) default 'v1/embeddings'       null comment '嵌入路径',
    model_source     varchar(50)                                not null comment '模型来源/厂商，比如 ollama openai 等',
    model_type       varchar(16)                                null comment '模型类型 embedding chat 等',
    model_name       varchar(50)  default 'gpt-4.1'             null comment '模型名称',
    timeout          int          default 180                   null comment '超时时间(秒)',
    status           tinyint(1)   default 1                     null comment '状态(0:禁用,1:启用)',
    create_time      datetime     default CURRENT_TIMESTAMP     null comment '创建时间',
    update_time      datetime     default CURRENT_TIMESTAMP     null on update CURRENT_TIMESTAMP comment '更新时间',
    created_by       varchar(32)  default '10001'               not null,
    tenant_id        varchar(32)  default '10001'               not null
)
    comment 'AI接口模型配置表' charset = utf8mb4;

create table `agent-station`.tb_workflow
(
    workflow_id bigint auto_increment
        primary key,
    name        varchar(255)                          not null,
    version     int         default 1                 not null comment '版本',
    meta_json   text                                  null comment '元数据json',
    description text                                  null,
    status      varchar(16) default '1'               null,
    created_at  timestamp   default CURRENT_TIMESTAMP null,
    updated_at  timestamp   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    created_by  varchar(32) default '10001'           not null,
    tenant_id   varchar(32) default '10001'           not null,
    constraint uniq_name
        unique (name asc, version desc)
)
    charset = utf8mb4;

create table `agent-station`.tb_workflow_dsl
(
    id          bigint auto_increment
        primary key,
    workflow_id bigint                                not null,
    version     int         default 0                 not null,
    content     text                                  null,
    create_time datetime    default CURRENT_TIMESTAMP null,
    created_by  varchar(32) default '10001'           not null,
    tenant_id   varchar(32) default '10001'           not null
);

create index workflow_id
    on `agent-station`.tb_workflow_dsl (workflow_id);

create table `agent-station`.tb_workflow_edge
(
    edge_id        bigint auto_increment
        primary key,
    workflow_id    bigint                                not null,
    from_node_id   bigint                                null,
    from_node_name varchar(32)                           null,
    to_node_id     bigint                                null,
    to_node_name   varchar(32)                           null,
    label          varchar(100)                          null comment '分支标签，例如 true/false 或 branchA，用于表示 CONDITION 节点的哪个输出',
    created_at     timestamp   default CURRENT_TIMESTAMP null,
    updated_at     timestamp   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    created_by     varchar(32) default '10001'           not null,
    tenant_id      varchar(32) default '10001'           not null
)
    charset = utf8mb4;

create index fk_edge_from_node
    on `agent-station`.tb_workflow_edge (from_node_id);

create index fk_edge_to_node
    on `agent-station`.tb_workflow_edge (to_node_id);

create index fk_edge_workflow
    on `agent-station`.tb_workflow_edge (workflow_id);

create table `agent-station`.tb_workflow_execution
(
    execution_id         bigint auto_increment
        primary key,
    workflow_name        varchar(32)                           null comment '工作流名称',
    workflow_instance_id bigint                                not null,
    status               varchar(32) default 'pending'         null comment '执行状态：PENDING / RUNNING / SUCCESS / FAILED',
    start_time           datetime                              null,
    end_time             datetime                              null,
    context              json                                  null comment '执行时上下文数据快照',
    error_message        text                                  null comment '执行异常信息',
    created_at           timestamp   default CURRENT_TIMESTAMP not null,
    updated_at           timestamp   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    created_by           varchar(32) default '10001'           not null,
    tenant_id            varchar(32) default '10001'           not null
)
    charset = utf8mb4;

create index idx_workflow_exec
    on `agent-station`.tb_workflow_execution (workflow_instance_id);

create table `agent-station`.tb_workflow_instance
(
    instance_id   bigint auto_increment
        primary key,
    workflow_id   bigint                                not null,
    workflow_name varchar(32)                           null,
    status        varchar(32)                           not null,
    created_at    timestamp   default CURRENT_TIMESTAMP null,
    updated_at    timestamp   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    created_by    varchar(32) default '10001'           not null,
    tenant_id     varchar(32) default '10001'           not null
);

create table `agent-station`.tb_workflow_node
(
    node_id        bigint auto_increment
        primary key,
    workflow_id    bigint                                not null,
    name           varchar(255)                          not null,
    type           varchar(100)                          not null comment '节点类型，例如 HTTP_CALL, AGENT,BATCH',
    trigger_type   varchar(16)                           null comment '触发器类型 ',
    start_node     tinyint     default 0                 not null comment '开始节点,1表示是开始节点',
    condition_expr varchar(128)                          null comment '断言条件表达式',
    config_id      bigint                                null comment '节点配置ID',
    config_json    mediumtext                            null comment '节点配置',
    position_x     int         default 0                 null,
    position_y     int         default 0                 null,
    created_at     timestamp   default CURRENT_TIMESTAMP null,
    updated_at     timestamp   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    created_by     varchar(32) default '10001'           not null,
    tenant_id      varchar(32) default '10001'           not null,
    constraint uniq_name
        unique (workflow_id, name)
)
    charset = utf8mb4;

create table `agent-station`.tb_workflow_node_execution
(
    node_execution_id     bigint auto_increment
        primary key,
    workflow_execution_id bigint                        not null,
    instance_id           bigint                        not null,
    node_id               int                           null,
    status                varchar(32) default 'RUNNING' not null,
    output                text                          null,
    error_msg             text                          null,
    start_time            datetime                      null,
    end_time              datetime                      null,
    created_by            varchar(32) default '10001'   not null,
    tenant_id             varchar(32) default '10001'   not null
);

