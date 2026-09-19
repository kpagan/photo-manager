import { useEffect, useState } from 'react';
import { fetchDashboardData, startFolderScan, fetchScanStatus } from '../service/dashboardService';
import { type DashboardDto } from '../model/DashboardDto';
import type { ScanStatusDto } from '../model/ScanStatusDto';

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
    setScanMessage('Starting the folder scan in the background...');

    try {
      const response = await startFolderScan();
      const message = await response.text();
      setScanState('running');
      setScanMessage(message || 'The folder scan job was started successfully.');
      await pollScanStatus();
    } catch(e) {
      console.log(e);
      if (e instanceof Error) {
        setScanState('error');
        setScanMessage(`The scan request could not be sent. Check your backend connection and API URL. Error: ${e.message}`);
      }
    }
  };

  const pollScanStatus = async () => {
    let intervalId : number | undefined = undefined;
    try {
      intervalId = setInterval(async () => {
        const statusDto: ScanStatusDto = await fetchScanStatus();
        if (statusDto.running) {
          setScanState('running');
          setScanMessage('Remaining photos to be added in library: ' + statusDto.numberOfPhotos);
        } else {
          setScanState('success');
          setScanMessage('The folder scan job has completed successfully.');
          clearInterval(intervalId);
        }
      }, 1000);
    } catch (e) {
      console.log(e);
      if (intervalId !== undefined) {
        clearInterval(intervalId);
      }
      if (e instanceof Error) {
        setScanState('error');
        setScanMessage(`Error while polling scan status: ${e.message}`);
      }
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
