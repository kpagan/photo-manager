import { NavLink } from 'react-router-dom';
import { useDuplicates } from '../hooks/useDuplicates';

function DuplicatesPage() {

  const { duplicates, loading, error } = useDuplicates();
  
  return (
    <>
      <header className="topbar">
        <div>
          <p className="eyebrow">Duplicates</p>
          <h1>Review photo matches</h1>
        </div>
        <div className="status-pill">Ready</div>
      </header>

      <section className="hero-card">
        <div>
          <h2>Duplicate review is now available in the app</h2>
          <p>
            This view is ready for the duplicate-photo experience and can be extended with backend-driven results.
          </p>
        </div>
        <NavLink to="/" className="scan-button">
          Back to dashboard
        </NavLink>
      </section>

      <section className="hero-card">
        <article className="card">
          <h3>Duplicate photos</h3>
          {loading ? (
              <p>Loading information...</p>
            ) : (
              <>
                {duplicates.length > 0 ? (
                  <div className="duplicate-groups">
                    {duplicates.map((duplicate) => (
                      <article className="duplicate-group" key={duplicate.duplicates[0]?.id}>
                        <h4>Duplicate photo</h4>
                        <div className="duplicate-photos">
                          {duplicate.duplicates.map((photo) => (
                            <div className="duplicate-photo" key={photo.id}>
                              <div>Filename: {photo.filename}</div>
                              <div>Path: {photo.absolutePath}</div>
                              <div>Size: {photo.fileSize} bytes</div>
                              <div>Dimensions: {photo.width}x{photo.height}</div>
                              <div>Date taken: {photo.dateTaken}</div>
                              {photo.exactMatch !== undefined && (
                                <div>Exact match: {photo.exactMatch ? 'Yes' : 'No'}</div>
                              )}
                            </div>
                          ))}
                        </div>
                      </article>
                    ))}
                  </div>
                ) : (
                  error ? <p className="helper-text">{error}</p> : null
                )}
              </>
            )}
        </article>
      </section>
    </>
  );
}

export default DuplicatesPage;