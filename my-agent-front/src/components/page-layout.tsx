import styled from "styled-components";
import {Button, Card, Layout} from "@douyinfe/semi-ui";
import {theme} from "../styles/theme";

const { Content } = Layout;

export const PageLayout = styled(Layout)`
  min-height: 100vh;
  background: ${theme.colors.bg.secondary};
`;

export const MainContent = styled.div<{ $collapsed: boolean }>`
  display: flex;
  flex: 1;
  margin-left: ${(props) => (props.$collapsed ? "80px" : "280px")};
  transition: margin-left ${theme.animation.duration.normal}
    ${theme.animation.easing.cubic};
`;

export const ContentArea = styled(Content)`
  flex: 1;
  padding: ${theme.spacing.lg};
  background: ${theme.colors.bg.secondary};
  overflow-y: auto;
`;

export const PageHeader = styled.div`
  padding: ${theme.spacing.lg};
  border-bottom: 1px solid ${theme.colors.border.secondary};
`;

export const SearchSection = styled(Card)`
  margin: ${theme.spacing.lg};

  .semi-card-body {
    padding: ${theme.spacing.lg};
  }
`;

export const SearchRow = styled.div`
  display: flex;
  align-items: center;
  gap: ${theme.spacing.base};
  flex-wrap: wrap;
`;

export const TableContainer = styled.div`
  flex: 1;
  margin: 0 ${theme.spacing.lg} ${theme.spacing.lg};
  display: flex;
  flex-direction: column;
`;

export const TableCard = styled(Card)`
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

export const TableWrapper = styled.div`
  flex: 1;
  overflow: auto;
`;

export const ActionButton = styled(Button)`
  margin-right: ${theme.spacing.sm};
`;

export default {
  PageLayout,
  MainContent,
  ContentArea,
  PageHeader,
  SearchSection,
  SearchRow,
  TableContainer,
  TableCard,
  TableWrapper,
  ActionButton,
};