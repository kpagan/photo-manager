import { NavLink, Outlet } from 'react-router-dom';

function AppLayout() {
  return (
    <div className="dashboard-shell">
      <aside className="sidebar">
        <div>
          <div className="brand">Photo Manager</div>
          <p className="brand-subtitle">Manage your photo collection.</p>
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
          <h3>Backend</h3>
          <p>Connect the UI to your Spring Boot API through the VITE_API_BASE_URL environment variable.</p>
        </div>
      </aside>

      <main className="main-panel">
        <Outlet />
      </main>
    </div>
  );
}

export default AppLayout;
