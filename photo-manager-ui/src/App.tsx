import './App.css';
import DashboardPage from './features/dashboard/components/DashboardPage';
import { useDashboard } from './features/dashboard/hooks/useDashboard';

function App() {
  const { dashboard, loading, error, scanState, scanMessage, handleStartScan } = useDashboard();

  return (
    <DashboardPage
      dashboardInfo={dashboard}
      loading={loading}
      error={error}
      scanState={scanState}
      scanMessage={scanMessage}
      onStartScan={handleStartScan}
    />
  );
}

export default App;
