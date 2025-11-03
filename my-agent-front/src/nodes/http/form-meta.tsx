import React from 'react';

import {
  Field,
  FieldRenderProps,
  FormRenderProps,
  FormMeta,
  ValidateTrigger,
} from '@flowgram.ai/free-layout-editor';
import { Input, Select, Button, TextArea, InputNumber } from '@douyinfe/semi-ui';
import { IconPlus, IconClose } from '@douyinfe/semi-icons';

import { FlowNodeJSON, FlowLiteralValueSchema, FlowRefValueSchema } from '../../typings';
import { useIsSidebar } from '../../hooks';
import { FormHeader, FormContent, FormItem, Feedback } from '../../form-components';
import { VariableSelector } from '@flowgram.ai/form-materials';
import { HttpPort } from './styles';
const { Option } = Select;

/**
 * Key-Value 列表编辑器，支持值为字符串或表达式
 */
function KeyValueListEditor({
  value,
  onChange,
  readonly,
}: {
  value?: { key?: string; value?: FlowLiteralValueSchema | FlowRefValueSchema }[];
  onChange: (v: { key?: string; value?: FlowLiteralValueSchema | FlowRefValueSchema }[]) => void;
  readonly?: boolean;
}) {
  const list = Array.isArray(value) ? value : [];
  const updateItem = (
    idx: number,
    item: { key?: string; value?: FlowLiteralValueSchema | FlowRefValueSchema }
  ) => {
    const newList = [...list];
    newList[idx] = item;
    onChange(newList);
  };
  const addItem = () => onChange([...list, { key: '', value: '' }]);
  const removeItem = (idx: number) => {
    const newList = list.filter((_, i) => i !== idx);
    onChange(newList);
  };
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
      {list.map((item, idx) => {
        const isExpression = typeof item?.value === 'object' && item?.value?.type === 'expression';
        return (
          <div key={idx} style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
            <Input
              placeholder="键"
              value={item?.key || ''}
              onChange={(v) => updateItem(idx, { ...item, key: v || '' })}
              disabled={readonly}
              style={{ width: 160 }}
            />
            {isExpression ? (
              <VariableSelector
                value={(item.value as FlowRefValueSchema)?.content}
                onChange={(v) =>
                  updateItem(idx, {
                    ...item,
                    value: { type: 'expression', content: v || '' },
                  })
                }
                readonly={readonly}
                style={{ flexGrow: 1 }}
              />
            ) : (
              <Input
                placeholder="值"
                value={(item?.value as string) || ''}
                onChange={(v) => updateItem(idx, { ...item, value: v ?? '' })}
                disabled={readonly}
                style={{ flexGrow: 1 }}
              />
            )}
            {!readonly && (
              <Button
                theme="borderless"
                icon={<IconClose />}
                onClick={() => removeItem(idx)}
              />
            )}
          </div>
        );
      })}
      {!readonly && (
        <div>
          <Button theme="borderless" icon={<IconPlus />} onClick={addItem}>
            添加
          </Button>
        </div>
      )}
    </div>
  );
}

/**
 * 文本区+表达式编辑器（用于请求体）
 */
function BodyEditor({
  value,
  onChange,
  readonly,
}: {
  value?: FlowLiteralValueSchema | FlowRefValueSchema;
  onChange: (v: FlowLiteralValueSchema | FlowRefValueSchema) => void;
  readonly?: boolean;
}) {
  const isExpression = typeof value === 'object' && (value as FlowRefValueSchema)?.type === 'expression';
  if (isExpression) {
    return (
      <VariableSelector
        value={(value as FlowRefValueSchema)?.content}
        onChange={(v) => onChange({ type: 'expression', content: v || '' })}
        readonly={readonly}
        style={{ width: '100%' }}
      />
    );
  }
  return (
    <TextArea
      rows={4}
      placeholder="请输入请求体（JSON或文本）"
      value={(value as string) || ''}
      onChange={(v) => onChange(v ?? '')}
      disabled={readonly}
    />
  );
}

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON>) => {
  const isSidebar = useIsSidebar();
  return (
    <>
      <FormHeader />
      <FormContent>
        {/* URL */}
        <Field name="inputsValues.url">
          {({ field, fieldState }: FieldRenderProps<string>) => (
            <FormItem name="接口URL" type="string" required>
              <Input
                placeholder="https://api.example.com/path"
                value={field.value || ''}
                onChange={(v) => field.onChange(v)}
                disabled={!isSidebar}
              />
              <Feedback errors={fieldState?.errors} />
            </FormItem>
          )}
        </Field>

        {/* Method */}
        <Field name="inputsValues.method" defaultValue={'GET'}>
          {({ field, fieldState }: FieldRenderProps<string>) => (
            <FormItem name="请求方法" type="string" required>
              <Select
                value={field.value || 'GET'}
                onChange={(v) => field.onChange(v as string)}
                disabled={!isSidebar}
                style={{ width: 160 }}
              >
                <Option value="GET">GET</Option>
                <Option value="POST">POST</Option>
                <Option value="PUT">PUT</Option>
                <Option value="DELETE">DELETE</Option>
                <Option value="PATCH">PATCH</Option>
                <Option value="HEAD">HEAD</Option>
                <Option value="OPTIONS">OPTIONS</Option>
              </Select>
              <Feedback errors={fieldState?.errors} />
            </FormItem>
          )}
        </Field>

        {/* Params */}
        <Field name="inputsValues.params">
          {({ field, fieldState }: FieldRenderProps<any[]>) => (
            <FormItem name="请求参数" type="array">
              <KeyValueListEditor
                value={field.value as any[]}
                onChange={(v) => (field.onChange as unknown as (value: any) => void)(v as any[])}
                readonly={!isSidebar}
              />
              <Feedback errors={fieldState?.errors} />
            </FormItem>
          )}
        </Field>

        {/* Headers */}
        <Field name="inputsValues.headers">
          {({ field, fieldState }: FieldRenderProps<any[]>) => (
            <FormItem name="请求头" type="array">
              <KeyValueListEditor
                value={field.value as any[]}
                onChange={(v) => (field.onChange as unknown as (value: any) => void)(v as any[])}
                readonly={!isSidebar}
              />
              <Feedback errors={fieldState?.errors} />
            </FormItem>
          )}
        </Field>

        {/* Body */}
        <Field name="inputsValues.body">
          {({ field, fieldState }: FieldRenderProps<FlowLiteralValueSchema | FlowRefValueSchema>) => (
            <FormItem name="请求体" type="string">
              <BodyEditor
                value={field.value}
                onChange={(v) => (field.onChange as unknown as (value: any) => void)(v as any)}
                readonly={!isSidebar}
              />
              <Feedback errors={fieldState?.errors} />
              {/* 输出端口锚点：允许从HTTP节点右侧发起连线 */}
              <HttpPort data-port-id="response" data-port-type="output" />
            </FormItem>
          )}
        </Field>

        {/* Timeout Seconds */}
        <Field<number> name="inputsValues.timeoutSeconds" defaultValue={10}>
          {({ field, fieldState }: FieldRenderProps<number>) => (
            <FormItem name="超时时间(秒)" type="integer">
              <InputNumber
                value={typeof field.value === 'number' ? field.value : 10}
                onChange={(v) => {
                  const num = typeof v === 'number' ? v : Number(v);
                  field.onChange(num);
                }}
                disabled={!isSidebar}
                style={{ width: 160 }}
                placeholder="超时时间(秒)"
                min={0}
                step={1}
              />
              <Feedback errors={fieldState?.errors} />
            </FormItem>
          )}
        </Field>
      </FormContent>
    </>
  );
};

export const formMeta: FormMeta<FlowNodeJSON> = {
  render: renderForm,
  validateTrigger: ValidateTrigger.onChange,
  validate: {
    title: ({ value }: { value: string }) => (value ? undefined : 'Title is required'),
    'inputsValues.url': ({ value }: { value: string }) => (value ? undefined : 'URL is required'),
    'inputsValues.method': ({ value }: { value: string }) => (value ? undefined : 'Method is required'),
  },
};