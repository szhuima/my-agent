import React, {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import {Button, Card, Input, Layout, Popconfirm, Space, Switch, Table, Toast, Typography,} from "@douyinfe/semi-ui";
import {IconDelete, IconEdit, IconExport, IconImport, IconPlus, IconSearch,} from "@douyinfe/semi-icons";
import styled from "styled-components";
import {theme} from "../styles/theme";
import {Header, Sidebar} from "../components/layout";
import {PageLayout} from "../components/page-layout";
import {PageDTO, WorkflowQueryRequestDTO, WorkflowResponseDTO, WorkflowService,} from "../services/workflow-service";
import useHandleNavigation from "../utils/useHandleNavigation";
import yaml from "js-yaml";
import WorkflowDslModal from "../components/workflow-dsl-modal";

const { Content } = Layout;
const { Title } = Typography;



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

export const WorkflowListPage: React.FC<AgentListPageProps> = ({
  selectedKey = "workflow-list",
  onMenuSelect,
}) => {
  const navigate = useNavigate();
  const [collapsed, setCollapsed] = useState(false);
  const fileInputRef = React.useRef<HTMLInputElement>(null);
  const handleNavigation = useHandleNavigation();

  // 处理退出登录
  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("userInfo");
    localStorage.removeItem("isLoggedIn");
    navigate("/login");
  };
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<WorkflowResponseDTO[]>([]);
  const [searchParams, setSearchParams] = useState<WorkflowQueryRequestDTO>({
    pageNum: 1,
    pageSize: 10,
  });
  const [total, setTotal] = useState(0);

  // 编辑 DSL 弹框状态
  const [dslModalVisible, setDslModalVisible] = useState(false);
  const [dslContent, setDslContent] = useState<string>("");
  const [dslSubmitting, setDslSubmitting] = useState(false);
  const [editingWorkflow, setEditingWorkflow] =
    useState<WorkflowResponseDTO | null>(null);
  // 表格列定义
  const columns = [
    {
      title: "工作流ID",
      dataIndex: "workflowId",
      key: "workflowId",
      width: 80,
      render: (text: string) => (
        <span style={{ fontFamily: "monospace", fontSize: "12px" }}>
          {text}
        </span>
      ),
    },
    {
      title: "工作流名称",
      dataIndex: "name",
      key: "name",
      width: 200,
      render: (text: string) => <span style={{ fontWeight: 500 }}>{text}</span>,
    },
    {
      title: "描述",
      dataIndex: "description",
      key: "description",
      width: 200,
      render: (text: string) => text || "-",
    },
    {
      title: "状态",
      dataIndex: "status",
      key: "status",
      width: 90,
      render: (status: number, record: WorkflowResponseDTO) => (
        <Switch
          checked={status === 1}
          onChange={(checked) => {
            if (checked) {
              activeWorkflow(record);
            } else {
              archiveWorkflow(record);
            }
          }}
          checkedText="激活"
          uncheckedText="归档"
          size="large"
        />
      ),
    },
    {
      title: "操作",
      key: "action",
      width: 120,
      fixed: "right" as const,
      render: (_: any, record: WorkflowResponseDTO) => (
        <Space>
          <ActionButton
            type="tertiary"
            size="small"
            icon={<IconEdit />}
            onClick={() => handleEdit(record)}
           />
          <ActionButton
            type="tertiary"
            size="small"
            icon={<IconExport />}
            onClick={() => handleExport(record)}
          >
            导出
          </ActionButton>
          <Popconfirm
            title="确定要删除这个配置吗？"
            content="删除后无法恢复，请谨慎操作"
            onConfirm={() => handleDelete(record)}
          >
            <ActionButton type="danger" size="small" icon={<IconDelete />} />
          </Popconfirm>
        </Space>
      ),
    },
  ];

  // 加载数据
  const loadData = async (params?: WorkflowQueryRequestDTO) => {
    setLoading(true);
    try {
      const queryParams = { ...searchParams, ...params };
      const result: PageDTO<WorkflowResponseDTO> | null =
        await WorkflowService.queryWorkflowList(queryParams);
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
    if (!searchParams.workflowName) {
      Toast.error("请输入工作流名称");
      return;
    }
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

  const handleImport = async () => {
    fileInputRef.current?.click();
  };

  const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    try {
      // 读取文件内容
      const content = await file.text(); // 返回 string
      // YAML 格式校验
      try {
        yaml.load(content);
      } catch (err: any) {
        Toast.error(`YAML 格式错误：${err.message}`);
        return;
      }
      await WorkflowService.saveWorkflow(content);
      // 刷新数据
      await loadData();
      Toast.success("导入工作流成功");
    } catch (error) {
    } finally {
      // 清空 input 值，以便下一次选择同一个文件也能触发 onChange
      e.target.value = "";
    }
  };

  // 新建
  const handleCreate = () => {
    // 在新标签页打开工作流创建画布页面（无侧边栏）
    window.open("/workflow-create?bare=1", "_blank");
  };

  // 查看
  const handleExport = async (record: WorkflowResponseDTO) => {
    try {
      const dsl: string | null = await WorkflowService.getDSL(
        record.workflowId
      );
      if (dsl) {
        // 导出dsl文件
        const blob = new Blob([dsl], { type: "text/plain;charset=utf-8" });
        const url = URL.createObjectURL(blob);
        const link = document.createElement("a");
        link.href = url;
        link.download = `${record.name}.yaml`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
      } else {
        Toast.error("获取DSL失败");
      }
    } catch (error) {
      console.error("获取DSL失败:", error);
      Toast.error(
        `获取DSL失败: ${error instanceof Error ? error.message : "未知错误"}`
      );
    }
  };

  // 编辑
  const handleEdit = async (record: WorkflowResponseDTO) => {
    const target = `/workflow-create?bare=1&configId=${encodeURIComponent(
      record.configId
    )}`;
    window.open(target, "_blank");
  };

  const handleDslImport = async () => {
    const content = dslContent?.trim();
    if (!content) {
      Toast.error("DSL 内容不能为空");
      return;
    }
    // YAML 格式校验
    try {
      yaml.load(content);
    } catch (err: any) {
      Toast.error(`YAML 格式错误：${err.message}`);
      return;
    }
    try {
      setDslSubmitting(true);
      const id = Toast.info("正在导入工作流 DSL...");
      await WorkflowService.saveWorkflow(content);
      Toast.close(id);
      Toast.success("DSL 导入成功");
      setDslModalVisible(false);
      setEditingWorkflow(null);
      setDslContent("");
      await loadData();
    } catch (error) {
      console.error("导入DSL失败:", error);
      Toast.error(
        `导入DSL失败: ${error instanceof Error ? error.message : "未知错误"}`
      );
    } finally {
      setDslSubmitting(false);
    }
  };

  // 激活
  const activeWorkflow = async (record: WorkflowResponseDTO) => {
    try {
      setLoading(true);

      const isActive = record.status === 1;

      if (isActive) {
        Toast.warning(`工作流 ${record.name} 已激活`);
        return;
      }

      const workflowId = await WorkflowService.activeWorkflow(
        record.workflowId
      );
      if (workflowId) {
        Toast.success(`工作流 ${record.name} 激活成功！工作流ID：${workflowId}`);
        // 刷新数据
        await loadData();
      }
    }  finally {
      setLoading(false);
    }
  };
  // 归档
  const archiveWorkflow = async (record: WorkflowResponseDTO) => {
    try {
      setLoading(true);

      const isActive = record.status === 1;

      if (!isActive) {
        Toast.warning(`工作流 ${record.name} 已归档`);
        return;
      }

      const workflowId = await WorkflowService.archiveWorkflow(
          record.workflowId
      );
      if (workflowId) {
        Toast.success(`工作流 ${record.name} 归档成功！工作流ID：${workflowId}`);
        // 刷新数据
        await loadData();
      }
    }  finally {
      setLoading(false);
    }
  };

  // 删除
  const handleDelete = async (record: WorkflowResponseDTO) => {
    try {
      setLoading(true);
      const success = await WorkflowService.deleteWorkflow(record.workflowId);
      if (success) {
        Toast.success(`成功删除工作流: ${record.name}`);
        // 重新加载数据
        await loadData();
      } else {
        Toast.error("删除工作流失败");
      }
    } catch (error) {
      console.error("删除工作流失败:", error);
    } finally {
      setLoading(false);
    }
  };

  // 初始化加载数据
  useEffect(() => {
    loadData();
  }, []);

  return (
    <PageLayout>
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
              <Title heading={3}>工作流列表</Title>
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
                {/* <Input
                  placeholder="请输入智能体ID"
                  value={searchParams.agentId || ''}
                  onChange={(value) => setSearchParams(prev => ({ ...prev, agentId: value }))}
                  style={{ width: 200 }}
                /> */}
                <Button type="primary" onClick={handleSearch} loading={loading}>
                  搜索
                </Button>
                <Button onClick={handleReset}>重置</Button>

                <div style={{ marginLeft: "auto" }}>
                  <span style={{ marginRight: 8 }}>
                    <Button
                      type="primary"
                      icon={<IconImport />}
                      onClick={handleImport}
                    >
                      导入
                    </Button>
                  </span>

                  <Button
                    type="primary"
                    icon={<IconPlus />}
                    onClick={handleCreate}
                  >
                    新建
                  </Button>
                </div>

                <input
                  type="file"
                  accept=".yaml,.yml"
                  ref={fileInputRef}
                  style={{ display: "none" }}
                  onChange={handleFileChange}
                />
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
          </ContentArea>
        </div>
      </MainContent>
      {/* 编辑 DSL 弹框 */}
      <WorkflowDslModal
        visible={dslModalVisible}
        editingWorkflow={editingWorkflow}
        dslContent={dslContent}
        submitting={dslSubmitting}
        onCancel={() => {
          setDslModalVisible(false);
          setEditingWorkflow(null);
        }}
        onOk={handleDslImport}
        onChange={setDslContent}
      />
    </PageLayout>
  );
};

export default WorkflowListPage;
