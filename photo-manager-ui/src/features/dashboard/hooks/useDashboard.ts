import { useEffect, useState } from 'react';
import { startFolderScan } from '../../../services/appService';
import { fetchDashboardData } from '../service/dashboardService';
import { type DashboardDto } from '../model/DashboardDto';

export type ScanStatus = 'idle' | 'running' | 'success' | 'error';

export function useDashboard() {
  const [dashboard, setDashboard] = useState<DashboardDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [scanState, setScanState] = useState<ScanStatus>('idle');
  const [scanMessage, setScanMessage] = useState('');

  useEffect(() => {
    let isMounted = true;

    const loadConfig = async () => {
      try {
        const data: DashboardDto = await fetchDashboardData();

        if (isMounted) {
          setDashboard(data);
          setError(null);
        }
      } catch {
        if (isMounted) {
          setDashboard(null);
          setError('Unable to reach the backend application. Verify the server is running and the UI is configured correctly and that there is no blocking communication between frontend and backend.');
        }
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    };

    void loadConfig();

    return () => {
      isMounted = false;
    };
  }, []);

  const handleStartScan = async () => {
    setScanState('running');
    setScanMessage('Starting the folder scan in the background...');

    try {
      const response = await startFolderScan();
      setScanState('success');
      setScanMessage(response.message || 'The folder scan job was started successfully.');
    } catch {
      setScanState('error');
      setScanMessage('The scan request could not be sent. Check your backend connection and API URL.');
    }
  };

  return {
    dashboard,
    loading,
    error,
    scanState,
    scanMessage,
    handleStartScan,
  };
}
