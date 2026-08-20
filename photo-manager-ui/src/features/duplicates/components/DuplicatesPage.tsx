import AppLayout from '../../../components/AppLayout';
import { NavLink } from 'react-router-dom';

type DuplicatesPageProps = DuplicatesDto[];

type DuplicatesDto = {
    duplicates: DuplicateDto[]
};

type DuplicateDto = {
    id: number;
    filename: string;
    absolutePath: string;
    fileSize: number;
    sha256: string;
    perceptualHash: number;
    dateTaken: string;
    width: number;
    height: number;
    exactMatch: boolean;
};


function DuplicatesPage() {
  return (
    <AppLayout
      brandSubtitle="Review duplicate and similar photos in your collection."
      sidebarCardTitle="Duplicates"
      sidebarCardDescription="Use this screen to review photo groups once the backend exposes duplicate data."
    >
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

      <section className="content-grid">
        <article className="card">
          <h3>What to expect</h3>
          <p className="helper-text">
            Connect the API to list duplicate groups, keep decisions, and review photos side by side.
          </p>
        </article>

        <article className="card">
          <h3>Next step</h3>
          <p className="helper-text">
            The navigation is wired up; the remaining work is to populate this page from your backend service.
          </p>
        </article>
      </section>
    </AppLayout>
  );
}

export default DuplicatesPage;