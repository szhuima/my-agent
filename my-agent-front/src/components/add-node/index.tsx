import { useState, useCallback } from 'react';
import { Button, Modal, Space, Typography, Card } from '@douyinfe/semi-ui';
import { IconPlus } from '@douyinfe/semi-icons';
import {
  useService,
  WorkflowDocument,
  WorkflowSelectService,
  WorkflowNodeEntity,
} from '@flowgram.ai/free-layout-editor';
import { WorkflowNodeType } from '../../nodes';

export const AddNode = (props: { disabled: boolean }) => {
  const [visible, setVisible] = useState(false);
  const workflowDocument = useService(WorkflowDocument);
  const selectService = useService(WorkflowSelectService);

  const handleAdd = useCallback(
    (type: WorkflowNodeType) => {
      const node: WorkflowNodeEntity = workflowDocument.createWorkflowNodeByType(type, undefined, {} as any);
      if (node) {
        selectService.selectNode(node);
      }
      setVisible(false);
    },
    [workflowDocument, selectService]
  );

  return (
    <>
      <Button
        icon={<IconPlus />}
        color="highlight"
        style={{ backgroundColor: 'rgba(171,181,255,0.3)', borderRadius: '8px' }}
        disabled={props.disabled}
        onClick={() => setVisible(true)}
      >
        添加节点
      </Button>
      <Modal
        title="请选择节点类型"
        visible={visible}
        onCancel={() => setVisible(false)}
        footer={null}
      >
        <Space align="start" spacing={16} wrap>
          <div
            role="button"
            tabIndex={0}
            style={{ width: 200, cursor: 'pointer' }}
            onClick={() => handleAdd(WorkflowNodeType.Http)}
            onKeyDown={(e) => {
              if (e.key === 'Enter' || e.key === ' ') {
                handleAdd(WorkflowNodeType.Http);
              }
            }}
          >
            <Card style={{ width: '100%' }} title="HTTP 请求">
              <Typography.Text type="secondary">发起 HTTP 请求并返回响应</Typography.Text>
            </Card>
          </div>
          

          <div
            role="button"
            tabIndex={0}
            style={{ width: 200, cursor: 'pointer' }}
            onClick={() => handleAdd(WorkflowNodeType.Agent)}
            onKeyDown={(e) => {
              if (e.key === 'Enter' || e.key === ' ') {
                handleAdd(WorkflowNodeType.Agent);
              }
            }}
          >
            <Card style={{ width: '100%' }} title="智能体">
              <Typography.Text type="secondary">选择并配置智能体（Agent）</Typography.Text>
            </Card>
          </div>
        </Space>
      </Modal>
    </>
  );
};
