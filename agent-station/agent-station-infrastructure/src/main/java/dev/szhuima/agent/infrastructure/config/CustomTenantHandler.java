package dev.szhuima.agent.infrastructure.config;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import dev.szhuima.agent.infrastructure.util.UserContext;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomTenantHandler implements TenantLineHandler {

    private static final List<String> ignoreTables = List.of("admin_user", "ai_agent");

    @Override
    public Expression getTenantId() {
        String tenantId = UserContext.getCurrentUserId();
        return new StringValue(tenantId);
    }

    @Override
    public String getTenantIdColumn() {
        return "tenant_id";
    }

    @Override
    public boolean ignoreTable(String tableName) {
        // 根据需要返回是否忽略该表
        return ignoreTables.contains(tableName);
    }

}