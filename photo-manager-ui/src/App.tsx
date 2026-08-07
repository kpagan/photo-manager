import { useEffect, useState } from 'react';
import './App.css';

type AppConfig = {
  appName: string;
  version: string;
  backendStatus: string;
  description: string;
  features: string[];
};

type ScanResponse = {
  message: string;
  jobId?: string;
  status?: string;
};

const DEFAULT_API_BASE_URL = 'http://localhost:8080/api';

async function requestJson<T>(path: string, init?: RequestInit): Promise<T> {
  const baseUrl = import.meta.env.VITE_API_BASE_URL ?? DEFAULT_API_BASE_URL;
  const response = await fetch(`${baseUrl}${path}`, {
    headers: {
      'Content-Type': 'application/json',
    },
    ...init,
  });

  if (!response.ok) {
    throw new Error(`Request failed with ${response.status}`);
  }

  return response.json() as Promise<T>;
}

function App() {
  const [config, setConfig] = useState<AppConfig | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [scanState, setScanState] = useState<'idle' | 'running' | 'success' | 'error'>('idle');
  const [scanMessage, setScanMessage] = useState('');

  useEffect(() => {
    let isMounted = true;

    const loadConfig = async () => {
      try {
        const data = await requestJson<AppConfig>('/config');
        if (isMounted) {
          setConfig(data);
          setError(null);
        }
      } catch (err) {
        if (isMounted) {
          setConfig({
            appName: 'Photo Manager',
            version: '1.0.0',
            backendStatus: 'offline',
            description: 'Using local placeholder values until your Spring Boot API is reachable.',
            features: ['Duplicate photo detection', 'Folder scan orchestration'],
          });
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
      const response = await requestJson<ScanResponse>('/folder-scan/start', {
        method: 'POST',
      });

      setScanState('success');
      setScanMessage(response.message || 'The folder scan job was started successfully.');
    } catch (err) {
      setScanState('error');
      setScanMessage('The scan request could not be sent. Check your backend connection and API URL.');
    }
  };

  return (
    <div className="dashboard-shell">
      <aside className="sidebar">
        <div>
          <div className="brand">Photo Manager</div>
          <p className="brand-subtitle">Manage photo collections with a Spring Boot backend.</p>
        </div>

        <nav className="nav-links" aria-label="Primary navigation">
          <button type="button" className="nav-link active">
            Duplicate photos
          </button>
          <button type="button" className="nav-link disabled">
            More options coming soon
          </button>
        </nav>

        <div className="sidebar-card">
          <h3>Backend</h3>
          <p>Connect the UI to your Spring Boot API through the VITE_API_BASE_URL environment variable.</p>
        </div>
      </aside>

      <main className="main-panel">
        <header className="topbar">
          <div>
            <p className="eyebrow">Dashboard</p>
            <h1>Overview</h1>
          </div>
          <div className="status-pill">{config?.backendStatus ?? 'loading'}</div>
        </header>

        <section className="hero-card">
          <div>
            <h2>Keep your photo library organized</h2>
            <p>
              This dashboard loads configuration from your backend and lets you launch a folder scan job in the
              background.
            </p>
          </div>
          <button type="button" className="scan-button" onClick={handleStartScan} disabled={scanState === 'running'}>
            {scanState === 'running' ? 'Starting scan...' : 'Start folder scan'}
          </button>
        </section>

        <section className="content-grid">
          <article className="card">
            <h3>Application configuration</h3>
            {loading ? (
              <p>Loading configuration...</p>
            ) : (
              <>
                <dl>
                  <div>
                    <dt>App name</dt>
                    <dd>{config?.appName}</dd>
                  </div>
                  <div>
                    <dt>Version</dt>
                    <dd>{config?.version}</dd>
                  </div>
                  <div>
                    <dt>Description</dt>
                    <dd>{config?.description}</dd>
                  </div>
                </dl>
                <ul className="feature-list">
                  {config?.features.map((feature) => (
                    <li key={feature}>{feature}</li>
                  ))}
                </ul>
              </>
            )}
            {error ? <p className="helper-text">{error}</p> : null}
          </article>

          <article className="card">
            <h3>Background job</h3>
            <p className="helper-text">
              The button above will call a Spring Boot endpoint such as /api/folder-scan/start.
            </p>
            <div className={`status-box ${scanState}`}>
              <strong>{scanState === 'success' ? 'Job started' : scanState === 'error' ? 'Request failed' : 'Awaiting scan'}</strong>
              <p>{scanMessage || 'No scan has been started yet.'}</p>
            </div>
          </article>
        </section>
      </main>
    </div>
  );
}

export default App;
