package dev.szhuima.agent.infrastructure.repository;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import dev.szhuima.agent.domain.agent.model.Knowledge;
import dev.szhuima.agent.domain.knowledge.repository.IKnowledgeRepository;
import dev.szhuima.agent.infrastructure.entity.TbAgentKnowledgeConfig;
import dev.szhuima.agent.infrastructure.entity.TbKnowledge;
import dev.szhuima.agent.infrastructure.mapper.AgentKnowledgeConfigMapper;
import dev.szhuima.agent.infrastructure.mapper.KnowledgeMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * * @Author: szhuima
 * * @Date    2025/11/4 14:17
 * * @Description
 **/
@Repository
public class KnowledgeRepository implements IKnowledgeRepository {

    @Resource
    private KnowledgeMapper knowledgeMapper;

    @Resource
    private AgentKnowledgeConfigMapper agentKnowledgeConfigMapper;

    @Override
    public Long saveKnowledge(Knowledge knowledge) {
        TbKnowledge tbKnowledge = new TbKnowledge();
        tbKnowledge.setRagName(knowledge.getRagName());
        tbKnowledge.setKnowledgeTag(knowledge.getKnowledgeTag());
        tbKnowledge.setContent(knowledge.getContent());
        tbKnowledge.setStatus(1);
        tbKnowledge.setCreateTime(LocalDateTime.now());
        tbKnowledge.setUpdateTime(LocalDateTime.now());
        knowledgeMapper.insert(tbKnowledge);
        return tbKnowledge.getId();
    }

    @Override
    public Knowledge queryKnowledge(Long knowledgeId) {
        TbKnowledge tbKnowledge = knowledgeMapper.selectById(knowledgeId);
        return BeanUtil.copyProperties(tbKnowledge, Knowledge.class);
    }

    @Override
    public List<Knowledge> queryKnowledgeList(List<Long> knowledgeIds) {
        List<TbKnowledge> tbKnowledgeList = knowledgeMapper.selectBatchIds(knowledgeIds);
        return BeanUtil.copyToList(tbKnowledgeList, Knowledge.class);
    }

    @Override
    public List<Knowledge> queryByAgentId(Long agentId) {
        List<Long> knowledgeIds = agentKnowledgeConfigMapper.selectList(Wrappers.lambdaQuery(TbAgentKnowledgeConfig.class)
                .eq(TbAgentKnowledgeConfig::getAgentId, agentId))
                .stream()
                .map(TbAgentKnowledgeConfig::getKnowledgeId)
                .toList();
        if (knowledgeIds.isEmpty()) {
            return Collections.emptyList();
        }
        return queryKnowledgeList(knowledgeIds);
    }
}
