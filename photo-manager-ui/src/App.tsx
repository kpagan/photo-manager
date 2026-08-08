import './App.css';
import DashboardPage from './features/dashboard/components/DashboardPage';
import { useDashboard } from './features/dashboard/hooks/useDashboard';

function App() {
  const { config, loading, error, scanState, scanMessage, handleStartScan } = useDashboard();

  return (
    <DashboardPage
      config={config}
      loading={loading}
      error={error}
      scanState={scanState}
      scanMessage={scanMessage}
      onStartScan={handleStartScan}
      backendStatus={config?.backendStatus ?? 'loading'}
    />
  );
}

export default App;
