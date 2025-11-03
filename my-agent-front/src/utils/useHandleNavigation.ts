// src/utils/handleNavigation.ts
import { useNavigate } from "react-router-dom";

const useHandleNavigation = () => {
  const navigate = useNavigate();

  const handleNavigation = (path: string) => {
    switch (path) {
      case "dashboard":
        navigate("/dashboard");
        break;
      case "workflow-list":
        navigate("/workflow-list");
        break;
      case "workflow-instance":
        navigate("/workflow-instance");
        break;
      case "workflow-execution":
        navigate("/workflow-execution");
        break;
      case "agent-list":
        navigate("/agent-list");
        break;
      case "agent-config":
        navigate("/agent-config");
        break;
      case "client-management":
        navigate("/client-management");
        break;
      case "ai-client-api-management":
        navigate("/ai-client-api-management");
        break;
      case "advisor-management":
        navigate("/advisor-management");
        break;
      case "rag-order-management":
        navigate("/rag-order-management");
        break;
      case "client-model-management":
        navigate("/client-model-management");
        break;
      case "client-system-prompt-management":
        navigate("/client-system-prompt-management");
        break;
      case "client-tool-mcp-management":
        navigate("/client-tool-mcp-management");
        break;
      default:
        navigate(path);
        break;
    }
  };

  return handleNavigation;
};

export default useHandleNavigation;