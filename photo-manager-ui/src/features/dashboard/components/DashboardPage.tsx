import type { ScanStatus } from '../hooks/useDashboard';
import type { DashboardDto } from '../model/DashboardDto';

type DashboardPageProps = {
  dashboardInfo: DashboardDto | null;
  loading: boolean;
  error: string | null;
  scanState: ScanStatus;
  scanMessage: string;
  onStartScan: () => void;
};

function DashboardPage({
  dashboardInfo,
  loading,
  error,
  scanState,
  scanMessage,
  onStartScan,
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
          <div className="status-pill">Pending</div>
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
            <h3>Application information</h3>
            {loading ? (
              <p>Loading information...</p>
            ) : 
            <>
              {dashboardInfo ? (
                <dl>
                  <div>
                    <dt>Photo folders</dt>
                    {dashboardInfo.photoFolders.map((folder) => (
                      <dd key={folder}>{folder}</dd>
                    ))}
                  </div>
                  <div>
                    <dt>Total photos</dt>
                    <dd>{dashboardInfo.photosNumbers}</dd>
                  </div>
                  <div>
                    <dt>Duplicate photos</dt>
                    <dd>{dashboardInfo.duplicates}</dd>
                  </div>
                  <div>
                    <dt>Similar photos</dt>
                    <dd>{dashboardInfo.similarDuplicates}</dd>
                  </div>
                </dl>
              ) : (
                error ? <p className="helper-text">{error}</p> : null
              )}
            </>}
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
