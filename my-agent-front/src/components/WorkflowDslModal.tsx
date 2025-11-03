import React, { useState } from "react";
import { Modal, Toast } from "@douyinfe/semi-ui";
import MonacoEditor from "react-monaco-editor";
import * as monaco from "monaco-editor";
import yaml from "js-yaml";

interface WorkflowDslModalProps {
  visible: boolean;
  dslContent: string;
  editingWorkflow: any;
  submitting: boolean;
  onCancel: () => void;
  onOk: () => void;
  onChange: (value: string) => void;
}

const WorkflowDslModal: React.FC<WorkflowDslModalProps> = ({
  visible,
  dslContent,
  editingWorkflow,
  submitting,
  onCancel,
  onOk,
  onChange
}) => {
  const [yamlError, setYamlError] = useState<string>("");

  return (
    <Modal
      title={`编辑工作流定义配置${editingWorkflow?.name ? ` - ${editingWorkflow.name}` : ""}`}
      visible={visible}
      onCancel={onCancel}
      onOk={onOk}
      confirmLoading={submitting}
      okText="导入"
      cancelText="取消"
      width={800}
      maskClosable={false}
    >
      <div style={{ height: "60vh", display: "flex", flexDirection: "column", overflow: "auto" }}>
        <div style={{ flex: 1, minHeight: 0 }}>
          <MonacoEditor
            width="100%"
            height="100%"
            language="yaml"
            theme="yaml-key-highlight"
            value={dslContent}
            options={{
              fontSize: 14,
              minimap: { enabled: false },
              wordWrap: "on",
              automaticLayout: true,
            }}
            onChange={(value: string) => {
              onChange(value);
              try {
                yaml.load(value);
                setYamlError("");
              } catch (e: any) {
                setYamlError(e.message);
              }
            }}
          />
        </div>
        {yamlError && (
          <div style={{ color: "red", marginTop: 8 }}>
            YAML语法错误：{yamlError}
          </div>
        )}
      </div>
    </Modal>
  );
};

export default WorkflowDslModal;

// 定义自定义主题，突出 YAML key
if (monaco && monaco.editor) {
  monaco.editor.defineTheme("yaml-key-highlight", {
    base: "vs",
    inherit: true,
    rules: [
      { token: "key", foreground: "FFD700", fontStyle: "bold" }, // key 黄色加粗
      { token: "string", foreground: "A31515" }, // value 保持原色
      { token: "comment", foreground: "6A9955" }, // 注释绿色
      { token: "number", foreground: "098658" },
      { token: "keyword", foreground: "0000FF" },
      { token: "delimiter", foreground: "000000" },
    ],
    colors: {
      "editor.background": "#FFFFFF",
    },
  });
}

// 注册/增强 YAML 语言的 Monarch 词法规则，突出行首 key
if (monaco && monaco.languages) {
  try {
    const hasYaml = monaco.languages.getLanguages().some((l) => l.id === "yaml");
    if (!hasYaml) {
      monaco.languages.register({ id: "yaml" });
    }
    monaco.languages.setMonarchTokensProvider("yaml", {
      // 简化的 YAML 词法规则，确保行首的 "key:" 被识别为 key token
      tokenizer: {
        root: [
          [/#.*$/, "comment"],
          [/^(\s*-?\s*)([\w.-]+)(\s*)(:)/, ["white", "key", "white", "delimiter"]],
          [/"([^"\\]|\\.)*"/, "string"],
          [/'([^'\\]|\\.)*'/, "string"],
          [/\b(true|false|null)\b/, "keyword"],
          [/\b\d+(\.\d+)?\b/, "number"],
          [/[[\]{}.,]/, "delimiter.bracket"],
          [/:/, "delimiter"],
        ],
      },
    });
  } catch (e) {
    // noop
  }
}