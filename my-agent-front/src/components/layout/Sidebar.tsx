import React from 'react';
import {Avatar, Nav, Typography} from '@douyinfe/semi-ui';
import {IconApps} from '@douyinfe/semi-icons';
import agentLogo from '../../assets/icon-agent.jpg';
import styled from 'styled-components';
import {theme} from '../../styles/theme';

const { Text } = Typography;

interface SidebarProps {
  selectedKey?: string;
  onSelect?: (key: string) => void;
  collapsed?: boolean;
}

const SidebarContainer = styled.div<{ $collapsed: boolean }>`
  width: ${props => props.$collapsed ? '80px' : '260px'};
  height: 100vh;
  background: ${theme.colors.bg.primary};
  border-right: 1px solid ${theme.colors.border.secondary};
  display: flex;
  flex-direction: column;
  transition: width ${theme.animation.duration.normal} ${theme.animation.easing.cubic};
  position: fixed;
  left: 0;
  top: 0;
  z-index: 1000;
  overflow-y: auto;
`;

const SidebarHeader = styled.div<{ $collapsed: boolean }>`
  padding: ${theme.spacing.lg};
  border-bottom: 1px solid ${theme.colors.border.secondary};
  display: flex;
  align-items: center;
  gap: ${theme.spacing.base};
  min-height: 72px;
`;

const Logo = styled.div`
  width: 40px;
  height: 40px;
  background: #87CEEB;
  border-radius: ${theme.borderRadius.base};
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  
  .semi-icon {
    color: white;
    font-size: 20px;
  }
`;

const BrandInfo = styled.div<{ $collapsed: boolean }>`
  display: ${props => props.$collapsed ? 'none' : 'block'};
  
  h4 {
    margin: 0;
    color: ${theme.colors.text.primary};
    font-weight: ${theme.typography.fontWeight.semibold};
    font-size: ${theme.typography.fontSize.base};
  }
  
  p {
    margin: 0;
    color: ${theme.colors.text.tertiary};
    font-size: ${theme.typography.fontSize.sm};
  }
`;

const SidebarContent = styled.div`
  flex: 1;
  padding: ${theme.spacing.base} 0;
  overflow-y: auto;
`;

const StyledNav = styled(Nav)<{ $collapsed: boolean }>`
  background: transparent;
  border: none;
  
  .semi-navigation-item {
    margin: 4px ${theme.spacing.base};
    border-radius: ${theme.borderRadius.base};
    transition: all ${theme.animation.duration.normal} ${theme.animation.easing.cubic};
    
    &:hover {
      background: ${theme.colors.bg.tertiary};
    }
    
    &.semi-navigation-item-selected {
      background: ${theme.colors.primary};
      color: white;
      
      .semi-icon {
        color: white;
      }
      
      .semi-navigation-item-text {
        color: white;
      }
    }
    
    .semi-navigation-item-text {
      display: ${props => props.$collapsed ? 'none' : 'block'};
    }
  }
  
  .semi-navigation-sub {
    .semi-navigation-item {
      padding-left: ${props => props.$collapsed ? theme.spacing.base : theme.spacing.xl};
    }
  }
`;

const SidebarFooter = styled.div<{ $collapsed: boolean }>`
  padding: ${theme.spacing.lg};
  border-top: 1px solid ${theme.colors.border.secondary};
  display: flex;
  align-items: center;
  gap: ${theme.spacing.base};
`;

const UserInfo = styled.div<{ $collapsed: boolean }>`
  display: ${props => props.$collapsed ? 'none' : 'flex'};
  flex-direction: column;
  flex: 1;
  
  .username {
    color: ${theme.colors.text.primary};
    font-weight: ${theme.typography.fontWeight.medium};
    font-size: ${theme.typography.fontSize.sm};
    margin: 0;
  }
  
  .role {
    color: ${theme.colors.text.tertiary};
    font-size: ${theme.typography.fontSize.xs};
    margin: 0;
  }
`;

const menuItems = [
  // {
  //   itemKey: "dashboard",
  //   text: "工作台",
  //   icon: <IconHome />,
  // },
  {
    itemKey: "workflows",
    text: "工作流管理",
    icon: <IconApps />,
    items: [
      {
        itemKey: "workflow-list",
        text: "工作流列表",
      },
      {
        itemKey: "workflow-instance",
        text: "工作流实例",
      },
      {
        itemKey: "workflow-execution",
        text: "工作流执行",
      },
    ],
  },
  {
    itemKey: "resources",
    text: "智能体管理",
    icon: <IconApps />,
    items: [
      {
        itemKey: "client-management",
        text: "智能体",
      },
      {
        itemKey: "rag-order-management",
        text: "知识库",
      },
      {
        itemKey: "ai-client-api-management",
        text: "模型API",
      },
      {
        itemKey: "client-tool-mcp-management",
        text: "MCP工具",
      },
    ],
  },
  // {
  //   itemKey: "analytics",
  //   text: "数据分析",
  //   icon: <IconActivity />,
  //   items: [
  //     {
  //       itemKey: "performance",
  //       text: "性能监控",
  //     },
  //     {
  //       itemKey: "usage",
  //       text: "使用统计",
  //     },
  //   ],
  // },
  // {
  //   itemKey: "settings",
  //   text: "系统设置",
  //   icon: <IconSetting />,
  //   items: [
  //     {
  //       itemKey: "profile",
  //       text: "个人设置",
  //     },
  //     {
  //       itemKey: "system",
  //       text: "系统配置",
  //     },
  //   ],
  // },
];

export const Sidebar: React.FC<SidebarProps> = ({
  selectedKey = 'dashboard',
  onSelect,
  collapsed = false,
}) => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');

  return (
    <SidebarContainer $collapsed={collapsed}>
      <SidebarHeader $collapsed={collapsed}>
        <Logo>
          <img src={agentLogo} alt="智能体" style={{ width: 24, height: 24, borderRadius: 6 }} />
        </Logo>
        <BrandInfo $collapsed={collapsed}>
          <h4>智能体开发平台</h4>
        </BrandInfo>
      </SidebarHeader>

      <SidebarContent>
        <StyledNav
          $collapsed={collapsed}
          selectedKeys={[selectedKey]}
          onSelect={({ selectedKeys }: { selectedKeys: string[] }) => {
            const key = selectedKeys[0] as string;
            onSelect?.(key);
          }}
          items={menuItems}
        />
      </SidebarContent>

      <SidebarFooter $collapsed={collapsed}>
        <Avatar size="small" color="blue">
          {userInfo.username?.[0]?.toUpperCase() || "U"}
        </Avatar>
        <UserInfo $collapsed={collapsed}>
          <Text className="username">{userInfo.username || "用户"}</Text>
          {/* <Text className="role">管理员</Text> */}
        </UserInfo>
      </SidebarFooter>
    </SidebarContainer>
  );
};