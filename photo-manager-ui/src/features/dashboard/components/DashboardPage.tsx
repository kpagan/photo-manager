import type { AppConfig } from '../../../services/appService';
import type { ScanStatus } from '../hooks/useDashboard';

type DashboardPageProps = {
  config: AppConfig | null;
  loading: boolean;
  error: string | null;
  scanState: ScanStatus;
  scanMessage: string;
  onStartScan: () => void;
  backendStatus: string;
};

function DashboardPage({
  config,
  loading,
  error,
  scanState,
  scanMessage,
  onStartScan,
  backendStatus,
}: DashboardPageProps) {
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
          <div className="status-pill">{backendStatus}</div>
        </header>

        <section className="hero-card">
          <div>
            <h2>Keep your photo library organized</h2>
            <p>
              This dashboard loads configuration from your backend and lets you launch a folder scan job in the
              background.
            </p>
          </div>
          <button type="button" className="scan-button" onClick={onStartScan} disabled={scanState === 'running'}>
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

export default DashboardPage;
