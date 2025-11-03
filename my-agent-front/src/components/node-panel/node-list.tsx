import React, { FC } from 'react';

import styled from 'styled-components';
import { NodePanelRenderProps } from '@flowgram.ai/free-node-panel-plugin';
import { useClientContext } from '@flowgram.ai/free-layout-editor';

import { FlowNodeRegistry } from '../../typings';
import { visibleNodeRegistries, WorkflowNodeType } from '../../nodes';

const NodeWrap = styled.div`
  width: 100%;
  height: 32px;
  border-radius: 5px;
  display: flex;
  align-items: center;
  cursor: pointer;
  font-size: 19px;
  padding: 0 15px;
  &:hover {
    background-color: hsl(252deg 62% 55% / 9%);
    color: hsl(252 62% 54.9%);
  }
`;

const NodeLabel = styled.div`
  font-size: 12px;
  margin-left: 10px;
`;

interface NodeProps {
  label: string;
  icon: JSX.Element;
  onClick: React.MouseEventHandler<HTMLDivElement>;
  disabled: boolean;
}

function Node(props: NodeProps) {
  return (
    <NodeWrap
      onClick={props.disabled ? undefined : props.onClick}
      style={props.disabled ? { opacity: 0.3 } : {}}
    >
      <div style={{ fontSize: 14 }}>{props.icon}</div>
      <NodeLabel>{props.label}</NodeLabel>
    </NodeWrap>
  );
}

const NodesWrap = styled.div`
  max-height: 500px;
  overflow: auto;
  &::-webkit-scrollbar {
    display: none;
  }
`;

interface NodeListProps {
  onSelect: NodePanelRenderProps['onSelect'];
}

export const NodeList: FC<NodeListProps> = (props) => {
  const { onSelect } = props;
  const context = useClientContext();
  const handleClick = (e: React.MouseEvent, registry: FlowNodeRegistry) => {
    const json = registry.onAdd?.(context);
    onSelect({
      nodeType: registry.type as string,
      selectEvent: e,
      nodeJSON: json,
    });
  };

  // 仅展示 HTTP 与 智能体 两类节点
  const allowedTypes = new Set<WorkflowNodeType>([
    WorkflowNodeType.Http,
    WorkflowNodeType.Agent,
  ]);
  // 优先显示 HTTP 和 智能体 节点，并保持稳定排序
  const priorityOrder = new Map<WorkflowNodeType, number>([
    [WorkflowNodeType.Http, 0],
    [WorkflowNodeType.Agent, 1],
  ]);
  const registries = visibleNodeRegistries
    .filter((r) => allowedTypes.has(r.type as WorkflowNodeType))
    .sort((a, b) => {
      const wa = priorityOrder.get(a.type as WorkflowNodeType) ?? 999;
      const wb = priorityOrder.get(b.type as WorkflowNodeType) ?? 999;
      if (wa !== wb) return wa - wb;
      return String(a.type).localeCompare(String(b.type));
    });

  const getLabel = (r: FlowNodeRegistry): string => {
    if ((r.type as WorkflowNodeType) === WorkflowNodeType.Http) return 'HTTP 请求';
    if ((r.type as WorkflowNodeType) === WorkflowNodeType.Agent) return '智能体';
    return r.info?.description ?? (r.type as string);
  };
  return (
    <NodesWrap style={{ width: 80 * 2 + 20 }}>
      {registries.map((registry) => (
        <Node
          key={registry.type}
          disabled={!(registry.canAdd?.(context) ?? true)}
          icon={
            <img style={{ width: 10, height: 10, borderRadius: 4 }} src={registry.info?.icon} />
          }
          label={getLabel(registry)}
          onClick={(e) => handleClick(e, registry)}
        />
      ))}
    </NodesWrap>
  );
};
