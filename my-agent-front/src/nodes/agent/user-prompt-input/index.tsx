import React from 'react';
import { Field } from '@flowgram.ai/free-layout-editor';
import { TextArea } from '@douyinfe/semi-ui';

import { FormItem, Feedback } from '../../../form-components';
import { useIsSidebar } from '../../../hooks';
import { AgentPort } from '../agent-select/styles';

interface UserPromptValue {
  key: string;
  value: string;
}

export function UserPromptInput() {
  const readonly = !useIsSidebar();

  return (
    <Field<UserPromptValue> name="inputsValues.userPrompt.0">
      {({ field, fieldState }) => (
        <FormItem name="用户提示词" type="string">
          <TextArea
            value={field.value?.value || ''}
            onChange={(value) => {
              field.onChange({ key: field.value?.key || '', value: value || '' });
            }}
            disabled={readonly}
            style={{ width: '100%', minHeight: '120px' }}
            placeholder="请输入用户提示词（可选）"
            rows={6}
            autosize={{ minRows: 6, maxRows: 12 }}
          />
          <Feedback errors={fieldState?.errors} />
          {/* 输出端口锚点：允许从智能体节点右侧发起连线 */}
          <AgentPort data-port-id="agent_output" data-port-type="output" />
        </FormItem>
      )}
    </Field>
  );
}