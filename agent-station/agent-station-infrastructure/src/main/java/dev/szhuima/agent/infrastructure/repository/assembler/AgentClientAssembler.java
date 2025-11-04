//package dev.szhuima.agent.infrastructure.repository.assembler;
//
//import cn.hutool.core.collection.CollectionUtil;
//import cn.hutool.core.util.StrUtil;
//import dev.szhuima.agent.domain.agent.Agent;
//import dev.szhuima.agent.domain.agent.model.AgentAssemblyInput;
//import dev.szhuima.agent.domain.agent.model.Knowledge;
//import dev.szhuima.agent.infrastructure.repository.assembler.advisor.KnowledgeAnswerAdvisor;
//import dev.szhuima.agent.infrastructure.design.chain.ChainContext;
//import dev.szhuima.agent.infrastructure.design.chain.HandleResult;
//import io.modelcontextprotocol.client.McpSyncClient;
//import jakarta.annotation.Resource;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
//import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
//import org.springframework.ai.chat.client.advisor.api.Advisor;
//import org.springframework.ai.chat.memory.MessageWindowChatMemory;
//import org.springframework.ai.chat.model.ChatModel;
//import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
//import org.springframework.ai.vectorstore.SearchRequest;
//import org.springframework.ai.vectorstore.VectorStore;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * * @Author: szhuima
// * * @Date    2025/9/12 15:31
// * * @Description
// **/
//@Slf4j
//@Component
//public class AgentClientAssembler extends AbstractAgentAssembler {
//
//    @Resource
//    private VectorStore vectorStore;
//
//    @Override
//    public HandleResult<Void> doHandle(ChainContext ctx, AgentAssemblyInput param) {
//        List<Agent> agentList = ctx.getAs(AGENT_CLIENTS_KEY, List.class);
//
//        for (Agent agent : agentList) {
//            // 1. 预设话术
//            String defaultSystem = StrUtil.isEmpty(agent.getSystemPrompt()) ? "超级智能体" : agent.getSystemPrompt();
//
//            // 2. chatModel
////            ChatModel chatModel = agentBeanFactory.getModel(modelBeanId);
//
//            // 3. ToolCallbackProvider
////            List<McpSyncClient> mcpSyncClients = new ArrayList<>();
////            List<String> mcpBeanIdList = agent.getMcpIdList();
////            for (String mcpBeanId : mcpBeanIdList) {
////                McpSyncClient mcpSyncClient = agentBeanFactory.getMcpSyncClient(mcpBeanId);
////                mcpSyncClients.add(mcpSyncClient);
////            }
//
//            // 4. Advisor
//            List<Advisor> advisors = new ArrayList<>();
//
//            // 4.1 内存Advisor
//            if (agent.getMemorySize() > 0) {
//                MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
//                        .maxMessages(agent.getMemorySize()).build();
//                PromptChatMemoryAdvisor memoryAdvisor = PromptChatMemoryAdvisor.builder(chatMemory).build();
//                advisors.add(memoryAdvisor);
//                // 把 ChatMemory 放入到 Spring 容器中
//                agentBeanFactory.registerChatMemory(agent.getId(), chatMemory);
//            }
//
//            // 配置RAG顾问
//            List<Knowledge> knowledgeList = agent.getKnowledgeList();
//            if (CollectionUtil.isNotEmpty(knowledgeList)) {
//                for (Knowledge knowledge : knowledgeList) {
//                    KnowledgeAnswerAdvisor answerAdvisor = new KnowledgeAnswerAdvisor(knowledge.getId(), vectorStore, SearchRequest.builder()
//                            .topK(knowledge.getTopK())
//                            .build());
//                    advisors.add(answerAdvisor);
//                }
//            }
//
//            advisors.add(new SimpleLoggerAdvisor());
//            Advisor[] advisorArray = advisors.toArray(new Advisor[]{});
//
//            // 5. 构建对话客户端
//            ChatClient chatClient = ChatClient.builder(null)
//                    .defaultSystem(defaultSystem)
////                    .defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClients.toArray(new McpSyncClient[]{})))
//                    .defaultAdvisors(advisorArray)
//                    .build();
//
//            agentBeanFactory.registerChatClient(agent.getId(), chatClient);
//        }
//        return proceed(ctx, param, HandleResult.keepGoing());
//    }
//}
