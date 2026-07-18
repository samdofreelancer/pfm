import React, { useEffect, useState } from 'react';
import { Routes, Route, Navigate, useNavigate, useLocation } from 'react-router-dom';
import AuthPage from './components/auth/AuthPage';
import { authApi, clearAccessToken, setAccessToken } from './services/api';

const Dashboard = ({ onLogout }) => {
  const navigate = useNavigate();

  const handleLogout = async () => {
      try {
        // debug log: user initiated logout
        // eslint-disable-next-line no-console
        console.log('[app] logout: calling authApi.logout()');

        await authApi.logout();

        // eslint-disable-next-line no-console
        console.log('[app] logout: logout API success');

        clearAccessToken();
        // inform parent app that user is logged out
        try {
          onLogout?.();
        } catch (e) {}
        navigate('/login');
      } catch (err) {
        // eslint-disable-next-line no-console
        console.log('[app] logout: logout API failed', err);
        // still clear local token and redirect
        clearAccessToken();
        try {
          onLogout?.();
        } catch (e) {}
        navigate('/login');
      }
  };

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center">
      <div className="text-center">
        <h1 className="text-3xl font-bold text-gray-900 mb-4">Dashboard</h1>
        <p className="text-gray-500">Welcome to PFM! You are logged in.</p>
        <button
          onClick={handleLogout}
          className="mt-6 text-primary-600 hover:text-primary-700 font-medium"
        >
          Logout
        </button>
      </div>
    </div>
  );
};

const ProtectedRoute = ({ children, isLoading, isAuthenticated }) => {
  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center text-gray-500">Checking authentication...</div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return children;
};

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  const location = useLocation();

  useEffect(() => {
    const initializeAuth = async () => {
      if (process.env.NODE_ENV !== 'production') {
        // eslint-disable-next-line no-console
        console.debug('[app] initializeAuth: starting refresh');
      }
      try {
        const response = await authApi.refresh();
        setAccessToken(response.data.accessToken);
        setIsAuthenticated(true);
      } catch {
        clearAccessToken();
        setIsAuthenticated(false);
      } finally {
        setIsLoading(false);
      }
    };

    // Run initializeAuth once on mount. This avoids triggering refresh
    // on every route change (for example during logout navigation).
    if (location.pathname === '/login') {
      // If the user is already on login page, skip refresh.
      // eslint-disable-next-line no-console
      console.log('[app] initializeAuth: on /login at mount, skipping refresh');
      setIsLoading(false);
    } else {
      // eslint-disable-next-line no-console
      console.log('[app] initializeAuth: running refresh at mount');
      initializeAuth();
    }
  }, []); // run once on mount

  return (
    <Routes>
      <Route
        path="/login"
        element={
          isAuthenticated ? (
            <Navigate to="/dashboard" replace />
          ) : (
            <AuthPage onLoginSuccess={() => setIsAuthenticated(true)} />
          )
        }
      />
      <Route
        path="/dashboard"
        element={
          <ProtectedRoute isAuthenticated={isAuthenticated} isLoading={isLoading}>
            <Dashboard onLogout={() => setIsAuthenticated(false)} />
          </ProtectedRoute>
        }
      />
      <Route path="/" element={<Navigate to={isAuthenticated ? '/dashboard' : '/login'} replace />} />
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}

export default App;