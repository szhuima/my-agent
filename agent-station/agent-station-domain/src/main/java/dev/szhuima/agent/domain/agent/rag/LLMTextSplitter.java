package dev.szhuima.agent.domain.agent.rag;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import dev.szhuima.agent.domain.agent.model.valobj.AiClientModelVO;
import dev.szhuima.agent.domain.agent.repository.IClientModelRepository;
import dev.szhuima.agent.domain.agent.service.config.factory.AgentBeanFactory;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("llmTextSplitter")
public class LLMTextSplitter extends TextSplitter {

    @Resource
    private IClientModelRepository modelRepository;

    @Value("${agent-station.rag.text-splitter-model-id}")
    private Long textSplitterModelId;

    @Resource
    private AgentBeanFactory agentBeanFactory;

    @Override
    protected List<String> splitText(String text) {
        // 构造提示词
        String prompt = """
                你是一个文档智能切分助手，请将以下文本切分为语义完整的段落。
                  
                  要求：
                  1. 每个段落（chunk）保持语义完整，尽量不要把一句话或逻辑拆散。
                  2. 每个段落至少包含3句话，最多不超过150个词。
                  3. 输出 JSON 数组，每个元素包含：
                     {
                         "content": "段落内容文本"
                     }
                  4. title 必须简洁，能概括段落核心意思。
                  5. 输出内容必须为 **严格 JSON 格式**，不能有多余文字。
                  6. 如果原文有明显章节或小标题，可保留为 title，否则自动生成。
                  7. 输出语言请使用中文（或英文，根据需求替换）。
                  
                  原始文档如下：
                  %s
                """.formatted(text);
        AiClientModelVO modelVO = modelRepository.getClientModelById(textSplitterModelId);
        ChatModel chatModel = agentBeanFactory.createChatModel(modelVO);
        String result = chatModel.call(prompt);
        JSONArray array = JSON.parseArray(result);
        List<String> contents = array.stream()
                .map((obj) -> ((JSONObject) obj).getString("content"))
                .toList();
        return contents;
    }
}
