import { FormRenderProps, FormMeta, ValidateTrigger } from '@flowgram.ai/free-layout-editor';
import { FlowNodeJSON } from '../../typings';
import { FormHeader, FormContent, FormInputs, FormOutputs } from '../../form-components';
import { LLMClientSelect } from './client-select';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON>) => (
  <>
    <FormHeader />
    <FormContent>
      {/* 模型客户端下拉 */}
      <LLMClientSelect />
      {/* 其余输入项按节点schema自动渲染 */}
      <FormInputs />
      <FormOutputs />
    </FormContent>
  </>
);

export const formMeta: FormMeta<FlowNodeJSON> = {
  render: renderForm,
  validateTrigger: ValidateTrigger.onChange,
  validate: {
    title: ({ value }: { value: string }) => (value ? undefined : 'Title is required'),
  },
};