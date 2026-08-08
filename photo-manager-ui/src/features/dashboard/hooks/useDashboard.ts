import { useEffect, useState } from 'react';
import { fetchAppConfig, startFolderScan, type AppConfig } from '../../../services/appService';

export type ScanStatus = 'idle' | 'running' | 'success' | 'error';

const fallbackConfig: AppConfig = {
  appName: 'Photo Manager',
  version: '1.0.0',
  backendStatus: 'offline',
  description: 'Using local placeholder values until your Spring Boot API is reachable.',
  features: ['Duplicate photo detection', 'Folder scan orchestration'],
};

export function useDashboard() {
  const [config, setConfig] = useState<AppConfig | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [scanState, setScanState] = useState<ScanStatus>('idle');
  const [scanMessage, setScanMessage] = useState('');

  useEffect(() => {
    let isMounted = true;

    const loadConfig = async () => {
      try {
        const data = await fetchAppConfig();

        if (isMounted) {
          setConfig(data);
          setError(null);
        }
      } catch {
        if (isMounted) {
          setConfig(fallbackConfig);
          setError('Unable to reach the backend yet. Set VITE_API_BASE_URL to your Spring Boot API base URL.');
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
    config,
    loading,
    error,
    scanState,
    scanMessage,
    handleStartScan,
  };
}
