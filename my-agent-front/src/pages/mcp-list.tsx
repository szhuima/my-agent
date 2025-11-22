import React, {useEffect, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {
    Button,
    Card,
    Input,
    Layout,
    Modal,
    Popconfirm,
    Select,
    Space,
    Table,
    Tag,
    TextArea,
    Toast,
    Typography
} from '@douyinfe/semi-ui';
import {IconDelete, IconEdit, IconEyeOpened, IconPlus, IconRefresh, IconSearch} from '@douyinfe/semi-icons';
import styled from 'styled-components';
import {theme} from '../styles/theme';
import {Header, Sidebar} from '../components/layout';
import {PageLayout} from '../components/page-layout';
import {
    aiClientToolMcpAdminService,
    AiClientToolMcpQueryRequestDTO,
    AiClientToolMcpRequestDTO,
    AiClientToolMcpResponseDTO
} from '../services/ai-client-tool-mcp-admin-service';
import useHandleNavigation from "../utils/useHandleNavigation";


const { Content } = Layout;
const { Title } = Typography;
const { Option } = Select;

// 样式组件
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

const ClientToolMcpManagementContainer = styled.div`
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

const ConfigPreview = styled.div`
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
  color: ${theme.colors.primary};
  
  &:hover {
    text-decoration: underline;
  }
`;

const ConfigModalContent = styled.pre`
  background: #f6f8fa;
  padding: 16px;
  border-radius: 6px;
  overflow-x: auto;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 12px;
  line-height: 1.5;
  max-height: 400px;
  overflow-y: auto;
`;

export const McpList: React.FC = () => {
  const navigate = useNavigate();
  const handleNavigation = useHandleNavigation();

  const [collapsed, setCollapsed] = useState(false);
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<AiClientToolMcpResponseDTO[]>([]);
  const [searchText, setSearchText] = useState('');
  const [status, setStatus] = useState<number | undefined>(undefined);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [total, setTotal] = useState(0);
  
  // 传输配置弹窗相关状态
  const [configModalVisible, setConfigModalVisible] = useState(false);
  const [selectedConfig, setSelectedConfig] = useState<string>('');
  const [selectedMcpName, setSelectedMcpName] = useState<string>('');
  
  // 新增MCP配置弹窗相关状态
  const [createModalVisible, setCreateModalVisible] = useState(false);
  const [createLoading, setCreateLoading] = useState(false);
  const [formData, setFormData] = useState<AiClientToolMcpRequestDTO>({
    id: undefined,
    config: '',
    requestTimeout: 10,
    status: 1 // 默认启用状态
  });

  // 编辑MCP配置弹窗相关状态
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [editLoading, setEditLoading] = useState(false);
  const [editFormData, setEditFormData] = useState<AiClientToolMcpRequestDTO>({
    id: 0,
    config: '{}',
    requestTimeout: 10,
    status: 1
  });
  const [currentEditRecord, setCurrentEditRecord] = useState<AiClientToolMcpResponseDTO | null>(null);

  // 处理退出登录
  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('userInfo');
    localStorage.removeItem('isLoggedIn');
    Toast.success('已退出登录');
    navigate('/login');
  };



  // 显示传输配置详情
  const showConfigDetail = (config: string, mcpName: string) => {
    setSelectedConfig(config);
    setSelectedMcpName(mcpName);
    setConfigModalVisible(true);
  };

  // 格式化JSON配置
  const formatConfig = (config: string) => {
    try {
      const parsed = JSON.parse(config);
      return JSON.stringify(parsed, null, 2);
    } catch (error) {
      return config;
    }
  };

  // 表格列定义
  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: 'MCP名称',
      dataIndex: 'mcpName',
      key: 'mcpName',
      width: 200,
    },
    {
      title: '传输配置',
      dataIndex: 'transportConfig',
      key: 'transportConfig',
      width: 250,
      render: (config: string, record: AiClientToolMcpResponseDTO) => (
        <ConfigPreview onClick={() => showConfigDetail(config, record.mcpName)}>
          {config ? `${config.substring(0, 50)}...` : '-'}
        </ConfigPreview>
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: number) => (
        <Tag color={status === 1 ? 'green' : 'red'}>
          {status === 1 ? '启用' : '禁用'}
        </Tag>
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 180,
      render: (time: string) => new Date(time).toLocaleString(),
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      key: 'updateTime',
      width: 180,
      render: (time: string) => new Date(time).toLocaleString(),
    },
    {
      title: '操作',
      key: 'action',
      width: 120,
      fixed: 'right' as const,
      render: (_: any, record: AiClientToolMcpResponseDTO) => (
        <Space>
          <ActionButton
            theme="borderless"
            type="tertiary"
            icon={<IconEyeOpened />}
            size="small"
            onClick={() => showConfigDetail(record.transportConfig, record.mcpName)}
          >
          </ActionButton>
          <ActionButton
            theme="borderless"
            type="primary"
            icon={<IconEdit />}
            size="small"
            onClick={() => handleEdit(record)}
          >
            
          </ActionButton>
          <Popconfirm
            title="确定要删除这个MCP配置吗？"
            content="删除后无法恢复，请谨慎操作"
            onConfirm={() => handleDelete(record)}
            okText="确定"
            cancelText="取消"
          >
            <ActionButton
              theme="borderless"
              type="danger"
              icon={<IconDelete />}
              size="small"
            >
              
            </ActionButton>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  // 获取MCP客户端工具列表数据
  const fetchMcpList = async () => {
    setLoading(true);
    try {
      const request: AiClientToolMcpQueryRequestDTO = {
        mcpName: searchText || undefined,
        status: status,
        pageNum: currentPage,
        pageSize: pageSize,
      };

      const result = await aiClientToolMcpAdminService.queryAiClientToolMcpList(request);
      
      if (result.code === '0000') {
        const data = result.data || [];
        setDataSource(data);
        setTotal(data.length); // 简单实现，实际应该从后端返回总数
      } else {
        throw new Error(result.info || '查询失败');
      }
    } catch (error) {
      console.error('获取MCP客户端工具列表失败:', error);
      Toast.error('获取MCP客户端工具列表失败，请检查网络连接');
      setDataSource([]);
      setTotal(0);
    } finally {
      setLoading(false);
    }
  };

  // 删除MCP配置
  const handleDelete = async (record: AiClientToolMcpResponseDTO) => {
    try {
      const result = await aiClientToolMcpAdminService.deleteAiClientToolMcpById(record.id);
      
      if (result.code === '0000' && result.data) {
        Toast.success('删除成功');
        // 重新加载数据
        fetchMcpList();
      } else {
        throw new Error(result.info || '删除失败');
      }
    } catch (error) {
      console.error('删除MCP配置失败:', error);
      Toast.error('删除失败，请检查网络连接');
    }
  };

  // 编辑MCP配置
  const handleEdit = (record: AiClientToolMcpResponseDTO) => {
    setCurrentEditRecord(record);
    setEditFormData({
      id: record.id,
      config: record.transportConfig,
      requestTimeout: record.requestTimeout,
      status: record.status
    });
    setEditModalVisible(true);
  };

  // 关闭编辑弹窗
  const handleEditCancel = () => {
    setEditModalVisible(false);
    setCurrentEditRecord(null);
    setEditFormData({
      id: 0,
      config: '{}',
      requestTimeout: 10,
      status: 1
    });
  };

  // 处理编辑表单字段变化
  const handleEditFormChange = (field: keyof AiClientToolMcpRequestDTO, value: any) => {
    setEditFormData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  // 提交编辑MCP配置
  const handleEditSubmit = async () => {
    // 表单验证
    if (!editFormData.config?.trim()) {
      Toast.error('请输入传输配置');
      return;
    }

    // 验证JSON格式
    try {
      JSON.parse(editFormData.config);
    } catch (error) {
      Toast.error('传输配置必须是有效的JSON格式');
      return;
    }

    setEditLoading(true);
    try {
      const request: AiClientToolMcpRequestDTO = {
        ...editFormData,
        id: currentEditRecord!.id, // 保持ID不变
        config: editFormData.config.trim()
      };

      const result = await aiClientToolMcpAdminService.updateAiClientToolMcpByMcpId(request);
      
      if (result.code === '0000' && result.data) {
        Toast.success('更新成功');
        setEditModalVisible(false);
        // 重新加载数据
        fetchMcpList();
        // 重置表单
        handleEditCancel();
      } else {
        throw new Error(result.info || '更新失败');
      }
    } catch (error) {
      console.error('更新MCP配置失败:', error);
      Toast.error('更新失败，请检查网络连接');
    } finally {
      setEditLoading(false);
    }
  };


  // 打开新增弹窗
  const handleCreate = () => {
    setFormData({
      id: undefined,
      config: '',
      requestTimeout: 10,
      status: 1
    });
    setCreateModalVisible(true);
  };

  // 关闭新增弹窗
  const handleCreateCancel = () => {
    setCreateModalVisible(false);
    setFormData({
      config: '',
      requestTimeout: 10,
      status: 1
    });
  };

  // 处理表单字段变化
  const handleFormChange = (field: keyof AiClientToolMcpRequestDTO, value: any) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  // 提交新增MCP配置
  const handleCreateSubmit = async () => {
    // 表单验证
    if (!formData.config?.trim()) {
      Toast.error('请输入传输配置');
      return;
    }

    // 验证JSON格式
    try {
      JSON.parse(formData.config);
    } catch (error) {
      Toast.error('传输配置必须是有效的JSON格式');
      return;
    }

    setCreateLoading(true);
    try {
      const request: AiClientToolMcpRequestDTO = {
        ...formData,
        status: 1, // 默认启用状态
        config: formData.config.trim()
      };

      const result = await aiClientToolMcpAdminService.createAiClientToolMcp(request);
      
      if (result.code === '0000' && result.data) {
        Toast.success('创建成功');
        setCreateModalVisible(false);
        // 重新加载数据
        fetchMcpList();
        // 重置表单
        handleCreateCancel();
      } else {
        throw new Error(result.info || '创建失败');
      }
    } catch (error) {
      console.error('创建MCP配置失败:', error);
      Toast.error('创建失败，请检查网络连接');
    } finally {
      setCreateLoading(false);
    }
  };

  // 处理状态变化
  const handleStatusChange = (value: any) => {
    setStatus(value === '' ? undefined : Number(value));
  };

  // 搜索
  const handleSearch = () => {
    setCurrentPage(1);
    fetchMcpList();
  };

  // 重置搜索
  const handleReset = () => {
    setSearchText('');
    setStatus(undefined);
    setCurrentPage(1);
    fetchMcpList();
  };

  // 分页变化
  const handlePageChange = (page: number, size?: number) => {
    setCurrentPage(page);
    if (size && size !== pageSize) {
      setPageSize(size);
    }
    fetchMcpList();
  };

  // 组件挂载时获取数据
  useEffect(() => {
    fetchMcpList();
  }, []);

  return (
    <PageLayout>
      <Sidebar
        collapsed={collapsed}
        selectedKey="client-tool-mcp-management"
        onSelect={handleNavigation}
      />
      <MainContent $collapsed={collapsed}>
        <ContentArea>
          <Header
            collapsed={collapsed}
            onToggleSidebar={() => setCollapsed(!collapsed)}
            onLogout={handleLogout}
          />
          <ClientToolMcpManagementContainer>
            <PageHeader>
              <Title heading={3} style={{ margin: 0 }}>
                MCP客户端工具管理
              </Title>
            </PageHeader>

            <SearchSection>
              <SearchRow>
                <Input
                  placeholder="请输入MCP名称"
                  value={searchText}
                  onChange={setSearchText}
                  style={{ width: 200 }}
                  onEnterPress={handleSearch}
                />
                <Select
                  placeholder="选择状态"
                  value={status === undefined ? "" : status}
                  onChange={handleStatusChange}
                  style={{ width: 120 }}
                >
                  <Option value="">全部</Option>
                  <Option value={1}>启用</Option>
                  <Option value={0}>禁用</Option>
                </Select>
                <Button
                  type="primary"
                  icon={<IconSearch />}
                  onClick={handleSearch}
                ></Button>
                <Button icon={<IconRefresh />} onClick={handleReset}></Button>
                <Button
                  type="primary"
                  theme="solid"
                  icon={<IconPlus />}
                  onClick={handleCreate}
                  style={{ marginLeft: "auto" }}
                >
                </Button>
              </SearchRow>
            </SearchSection>

            <TableContainer>
              <TableCard>
                <TableWrapper>
                  <Table
                    columns={columns}
                    dataSource={dataSource}
                    loading={loading}
                    pagination={{
                      currentPage: currentPage,
                      pageSize: pageSize,
                      total: total,
                      showSizeChanger: true,
                      showQuickJumper: true,
                      onChange: handlePageChange,
                    }}
                    rowKey="id"
                    scroll={{ x: 1400 }}
                    empty={
                      <div style={{ padding: "40px", textAlign: "center" }}>
                        <Typography.Text type="tertiary">
                          暂无数据
                        </Typography.Text>
                      </div>
                    }
                  />
                </TableWrapper>
              </TableCard>
            </TableContainer>
          </ClientToolMcpManagementContainer>

          {/* 传输配置详情弹窗 */}
          <Modal
            title={`传输配置详情 - ${selectedMcpName}`}
            visible={configModalVisible}
            onCancel={() => setConfigModalVisible(false)}
            footer={
              <Button onClick={() => setConfigModalVisible(false)}>关闭</Button>
            }
            width={800}
            style={{ maxWidth: "90vw" }}
          >
            <ConfigModalContent>
              {formatConfig(selectedConfig)}
            </ConfigModalContent>
          </Modal>

          {/* 新增MCP配置弹窗 */}
          <Modal
            title="新增MCP配置"
            visible={createModalVisible}
            onCancel={handleCreateCancel}
            footer={
              <Space>
                <Button onClick={handleCreateCancel}>取消</Button>
                <Button
                  type="primary"
                  loading={createLoading}
                  onClick={handleCreateSubmit}
                >
                  保存
                </Button>
              </Space>
            }
            width={600}
            style={{ maxWidth: "90vw" }}
          >
            <div style={{ padding: "20px 0" }}>
              <div
                style={{
                  display: "flex",
                  flexDirection: "column",
                  gap: "16px",
                }}
              >
                <div>
                  <Typography.Text
                    strong
                    style={{ display: "block", marginBottom: 8 }}
                  >
                    传输配置 <Typography.Text type="danger">*</Typography.Text>
                  </Typography.Text>
                  <TextArea
                    placeholder={`{
  "mcpServers": {
    "chrome-devtools": {
      "command": "npx",
      "args": ["-y", "chrome-devtools-mcp@latest"]
    }
  }
}`}
                    value={formData.config}
                    onChange={(value: string) =>
                      handleFormChange("config", value)
                    }
                    rows={6}
                    style={{
                      width: "100%",
                      fontFamily: "Monaco, Menlo, Ubuntu Mono, monospace",
                    }}
                  />
                </div>

                <div>
                  <Typography.Text
                    strong
                    style={{ display: "block", marginBottom: 8 }}
                  >
                    请求超时时间（秒）
                  </Typography.Text>
                  <Input
                    type="number"
                    placeholder="请输入超时时间"
                    value={formData.requestTimeout}
                    onChange={(value: string) =>
                      handleFormChange("requestTimeout", Number(value))
                    }
                    style={{ width: "100%" }}
                  />
                </div>
              </div>
            </div>
          </Modal>

          {/* 编辑MCP配置弹窗 */}
          <Modal
            title="编辑MCP配置"
            visible={editModalVisible}
            onCancel={handleEditCancel}
            footer={
              <Space>
                <Button onClick={handleEditCancel}>取消</Button>
                <Button
                  type="primary"
                  loading={editLoading}
                  onClick={handleEditSubmit}
                >
                  保存
                </Button>
              </Space>
            }
            width={600}
            style={{ maxWidth: "90vw" }}
          >
            <div style={{ padding: "20px 0" }}>
              <div
                style={{
                  display: "flex",
                  flexDirection: "column",
                  gap: "16px",
                }}
              >
                <div>
                  <Typography.Text
                    strong
                    style={{ display: "block", marginBottom: 8 }}
                  >
                    MCP ID
                  </Typography.Text>
                  <Input
                    value={editFormData.id}
                    disabled
                    style={{ width: "100%", backgroundColor: "#f6f8fa" }}
                  />
                  <Typography.Text type="tertiary" size="small">
                    MCP ID不可修改
                  </Typography.Text>
                </div>

                <div>
                  <Typography.Text
                    strong
                    style={{ display: "block", marginBottom: 8 }}
                  >
                    传输配置 <Typography.Text type="danger">*</Typography.Text>
                  </Typography.Text>
                  <TextArea
                    placeholder="请输入传输配置（JSON格式）"
                    value={editFormData.config}
                    onChange={(value: string) =>
                      handleEditFormChange("config", value)
                    }
                    rows={6}
                    style={{
                      width: "100%",
                      fontFamily: "Monaco, Menlo, Ubuntu Mono, monospace",
                    }}
                  />
                  <Typography.Text type="tertiary" size="small">
                    请输入有效的JSON格式配置，例如：{"{"}"command": "node",
                    "args": ["server.js"]{"}"}
                  </Typography.Text>
                </div>

                <div>
                  <Typography.Text
                    strong
                    style={{ display: "block", marginBottom: 8 }}
                  >
                    请求超时时间（秒）
                  </Typography.Text>
                  <Input
                    type="number"
                    placeholder="请输入超时时间"
                    value={editFormData.requestTimeout}
                    onChange={(value: string) =>
                      handleEditFormChange("requestTimeout", Number(value))
                    }
                    style={{ width: "100%" }}
                  />
                </div>

                <div>
                  <Typography.Text
                    strong
                    style={{ display: "block", marginBottom: 8 }}
                  >
                    状态
                  </Typography.Text>
                  <Select
                    value={editFormData.status}
                    onChange={(value: any) =>
                      handleEditFormChange("status", value)
                    }
                    style={{ width: "100%" }}
                  >
                    <Option value={1}>启用</Option>
                    <Option value={0}>禁用</Option>
                  </Select>
                </div>
              </div>
            </div>
          </Modal>
        </ContentArea>
      </MainContent>
    </PageLayout>
  );
};