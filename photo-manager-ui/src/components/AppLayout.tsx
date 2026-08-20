import type { ReactNode } from 'react';
import { NavLink } from 'react-router-dom';

type AppLayoutProps = {
  children: ReactNode;
  brandSubtitle: string;
  sidebarCardTitle: string;
  sidebarCardDescription: string;
};

function AppLayout({
  children,
  brandSubtitle,
  sidebarCardTitle,
  sidebarCardDescription,
}: AppLayoutProps) {
  return (
    <div className="dashboard-shell">
      <aside className="sidebar">
        <div>
          <div className="brand">Photo Manager</div>
          <p className="brand-subtitle">{brandSubtitle}</p>
        </div>

        <nav className="nav-links" aria-label="Primary navigation">
          <NavLink to="/" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            Dashboard
          </NavLink>
          <NavLink to="/duplicates" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            Duplicate photos
          </NavLink>
          <button type="button" className="nav-link disabled">
            More options coming soon
          </button>
        </nav>

        <div className="sidebar-card">
          <h3>{sidebarCardTitle}</h3>
          <p>{sidebarCardDescription}</p>
        </div>
      </aside>

      <main className="main-panel">{children}</main>
    </div>
  );
}

export default AppLayout;
