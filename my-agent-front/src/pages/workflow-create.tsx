import React, { useState } from "react";
import { useLocation } from "react-router-dom";
import { Layout, Typography } from "@douyinfe/semi-ui";
import styled from "styled-components";
import { theme } from "../styles/theme";
import { Sidebar, Header } from "../components/layout";
import { Editor } from "../editor";

const { Content } = Layout;
const { Title, Text } = Typography;

const PageLayout = styled(Layout)`
  min-height: 100vh;
  background: ${theme.colors.bg.secondary};
`;

const MainContent = styled.div<{ $collapsed: boolean; $bare?: boolean }>`
  display: flex;
  flex: 1;
  margin-left: ${(props) => (props.$bare ? "0" : props.$collapsed ? "80px" : "280px")};
  transition: margin-left ${theme.animation.duration.normal}
    ${theme.animation.easing.cubic};
`;

const ContentArea = styled(Content)`
  flex: 1;
  padding: ${theme.spacing.lg};
  background: ${theme.colors.bg.secondary};
  overflow: hidden; /* 让编辑器自己处理内部滚动 */
`;

const PageHeader = styled.div`
  margin-bottom: ${theme.spacing.lg};
`;

// 工作流创建页面：集成 Flowgram 拖拽编辑器
export const WorkflowCreatePage: React.FC<{ selectedKey?: string }> = ({
  selectedKey = "workflow-list",
}) => {
  const [collapsed, setCollapsed] = useState(false);
  const location = useLocation();
  const isBare = new URLSearchParams(location.search).get("bare") === "1";

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("userInfo");
    localStorage.removeItem("isLoggedIn");
    window.location.href = "/login";
  };

  return (
    <PageLayout>
      {!isBare && <Sidebar selectedKey={selectedKey} collapsed={collapsed} />}
      <MainContent $collapsed={collapsed} $bare={isBare}>
        <div style={{ flex: 1, display: "flex", flexDirection: "column" }}>
          <Header
            onToggleSidebar={() => setCollapsed(!collapsed)}
            onLogout={handleLogout}
            collapsed={collapsed}
          />
          <ContentArea>
            <PageHeader>
              <Title heading={3}>新建工作流</Title>
              <Text type="tertiary">
                通过拖拽节点、连接线来构建工作流，右侧工具栏可保存配置。
              </Text>
            </PageHeader>
            {/* Flowgram 编辑器主体 */}
            <div style={{ height: isBare ? "100vh" : "calc(100vh - 200px)" }}>
              <Editor />
            </div>
          </ContentArea>
        </div>
      </MainContent>
    </PageLayout>
  );
};

export default WorkflowCreatePage;