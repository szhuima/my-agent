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
  WorkflowInstanceQuery,
  WorkflowInstanceDTO,
  PageDTO,
} from "../services/workflow-instance-service";
import { WorkflowInstanceService } from "../services/workflow-instance-service";
import useHandleNavigation from "../utils/useHandleNavigation";

const { Content } = Layout;
const { Title } = Typography;

// 样式组件
const AgentListLayout = styled(Layout)`
  min-height: 100vh;
  background: ${theme.colors.bg.primary};
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
  background: ${theme.colors.bg.primary};
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

export const WorkflowInstancePage: React.FC<AgentListPageProps> = ({
  selectedKey = "workflow-instance",
  onMenuSelect,
}) => {
  const navigate = useNavigate();
  const location = useLocation();
  const [collapsed, setCollapsed] = useState(false);
  const handleNavigation = useHandleNavigation();


  // 处理退出登录
  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("userInfo");
    localStorage.removeItem("isLoggedIn");
    navigate("/login");
  };
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<WorkflowInstanceDTO[]>(
    []
  );
  const [searchParams, setSearchParams] = useState<WorkflowInstanceQuery>({
    pageNum: 1,
    pageSize: 10,
  });
  const [total, setTotal] = useState(0);

  // 测试弹窗状态
  const [testModalVisible, setTestModalVisible] = useState(false);
  const [testSubmitting, setTestSubmitting] = useState(false);
  const [testingInstance, setTestingInstance] = useState<WorkflowInstanceDTO | null>(null);
  const [testContextVars, setTestContextVars] = useState<Array<{ name: string; value: string }>>([
    { name: "", value: "" },
  ]);

  // 表格列定义
  const columns = [
    {
      title: "实例ID",
      dataIndex: "instanceId",
      key: "instanceId",
      width: 80,
      render: (text: string) => (
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
      title: "执行记录数",
      dataIndex: "executionCount",
      key: "executionCount",
      width: 100,
      render: (text: number, record: WorkflowInstanceDTO) => {
        const count = Number(text) || 0;
        if (count > 0) {
          const url = `/workflow-execution?instanceId=${encodeURIComponent(
            String(record.instanceId)
          )}`;
          return (
            <a
              href={url}
              style={{
                color: "#337ce3ff",
                textDecoration: "underline",
                fontWeight: 600,
                cursor: "pointer",
                fontFamily: "monospace",
                fontSize: "12px",
              }}
              onClick={(e) => {
                e.preventDefault();
                navigate(url);
              }}
              title="查看该工作流实例的执行记录"
            >
              {count}
            </a>
          );
        }
        return (
          <span
            style={{
              fontFamily: "monospace",
              fontSize: "12px",
              color: "rgba(0,0,0,0.45)",
            }}
          >
            {count}
          </span>
        );
      },
    }, //executionCount
    {
      title: "状态",
      dataIndex: "status",
      key: "status",
      width: 80,
      render: (status: string) => (
        <Tag color={status === "DEPLOYED" ? "green" : "red"}>
          {status === "DEPLOYED" ? "已部署" : "未部署"}
        </Tag>
      ),
    },
    {
      title: "部署时间",
      dataIndex: "createdAt",
      key: "createdAt",
      width: 160,
      render: (time: string) => {
        if (!time) return "-";
        return new Date(time).toLocaleString("zh-CN");
      },
    },
    {
      title: "操作",
      key: "action",
      width: 330,
      fixed: "right" as const,
      render: (_: any, record: WorkflowInstanceDTO) => (
        <Space>
          <ActionButton
            type={record.status === "DEPLOYED" ? "warn" : "primary"}
            size="small"
            onClick={() => toggleDeploy(record)}
          >
            {record.status === "DEPLOYED" ? "卸载" : "部署"}
          </ActionButton>
          <ActionButton
            type="secondary"
            size="small"
            onClick={() => openTestModal(record)}
          >
            测试
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
  const loadData = async (params?: WorkflowInstanceQuery) => {
    setLoading(true);
    try {
      const queryParams = { ...searchParams, ...params };
      const result: PageDTO<WorkflowInstanceDTO> | null =
        await WorkflowInstanceService.queryWorkflowInstanceList(queryParams);
      console.log("result====", result);
      if (!result) {
        setDataSource([]);
        setTotal(0);
        return;
      }
      setDataSource(result.records);
      // 注意：后端返回的是简单分页，这里设置一个估算的总数
      setTotal(result?.total || 0);
    } catch (error) {
      Toast.error("加载数据失败");
      console.error("加载数据失败:", error);
    } finally {
      setLoading(false);
    }
  };

  // 打开测试弹窗
  const openTestModal = (record: WorkflowInstanceDTO) => {
    setTestingInstance(record);
    setTestContextVars([{ name: "", value: "" }]);
    setTestModalVisible(true);
  };

  const handleTestCancel = () => {
    setTestModalVisible(false);
    setTestingInstance(null);
    setTestSubmitting(false);
  };

  const addContextRow = () => {
    setTestContextVars((prev) => [...prev, { name: "", value: "" }]);
  };

  const removeContextRow = (index: number) => {
    setTestContextVars((prev) => prev.filter((_, i) => i !== index));
  };

  const updateContextRow = (index: number, field: "name" | "value", value: string) => {
    setTestContextVars((prev) => {
      const next = [...prev];
      next[index] = { ...next[index], [field]: value };
      return next;
    });
  };

  // 执行测试
  const handleExecuteTest = async () => {
    if (!testingInstance) {
      Toast.error("未选择工作流实例");
      return;
    }
    try {
      setTestSubmitting(true);
      // 构建上下文对象
      const contextObj: Record<string, any> = {};
      testContextVars
        .filter((item) => item.name && item.name.trim().length > 0)
        .forEach((item) => {
          contextObj[item.name.trim()] = item.value;
        });

      const ok = await WorkflowInstanceService.runWorkflowInstanceAsync(
        testingInstance.instanceId,
        contextObj
      );
      if (ok) {
        Toast.success("已发起异步执行");
        setTestModalVisible(false);
      } else {
        Toast.error("执行触发失败");
      }
    } catch (error) {
      console.error("触发执行失败:", error);
      Toast.error("触发执行失败");
    } finally {
      setTestSubmitting(false);
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


  // 查看
  const toggleDeploy = async (record: WorkflowInstanceDTO) => {

    if(record.status === "DEPLOYED"){
      const success = await WorkflowInstanceService.unDeployWorkflowInstance(record.instanceId);
      if (success) {
        Toast.success(`成功卸载工作流实例: ${record.workflowName}`);
        // 重新加载数据
        await loadData();
      } else {
        Toast.error("卸载实例失败");
      }
    }else{
      const success = await WorkflowInstanceService.deployWorkflowInstance(record.instanceId);
      if (success) {
        Toast.success(`成功部署工作流实例: ${record.workflowName}`);
        // 重新加载数据
        await loadData();
      } else {
        Toast.error("部署实例失败");
      }
    }
  };


  // 删除
  const handleDelete = async (record: WorkflowInstanceDTO) => {
    try {
      setLoading(true);
      const success = await WorkflowInstanceService.deleteWorkflowInstance(
        record.instanceId
      );
      if (success) {
        Toast.success(`成功删除实例: ${record.workflowName}`);
        // 重新加载数据
        await loadData();
      } else {
        Toast.error("删除实例失败");
      }
    } catch (error) {
      Toast.error("删除实例失败");
      console.error("删除实例失败:", error);
    } finally {
      setLoading(false);
    }
  };

  // 初始化加载数据：支持通过 URL 参数预筛选 workflowId/workflowName
  useEffect(() => {
    const qs = new URLSearchParams(location.search);
    const workflowId = qs.get("workflowId") || undefined;
    const workflowName = qs.get("workflowName") || undefined;

    if (workflowId || workflowName) {
      const params = {
        ...searchParams,
        workflowId,
        workflowName,
        pageNum: 1,
      } as WorkflowInstanceQuery;
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
              <Title heading={3}>工作流实例</Title>
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
                columns={columns}
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

            {/* 测试弹窗 */}
            <Modal
              title="测试执行"
              visible={testModalVisible}
              onCancel={handleTestCancel}
              footer={
                <Space>
                  <Button onClick={handleTestCancel}>取消</Button>
                  <Button type="primary" loading={testSubmitting} onClick={handleExecuteTest}>
                    执行
                  </Button>
                </Space>
              }
            >
              <div style={{ marginBottom: 12, display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                <span style={{ fontWeight: 500 }}>上下文变量</span>
                <Button icon={<IconPlus />} size="small" onClick={addContextRow}>
                  新增变量
                </Button>
              </div>
              {testContextVars.map((item, index) => (
                <div
                  key={index}
                  style={{ display: "flex", alignItems: "center", gap: 8, marginBottom: 8 }}
                >
                  <Input
                    placeholder="属性名"
                    value={item.name}
                    onChange={(val: string) => updateContextRow(index, "name", val)}
                    style={{ width: 200 }}
                  />
                  <Input
                    placeholder="属性值"
                    value={item.value}
                    onChange={(val: string) => updateContextRow(index, "value", val)}
                    style={{ flex: 1 }}
                  />
                  <Button
                    type="danger"
                    icon={<IconDelete />}
                    onClick={() => removeContextRow(index)}
                    disabled={testContextVars.length === 1}
                  />
                </div>
              ))}
              <div style={{ color: "rgba(0,0,0,0.45)", fontSize: 12 }}>
                可添加多个键值对，点击“执行”将以 JSON 形式传入后端。
              </div>
            </Modal>
          </ContentArea>
        </div>
      </MainContent>
    </AgentListLayout>
  );
};

export default WorkflowInstancePage;
