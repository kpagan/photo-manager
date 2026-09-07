import { NavLink } from 'react-router-dom';
import { useDuplicates } from '../hooks/useDuplicates';
import ImageContainer from '../../image/components/ImageContainer';

function DuplicatesPage() {
  const { duplicates, loading, loadingMore, hasMore, error, totalElements, loadMore, refresh, sentinelRef } = useDuplicates();

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
            {totalElements > 0
              ? `Found ${totalElements} group${totalElements === 1 ? '' : 's'} of duplicate photos.`
              : 'This view displays backend-detected duplicate and near-duplicate photos.'}
          </p>
        </div>
        <NavLink to="/" className="scan-button">
          Back to dashboard
        </NavLink>
      </section>

      <section className="hero-card">
        <article className="card" style={{ width: '100%' }}>
          <h3>Duplicate photos</h3>
          {loading ? (
            <div className="scroll-loading-container">
              <div className="spinner" />
              <span>Loading duplicate photos...</span>
            </div>
          ) : error && duplicates.length === 0 ? (
            <div className="status-box error">
              <strong>Error loading duplicates</strong>
              <p className="helper-text">{error}</p>
              <button type="button" className="retry-button" onClick={() => refresh()}>
                Retry
              </button>
            </div>
          ) : duplicates.length > 0 ? (
            <>
              <div className="duplicate-groups">
                {duplicates.map((duplicate, index) => {
                  const primaryPhoto = duplicate.duplicates[0];
                  const key = primaryPhoto?.id ?? index;
                  return (
                    <article className="duplicate-group" key={key}>
                      <h4>Duplicate photo group #{index + 1}</h4>
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
                            <ImageContainer imageId={photo.id} />
                          </div>
                        ))}
                      </div>
                    </article>
                  );
                })}
              </div>

              {/* Sentinel element for infinite scrolling */}
              <div ref={sentinelRef} className="scroll-sentinel" />

              {/* Subsequent page loading indicator */}
              {loadingMore && (
                <div className="scroll-loading-container">
                  <div className="spinner" />
                  <span>Loading more duplicate photos...</span>
                </div>
              )}

              {/* Subsequent page error message */}
              {error && (
                <div className="status-box error" style={{ marginTop: '16px' }}>
                  <strong>Failed to load more duplicates</strong>
                  <p className="helper-text">{error}</p>
                  <button type="button" className="retry-button" onClick={() => loadMore()}>
                    Try again
                  </button>
                </div>
              )}

              {/* End of list message */}
              {!hasMore && !loadingMore && duplicates.length > 0 && (
                <p className="scroll-end-message">
                  All duplicate photo groups loaded ({duplicates.length} total)
                </p>
              )}
            </>
          ) : (
            <p className="helper-text">No duplicate photos found.</p>
          )}
        </article>
      </section>
    </>
  );
}

export default DuplicatesPage;