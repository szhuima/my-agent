package dev.szhuima.agent.infrastructure.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import dev.szhuima.agent.infrastructure.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

// java example
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        String currentUserId = UserContext.getCurrentUserId();
        log.info("插入SQL自动填充...");
        this.strictInsertFill(metaObject, "createdBy", String.class, currentUserId);
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("开始更新填充...");
//        this.strictInsertFill(metaObject, "updateUserId", Long.class, 123456L);
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}