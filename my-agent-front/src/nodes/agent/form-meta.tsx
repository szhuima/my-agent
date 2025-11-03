import { FormRenderProps, FormMeta, ValidateTrigger } from '@flowgram.ai/free-layout-editor';

import { FlowNodeJSON } from '../../typings';
import { FormHeader, FormContent } from '../../form-components';
import { AgentSelect } from './agent-select';
import { AgentClientSelect } from './client-select';
import { UserPromptInput } from './user-prompt-input';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON>) => (
  <>
    <FormHeader />
    <FormContent>
      {/* 智能体客户端选择 */}
      <AgentClientSelect />
      {/* 基本信息（名称/描述/渠道/策略） */}
      {/* <AgentSelect /> */}
      {/* 用户提示词 */}
      <UserPromptInput />
    </FormContent>
  </>
);

export const formMeta: FormMeta<FlowNodeJSON> = {
  render: renderForm,
  validateTrigger: ValidateTrigger.onChange,
  validate: {
    title: ({ value }: { value: string }) => (value ? undefined : 'Title is required'),
    'inputsValues.clientId': ({ value }) => {
      if (!value || String(value).trim() === '') return '智能体客户端是必选项';
      return undefined;
    },
    'inputsValues.agentName.*': ({ value }) => {
      if (!value?.value?.content) return 'Agent名称是必填项';
      return undefined;
    },
    'inputsValues.description.*': ({ value }) => {
      if (!value?.value?.content) return '描述是必填项';
      return undefined;
    },
    'inputsValues.channel': ({ value }) => {
      if (!value || value.trim() === '') return '渠道是必选项';
      return undefined;
    },
    'inputsValues.strategy': ({ value }) => {
      if (!value || value.trim() === '') return '策略是必选项';
      return undefined;
    },
  },
};
