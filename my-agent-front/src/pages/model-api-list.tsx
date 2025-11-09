import React, {useEffect, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {Button, Card, Input, Layout, Popconfirm, Space, Table, Tag, Toast, Typography} from '@douyinfe/semi-ui';
import {IconDelete, IconEdit, IconPlay, IconPlus, IconRefresh, IconSearch} from '@douyinfe/semi-icons';
import styled from 'styled-components';
import {theme} from '../styles/theme';
import {Header, Sidebar} from '../components/layout';
import {PageLayout} from '../components/page-layout';
import {ModelApiCreateModal} from '../components/model-api-create-modal';
import {ModelApiEditModal} from '../components/model-api-edit-modal';
import {ModelApiTestModal} from '../components/model-api-test-modal';
import {
  aiClientApiAdminService,
  AiClientApiQueryRequestDTO,
  AiClientApiResponseDTO
} from '../services/model-api-service';
import {PageDTO} from '../typings/page';
import {AiAgentService} from '../services/ai-agent-service';
import useHandleNavigation from '../utils/useHandleNavigation';


const { Content } = Layout;
const { Title } = Typography;

// 样式组件
const AiClientApiManagementLayout = styled(Layout)`
  min-height: 100vh;
  background: ${theme.colors.bg.secondary};
`;

const MainContent = styled.div<{ $collapsed: boolean }>`
  display: flex;
  flex: 1;
  margin-left: ${props => props.$collapsed ? '80px' : '280px'};
  transition: margin-left ${theme.animation.duration.normal} ${theme.animation.easing.cubic};
`;

const ContentArea = styled(Content)`
  flex: 1;
  padding: ${theme.spacing.lg};
  background: ${theme.colors.bg.secondary};
  overflow-y: auto;
`;

const AiClientApiManagementContainer = styled.div`
  height: 100%;
  display: flex;
  flex-direction: column;
`;

const PageHeader = styled.div`
  padding: ${theme.spacing.lg};
  border-bottom: 1px solid ${theme.colors.border.secondary};
`;

const SearchSection = styled(Card)`
  margin: ${theme.spacing.lg};
  
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

const TableContainer = styled.div`
  flex: 1;
  margin: 0 ${theme.spacing.lg} ${theme.spacing.lg};
  display: flex;
  flex-direction: column;
`;

const TableCard = styled(Card)`
  flex: 1;
  display: flex;
  flex-direction: column;
  
  .semi-card-body {
    padding: 0;
    flex: 1;
    display: flex;
    flex-direction: column;
  }
`;

const TableWrapper = styled.div`
  flex: 1;
  overflow: auto;
`;

const ActionButton = styled(Button)`
  margin-right: ${theme.spacing.sm};
`;

export const AiClientApiManagement: React.FC = () => {
  const navigate = useNavigate();
  const handleNavigation = useHandleNavigation();
  const [collapsed, setCollapsed] = useState(false);
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<AiClientApiResponseDTO[]>([]);
  const [createModalVisible, setCreateModalVisible] = useState(false);
  const [searchForm, setSearchForm] = useState<AiClientApiQueryRequestDTO>({
    modelApiName: '',
    pageNum: 1,
    pageSize: 10
  });
  const [total, setTotal] = useState<number>(0);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState<AiClientApiResponseDTO | null>(null);
  const [loadingApiIds, setLoadingApiIds] = useState<Set<string>>(new Set());
  const [testVisible, setTestVisible] = useState(false);
  const [testingRecord, setTestingRecord] = useState<AiClientApiResponseDTO | null>(null);

  // 表格列定义
  const columns = [
    {
      title: "ID",
      dataIndex: "id",
      key: "id",
      width: 80,
    },
    {
      title: "模型API名称",
      dataIndex: "modelApiName",
      key: "modelApiName",
      width: 160,
    },
    {
      title: "模型来源",
      dataIndex: "modelSource",
      key: "modelSource",
      width: 120,
    },
    {
      title: "模型名称",
      dataIndex: "modelName",
      key: "modelName",
      width: 160,
    },
    {
      title: "模型类型",
      dataIndex: "modelType",
      key: "modelType",
      width: 120,
      render: (modelType: string) => {
        if (modelType === 'CHAT') {
          return '对话模型';
        } else if (modelType === 'EMBEDDING') {
          return '嵌入模型';
        } else {
          return modelType || '-';
        }
      },
    },
    {
      title: "基础URL",
      dataIndex: "baseUrl",
      key: "baseUrl",
      width: 250,
      render: (text: string) => (
        <span
          title={text}
          style={{
            display: "block",
            maxWidth: "230px",
            overflow: "hidden",
            textOverflow: "ellipsis",
            whiteSpace: "nowrap",
          }}
        >
          {text}
        </span>
      ),
    },
    {
      title: "对话路径",
      dataIndex: "completionsPath",
      key: "completionsPath",
      width: 150,
      render: (text: string) => text || "-",
    },
    {
      title: "嵌入路径",
      dataIndex: "embeddingsPath",
      key: "embeddingsPath",
      width: 150,
      render: (text: string) => text || "-",
    },
    {
      title: "API密钥",
      dataIndex: "apiKey",
      key: "apiKey",
      width: 100,
      render: (text: string) => (
        <span
          title={text ? `点击复制完整API密钥: ${text}` : "无API密钥"}
          style={{
            display: "block",
            maxWidth: "180px",
            overflow: "hidden",
            textOverflow: "ellipsis",
            whiteSpace: "nowrap",
            cursor: text ? "pointer" : "default",
            color: text ? "#1890ff" : "inherit",
            textDecoration: text ? "underline" : "none",
          }}
          onClick={() => text && handleCopyApiKey(text)}
        >
          {text ? "***" + text.slice(-4) : "-"}
        </span>
      ),
    },

    {
      title: "状态",
      dataIndex: "status",
      key: "status",
      width: 100,
      render: (status: number) => (
        <Tag color={status === 1 ? "green" : "red"}>
          {status === 1 ? "启用" : "禁用"}
        </Tag>
      ),
    },
    {
      title: "创建时间",
      dataIndex: "createTime",
      key: "createTime",
      width: 120,
    },
    {
      title: "更新时间",
      dataIndex: "updateTime",
      key: "updateTime",
      width: 120,
    },
    {
      title: "操作",
      key: "action",
      width: 120,
      fixed: "right" as const,
      render: (_: any, record: any) => (
        <Space>
          <ActionButton
            type="primary"
            size="small"
            icon={<IconEdit />}
            onClick={() => handleEdit(record)}
          >
            
          </ActionButton>
          <ActionButton
            type="secondary"
            size="small"
            icon={<IconPlay />}
            onClick={() => handleTest(record)}
          >
          </ActionButton>
          <Popconfirm
            title="确定要删除这个API配置吗？"
            content="删除后无法恢复"
            onConfirm={() => handleDelete(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <ActionButton type="danger" size="small" icon={<IconDelete />}>
              
            </ActionButton>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  // 加载数据
  const loadData = async () => {
    try {
      setLoading(true);
      const page: PageDTO<AiClientApiResponseDTO> = await aiClientApiAdminService.queryAiClientApiList(searchForm);
      setDataSource(page?.records || []);
      setTotal(page?.total || 0);
    } catch (error) {
      console.error('查询AI客户端API配置失败:', error);
      Toast.error('查询失败，请稍后重试');
    } finally {
      setLoading(false);
    }
  };

  // 搜索
  const handleSearch = () => {
    setSearchForm(prev => ({ ...prev, pageNum: 1 }));
    loadData();
  };

  // 重置搜索
  const handleReset = () => {
    setSearchForm({
      modelApiName: '',
      pageNum: 1,
      pageSize: 10
    });
    loadData();
  };

  // 分页变化
  const handlePageChange = (pageNum: number, pageSize: number) => {
    setSearchForm(prev => ({ ...prev, pageNum, pageSize }));
    // 立即加载
    setTimeout(() => loadData(), 0);
  };

  // 刷新
  const handleRefresh = () => {
    loadData();
  };

  // 创建
  const handleCreate = () => {
    setCreateModalVisible(true);
  };

  // 创建成功回调
  const handleCreateSuccess = () => {
    setCreateModalVisible(false);
    loadData();
  };

  // 取消创建
  const handleCreateCancel = () => {
    setCreateModalVisible(false);
  };

  // 编辑
  const handleEdit = (record: AiClientApiResponseDTO) => {
    setEditingRecord(record);
    setModalVisible(true);
  };

  // 测试
  const handleTest = (record: AiClientApiResponseDTO) => {
    setTestingRecord(record);
    setTestVisible(true);
  };

  // 模态框成功回调
  const handleModalSuccess = () => {
    setModalVisible(false);
    setEditingRecord(null);
    loadData();
  };

  // 模态框取消回调
  const handleModalCancel = () => {
    setModalVisible(false);
    setEditingRecord(null);
  };

  const handleTestCancel = () => {
    setTestVisible(false);
    setTestingRecord(null);
  };

  // 删除
  const handleDelete = async (id: string) => {
    try {
      const response = await aiClientApiAdminService.deleteAiClientApiByApiId(id);
      if (response.code === '0000') {
        Toast.success('删除成功');
        loadData();
      } else {
        Toast.error(response.info || '删除失败');
      }
    } catch (error) {
      console.error('删除AI客户端API配置失败:', error);
      Toast.error('删除失败，请稍后重试');
    }
  };

  // 复制API密钥
  const handleCopyApiKey = async (apiKey: string) => {
    try {
      await navigator.clipboard.writeText(apiKey);
      Toast.success('API密钥已复制到剪贴板');
    } catch (error) {
      console.error('复制失败:', error);
      // 降级方案：使用传统的复制方法
      try {
        const textArea = document.createElement('textarea');
        textArea.value = apiKey;
        textArea.style.position = 'fixed';
        textArea.style.left = '-999999px';
        textArea.style.top = '-999999px';
        document.body.appendChild(textArea);
        textArea.focus();
        textArea.select();
        document.execCommand('copy');
        document.body.removeChild(textArea);
        Toast.success('API密钥已复制到剪贴板');
      } catch (fallbackError) {
        console.error('降级复制方案也失败:', fallbackError);
        Toast.error('复制失败，请手动复制');
      }
    }
  };

  // 加载API
  const handleLoadApi = async (apiId: string) => {
    try {
      setLoadingApiIds(prev => new Set(prev).add(apiId));
      const success = await AiAgentService.armoryApi(apiId);
      if (success) {
        Toast.success('API加载成功');
      } else {
        Toast.error('API加载失败');
      }
    } catch (error) {
      console.error('加载API失败:', error);
      Toast.error('加载API失败，请稍后重试');
    } finally {
      setLoadingApiIds(prev => {
        const newSet = new Set(prev);
        newSet.delete(apiId);
        return newSet;
      });
    }
  };

  // 页面初始化
  useEffect(() => {
    loadData();
  }, []);

  return (
    <PageLayout>
      <Sidebar 
        collapsed={collapsed} 
        selectedKey="ai-client-api-management"
        onSelect={handleNavigation}
      />
      <MainContent $collapsed={collapsed}>
        <ContentArea>
          <Header 
            collapsed={collapsed}
            onToggleSidebar={() => setCollapsed(!collapsed)}
            onLogout={() => navigate('/login')} 
          />
          <AiClientApiManagementContainer>
            <PageHeader>
              <Title heading={3}>模型API管理</Title>
            </PageHeader>

            <SearchSection>
              <SearchRow>
                <Input
                  placeholder="请输入API 名称"
                  value={searchForm.modelApiName}
                  onChange={(value) => setSearchForm(prev => ({ ...prev, modelApiName: value }))}
                  style={{ width: 200 }}
                />
                <Button
                  type="primary"
                  icon={<IconSearch />}
                  onClick={handleSearch}
                  loading={loading}
                >
                  搜索
                </Button>
                <Button
                  icon={<IconRefresh />}
                  onClick={handleReset}
                >
                  重置
                </Button>
              </SearchRow>
            </SearchSection>

            <TableContainer>
              <TableCard>
                <div style={{ padding: '16px', borderBottom: '1px solid #e6e6e6' }}>
                  <Space>
                    <Button
                      type="primary"
                      icon={<IconPlus />}
                      onClick={handleCreate}
                    >
                      新增API
                    </Button>
                    <Button
                      icon={<IconRefresh />}
                      onClick={handleRefresh}
                      loading={loading}
                    >
                      刷新
                    </Button>
                  </Space>
                </div>
                <TableWrapper>
                  <Table
                    columns={columns}
                    dataSource={dataSource}
                    loading={loading}
                    pagination={{
                      currentPage: searchForm.pageNum || 1,
                      pageSize: searchForm.pageSize || 10,
                      total: total,
                      showSizeChanger: true,
                      showQuickJumper: true,
                      onChange: handlePageChange,
                    }}
                    scroll={{ x: 1200}}
                    rowKey="id"
                  />
                </TableWrapper>
              </TableCard>
            </TableContainer>
          </AiClientApiManagementContainer>
        </ContentArea>
      </MainContent>

      <ModelApiCreateModal
        visible={createModalVisible}
        onCancel={handleCreateCancel}
        onSuccess={handleCreateSuccess}
      />

      <ModelApiEditModal
        visible={modalVisible}
        editingRecord={editingRecord}
        onCancel={handleModalCancel}
        onSuccess={handleModalSuccess}
      />

      <ModelApiTestModal
        visible={testVisible}
        record={testingRecord}
        onCancel={handleTestCancel}
      />
    </PageLayout>
  );
};