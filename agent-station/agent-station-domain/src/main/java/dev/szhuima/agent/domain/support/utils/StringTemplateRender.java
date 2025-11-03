package dev.szhuima.agent.domain.support.utils;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import io.micrometer.common.util.StringUtils;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

/**
 * * @Author: szhuima
 * * @Date    2025/9/25 17:05
 * * @Description
 **/
public interface StringTemplateRender {

     MustacheFactory mf = new DefaultMustacheFactory();

    /**
     * 使用 Mustache 渲染模板字符串
     *
     * @param template 模板内容，例如 "Hello {{name}}"
     * @param context  上下文数据，例如 Map.of("name", "Jobs")
     * @return 渲染后的字符串
     */
    default String render(String template, Map<String, Object> context, String rootKey) {
        if (template == null || template.isEmpty()) {
            return template;
        }
        Object scope;
        if (StringUtils.isNotEmpty(rootKey)) {
            scope = context.get(rootKey);
        } else {
            scope = context;
        }
        Reader reader = new StringReader(template);
        Mustache mustache = mf.compile(reader, "template");
        StringWriter writer = new StringWriter();
        mustache.execute(writer, scope);
        return writer.toString();
    }


    /**
     * 使用 Mustache 渲染模板字符串
     *
     * @param template 模板内容，例如 "Hello {{name}}"
     * @param scope   上下文数据，例如 Map.of("name", "Jobs")
     * @return 渲染后的字符串
     */
    default String render(String template, Object scope) {
        if (template == null || template.isEmpty()) {
            return template;
        }
        Reader reader = new StringReader(template);
        Mustache mustache = mf.compile(reader, "template");
        StringWriter writer = new StringWriter();
        mustache.execute(writer, scope);
        return writer.toString();
    }

}
