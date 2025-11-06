package dev.szhuima.agent.domain.knowledge.repository;

import dev.szhuima.agent.domain.agent.model.Knowledge;

import java.util.List;

/**
 * * @Author: szhuima
 * * @Date    2025/11/4 14:16
 * * @Description
 **/
public interface IKnowledgeRepository {

    Long saveKnowledge(Knowledge knowledge);

    Knowledge queryKnowledge(Long knowledgeId);

    List<Knowledge> queryKnowledgeList(List<Long> knowledgeIds);

    List<Knowledge> queryByAgentId(Long agentId);
}
