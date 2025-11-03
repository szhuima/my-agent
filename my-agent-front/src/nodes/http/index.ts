import { nanoid } from 'nanoid';

import { WorkflowNodeType } from '../constants';
import { FlowNodeRegistry } from '../../typings';
import iconClient from '../../assets/icon-client.jpg';
import { formMeta } from './form-meta';

let index = 0;
export const HttpNodeRegistry: FlowNodeRegistry = {
  type: WorkflowNodeType.Http,
  info: {
    icon: iconClient,
    description: 'HTTP 请求节点',
  },
  meta: {
    defaultPorts: [{ type: 'input' }],
    useDynamicPort: true,
    expandable: false,
  },
  formMeta,
  onAdd() {
    return {
      id: `http_${nanoid(5)}`,
      type: 'http',
      data: {
        title: `HTTP请求_${++index}`,
        inputsValues: {
          url: '',
          method: 'GET',
          params: [],
          headers: [],
          body: '',
          timeoutSeconds: 10,
        },
        inputs: {
          type: 'object',
          required: ['url', 'method'],
          properties: {
            url: { type: 'string' },
            method: { type: 'string' },
            params: {
              type: 'array',
              items: {
                type: 'object',
                properties: {
                  key: { type: 'string' },
                  value: { type: 'string' },
                },
              },
            },
            headers: {
              type: 'array',
              items: {
                type: 'object',
                properties: {
                  key: { type: 'string' },
                  value: { type: 'string' },
                },
              },
            },
            body: { type: 'string' },
            timeoutSeconds: { type: 'integer' },
          },
        },
        outputs: {
          type: 'object',
          properties: {
            response: { type: 'string' },
          },
        },
      },
    };
  },
};