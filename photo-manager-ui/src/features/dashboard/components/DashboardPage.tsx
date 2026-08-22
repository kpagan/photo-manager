import { useDashboard } from '../hooks/useDashboard';

function DashboardPage() {
  const { dashboard, loading, error, scanState, scanMessage, handleStartScan } = useDashboard();

  return (
    <>
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
          <button type="button" className="scan-button" onClick={handleStartScan} disabled={scanState === 'running'}>
            {scanState === 'running' ? 'Starting scan...' : 'Start folder scan'}
          </button>
        </section>

        <section className="content-grid">
          <article className="card">
            <h3>Application information</h3>
            {loading ? (
              <p>Loading information...</p>
            ) : (
              <>
                {dashboard ? (
                  <dl>
                    <div>
                      <dt>Photo folders</dt>
                      {dashboard.photoFolders.map((folder) => (
                        <dd key={folder}>{folder}</dd>
                      ))}
                    </div>
                    <div>
                      <dt>Total photos</dt>
                      <dd>{dashboard.photosNumbers}</dd>
                    </div>
                    <div>
                      <dt>Duplicate photos</dt>
                      <dd>{dashboard.duplicates}</dd>
                    </div>
                    <div>
                      <dt>Similar photos</dt>
                      <dd>{dashboard.similarDuplicates}</dd>
                    </div>
                  </dl>
                ) : (
                  error ? <p className="helper-text">{error}</p> : null
                )}
              </>
            )}
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
    </>
  );
}

export default DashboardPage;
