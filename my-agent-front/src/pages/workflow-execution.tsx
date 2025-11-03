import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import {
  Layout,
  Table,
  Button,
  Input,
  Space,
  Typography,
  Toast,
  Tag,
  Popconfirm,
  Card,
  Modal,
} from "@douyinfe/semi-ui";
import {
  IconSearch,
  IconPlus,
  IconEyeOpened,
  IconEdit,
  IconDelete,
} from "@douyinfe/semi-icons";
import styled from "styled-components";
import { theme } from "../styles/theme";
import { Sidebar, Header } from "../components/layout";

import {
  WorkflowExecutionQuery,
  WorkflowExecutionDTO,
  PageDTO,
} from "../services/workflow-execution-service";
import { WorkflowExecutionService } from "../services/workflow-execution-service";
import useHandleNavigation from "../utils/useHandleNavigation";

const { Content } = Layout;
const { Title } = Typography;

// 样式组件
const AgentListLayout = styled(Layout)`
  min-height: 100vh;
  background: ${theme.colors.bg.secondary};
`;

const MainContent = styled.div<{ $collapsed: boolean }>`
  display: flex;
  flex: 1;
  margin-left: ${(props) =>
    props.$collapsed ? "80px" : "280px"}; /* 根据 Sidebar 状态调整左边距 */
  transition: margin-left ${theme.animation.duration.normal}
    ${theme.animation.easing.cubic};
`;

const ContentArea = styled(Content)`
  flex: 1;
  padding: ${theme.spacing.lg};
  background: ${theme.colors.bg.secondary};
  overflow-y: auto;
`;

const PageHeader = styled.div`
  margin-bottom: ${theme.spacing.lg};
`;

const SearchSection = styled(Card)`
  margin-bottom: ${theme.spacing.lg};

  .semi-card-body {
    padding: ${theme.spacing.lg};
  }
`;

const SearchRow = styled.div`
  display: flex;
  align-items: center;
  gap: ${theme.spacing.base};
  flex-wrap: wrap;
`;

const TableCard = styled(Card)`
  .semi-card-body {
    padding: 0;
  }
`;

const ActionButton = styled(Button)`
  margin-right: ${theme.spacing.sm};
`;

interface AgentListPageProps {
  selectedKey?: string;
  onMenuSelect?: (key: string) => void;
}

export const WorkflowExecutionPage: React.FC<AgentListPageProps> = ({
  selectedKey = "workflow-execution",
  onMenuSelect,
}) => {
  const navigate = useNavigate();
  const location = useLocation();
  const handleNavigation = useHandleNavigation();

  const [collapsed, setCollapsed] = useState(false);


  // 处理退出登录
  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("userInfo");
    localStorage.removeItem("isLoggedIn");
    navigate("/login");
  };
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<WorkflowExecutionDTO[]>([]);
  const [searchParams, setSearchParams] = useState<WorkflowExecutionQuery>({
    pageNum: 1,
    pageSize: 10,
  });
  const [total, setTotal] = useState(0);

  // 上下文弹窗状态
  const [contextModalVisible, setContextModalVisible] = useState(false);
  const [contextModalContent, setContextModalContent] = useState<string>("");
  const [contextModalIsJson, setContextModalIsJson] = useState<boolean>(false);
  const openContextModal = (record: WorkflowExecutionDTO) => {
    let content = record?.context || "";
    let isJson = false;
    try {
      const obj = JSON.parse(content);
      content = JSON.stringify(obj, null, 2);
      isJson = true;
    } catch {
      // 保留原始字符串
    }
    setContextModalIsJson(isJson);
    setContextModalContent(content);
    setContextModalVisible(true);
  };

  // 高亮展示 JSON 的 key
  const renderHighlightedJson = (jsonText: string) => {
    const lines = jsonText.split("\n");
    return (
      <div
        style={{
          whiteSpace: "pre-wrap",
          wordBreak: "break-word",
          fontFamily: "monospace",
          fontSize: 12,
        }}
      >
        {lines.map((line, idx) => {
          const match = line.match(/^(\s*)"([^"]+)"(\s*):\s*(.*)$/);
          if (match) {
            const [, indent, key, spaceBeforeColon, rest] = match;
            return (
              <div key={idx} style={{ whiteSpace: "pre" }}>
                <span>{indent}</span>
                <span style={{ color: "#c41d7f", fontWeight: 600 }}>
                  "{key}"
                </span>
                <span>
                  {spaceBeforeColon}: {rest}
                </span>
              </div>
            );
          }
          return (
            <div key={idx} style={{ whiteSpace: "pre" }}>
              {line}
            </div>
          );
        })}
      </div>
    );
  };

  // 表格列定义
  const columns = [
    {
      title: "执行ID",
      dataIndex: "executionId",
      key: "executionId",
      width: 80,
      render: (text: number) => (
        <span style={{ fontFamily: "monospace", fontSize: "12px" }}>
          {text}
        </span>
      ),
    },
    {
      title: "工作流名称",
      dataIndex: "workflowName",
      key: "workflowName",
      width: 120,
      render: (text: string) => <span style={{ fontWeight: 500 }}>{text}</span>,
    },
    {
      title: "上下文",
      dataIndex: "context",
      key: "context",
      width: 300,
      render: (text: string) => {
        if (!text) return "-";
        return text.length > 50 ? `${text.slice(0, 50)}...` : text;
      },
    },
    {
      title: "执行状态",
      dataIndex: "status",
      key: "status",
      width: 80,
      render: (status: string) => (
        <Tag
          color={status === "RUNNING" || status === "SUCCESS" ? "green" : "red"}
        >
          {status === "RUNNING"
            ? "运行中"
            : status === "SUCCESS"
            ? "已完成"
            : "已失败"}
        </Tag>
      ),
    },
    {
      title: "开始时间",
      dataIndex: "startTime",
      key: "startTime",
      width: 160,
      render: (time: string) => {
        if (!time) return "-";
        return new Date(time).toLocaleString("zh-CN");
      },
    },
    {
      title: "结束时间",
      dataIndex: "endTime",
      align: "center",
      key: "endTime",
      width: 160,
      render: (time: string) => {
        if (!time) return "-";
        return new Date(time).toLocaleString("zh-CN");
      },
    },
    {
      title: "操作",
      key: "action",
      width: 100,
      fixed: "right" as const,
      render: (_: any, record: WorkflowExecutionDTO) => (
        <Space>
          <ActionButton
            type="secondary"
            size="small"
            icon={<IconEyeOpened />}
            onClick={() => openContextModal(record)}
          >
            查看上下文
          </ActionButton>
          <Popconfirm
            title="确定要删除这个配置吗？"
            content="删除后无法恢复，请谨慎操作"
            onConfirm={() => handleDelete(record)}
          >
            <ActionButton type="danger" size="small" icon={<IconDelete />}>
              删除
            </ActionButton>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  // 加载数据
  const loadData = async (params?: WorkflowExecutionQuery) => {
    setLoading(true);
    try {
      const queryParams = { ...searchParams, ...params };
      const result: PageDTO<WorkflowExecutionDTO> | null =
        await WorkflowExecutionService.queryWorkflowExecutionList(queryParams);
      console.log("result====", result);
      if (!result) {
        setDataSource([]);
        setTotal(0);
        return;
      }
      setDataSource(result.records);
      setTotal(result?.total || 0);
    } catch (error) {
      Toast.error("加载数据失败");
      console.error("加载数据失败:", error);
    } finally {
      setLoading(false);
    }
  };

  // 搜索
  const handleSearch = () => {
    const params = { ...searchParams, pageNum: 1 };
    setSearchParams(params);
    loadData(params);
  };

  // 重置搜索
  const handleReset = () => {
    const params = { pageNum: 1, pageSize: 10 };
    setSearchParams(params);
    loadData(params);
  };

  // 分页变化
  const handlePageChange = (pageNum: number, pageSize: number) => {
    const params = { ...searchParams, pageNum, pageSize };
    setSearchParams(params);
    loadData(params);
  };

  // 删除
  const handleDelete = async (record: WorkflowExecutionDTO) => {
    try {
      setLoading(true);
      const success = await WorkflowExecutionService.deleteWorkflowExecution(
        record.executionId
      );
      if (success) {
        Toast.success(`成功删除执行记录: ${record.executionId}`);
        // 重新加载数据
        await loadData();
      } else {
        Toast.error("删除执行记录失败");
      }
    } catch (error) {
      Toast.error("删除执行记录失败");
      console.error("删除执行记录失败:", error);
    } finally {
      setLoading(false);
    }
  };

  // 初始化：支持通过 URL 参数预筛选（优先 workflowInstanceId，其次 workflowId/workflowName）
  useEffect(() => {
    const qs = new URLSearchParams(location.search);
    const instanceId = qs.get("instanceId") || undefined;
    const workflowName = qs.get("workflowName") || undefined;

    if (instanceId  || workflowName) {
      const params = {
        ...searchParams,
        instanceId: instanceId,
        workflowName,
        pageNum: 1,
      } as WorkflowExecutionQuery;
      setSearchParams(params);
      loadData(params);
    } else {
      loadData();
    }
    // 仅在首次渲染时执行一次
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <AgentListLayout>
      <Sidebar
        selectedKey={selectedKey}
        onSelect={handleNavigation}
        collapsed={collapsed}
      />
      <MainContent $collapsed={collapsed}>
        <div style={{ flex: 1, display: "flex", flexDirection: "column" }}>
          <Header
            onToggleSidebar={() => setCollapsed(!collapsed)}
            onLogout={handleLogout}
            collapsed={collapsed}
          />
          <ContentArea>
            <PageHeader>
              <Title heading={3}>工作流执行记录</Title>
            </PageHeader>

            <SearchSection>
              <SearchRow>
                <Input
                  placeholder="请输入工作流名称"
                  value={searchParams.workflowName || ""}
                  onChange={(value) =>
                    setSearchParams((prev) => ({
                      ...prev,
                      workflowName: value,
                    }))
                  }
                  style={{ width: 200 }}
                  prefix={<IconSearch />}
                />
                <Button type="primary" onClick={handleSearch} loading={loading}>
                  搜索
                </Button>
                <Button onClick={handleReset}>重置</Button>
                {/* <div style={{ marginLeft: "auto" }}>
                  <Button
                    type="primary"
                    icon={<IconPlus />}
                    onClick={handleCreate}
                  >
                    新建
                  </Button>
                </div> */}
              </SearchRow>
            </SearchSection>

          <TableCard>
            <Table
              columns={columns as any}
              dataSource={dataSource}
              loading={loading}
              pagination={{
                currentPage: searchParams.pageNum || 1,
                pageSize: searchParams.pageSize || 10,
                total: total,
                showSizeChanger: true,
                showQuickJumper: true,
                onChange: handlePageChange,
              }}
              rowKey="configId"
              scroll={{ x: 1200 }}
              size="middle"
            />
          </TableCard>

          <Modal
            title="执行上下文"
            visible={contextModalVisible}
            onCancel={() => setContextModalVisible(false)}
            width={800}
            style={{ maxWidth: '90vw' }}
            footer={
              <Button onClick={() => setContextModalVisible(false)}>
                关闭
              </Button>
            }
          >
            <div style={{ maxHeight: "60vh", overflow: "auto" }}>
              {contextModalIsJson
                ? renderHighlightedJson(contextModalContent)
                : (
                    <pre
                      style={{
                        whiteSpace: "pre-wrap",
                        wordBreak: "break-word",
                        fontSize: 12,
                        margin: 0,
                      }}
                    >
                      {contextModalContent || "无内容"}
                    </pre>
                  )}
            </div>
          </Modal>
        </ContentArea>
      </div>
    </MainContent>
  </AgentListLayout>
  );
};

export default WorkflowExecutionPage;
