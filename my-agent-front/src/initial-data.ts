import { FlowDocumentJSON } from './typings';

export const initialData: FlowDocumentJSON = {
  nodes: [
    {
      id: "start_0",
      type: "start",
      meta: {
        position: {
          x: -842,
          y: 39.5
        }
      },
      data: {
        title: "Start",
        outputs: {
          type: "object",
          required: []
        }
      }
    }
  ],
  edges: []
};
