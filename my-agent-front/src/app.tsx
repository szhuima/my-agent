import React, {useContext, useEffect} from 'react';
import {createRoot} from 'react-dom/client';
import {BrowserRouter as Router, Navigate, Route, Routes} from 'react-router-dom';
import {
    AgentList,
    AiClientApiManagement,
    LoginPage,
    McpList,
    RagOrderManagement,
    WorkflowCreatePage,
    WorkflowExecutionPage,
    WorkflowListPage
} from './pages';
import {ThemeContext, ThemeProvider} from './context/theme-context';
import './styles/theme.css';

// 统一的认证检查函数
const isAuthenticated = (): boolean => {
  const token = localStorage.getItem('token');
  const userInfo = localStorage.getItem('userInfo');
  const isLoggedIn = localStorage.getItem('isLoggedIn');
  
  // 检查所有必要的认证信息是否存在
  return !!(token && userInfo && isLoggedIn);
};

// 路由保护组件
const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  return isAuthenticated() ? <>{children}</> : <Navigate to="/login" replace />;
};

// 登录重定向组件
const LoginRedirect: React.FC = () => {
  return isAuthenticated() ? (
    <Navigate to="/workflow-list" replace />
  ) : (
    <LoginPage />
  );
};

// 主题应用组件
const ThemeApp: React.FC = () => {
  const { theme } = useContext(ThemeContext);

  useEffect(() => {
    document.body.className = theme;
  }, [theme]);

  return (
    <Router>
      <Routes>
        <Route path="/login" element={<LoginRedirect />} />
        {/* <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <DashboardPage />
            </ProtectedRoute>
          }
        /> */}
        <Route
          path="/workflow-list"
          element={
            <ProtectedRoute>
              <WorkflowListPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/workflow-create"
          element={
            <ProtectedRoute>
              <WorkflowCreatePage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/workflow-execution"
          element={
            <ProtectedRoute>
              <WorkflowExecutionPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/client-management"
          element={
            <ProtectedRoute>
              <AgentList />
            </ProtectedRoute>
          }
        />
        <Route
          path="/ai-client-api-management"
          element={
            <ProtectedRoute>
              <AiClientApiManagement />
            </ProtectedRoute>
          }
        />
        <Route
          path="/rag-order-management"
          element={
            <ProtectedRoute>
              <RagOrderManagement />
            </ProtectedRoute>
          }
        />
      
        <Route
          path="/client-tool-mcp-management"
          element={
            <ProtectedRoute>
              <McpList />
            </ProtectedRoute>
          }
        />
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </Router>
  );
};

const App: React.FC = () => {
  return (
    <ThemeProvider>
      <ThemeApp />
    </ThemeProvider>
  );
};

const app = createRoot(document.getElementById('root')!);

app.render(<App />);

export default App;
