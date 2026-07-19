import React, { useEffect, useState, useCallback } from 'react';
import { Routes, Route, Navigate, useLocation } from 'react-router-dom';
import AuthPage from './components/auth/AuthPage';
import DashboardPage from './components/dashboard/DashboardPage';
import ProfilePage from './components/profile/ProfilePage';
import AccountsPage from './components/accounts/AccountsPage';
import AppLayout from './components/common/AppLayout';
import { authApi, clearAccessToken, setAccessToken } from './services/api';

const PlaceholderPage = ({ title, children }) => (
  <div className="p-4 lg:p-6">
    <h1 className="text-2xl font-bold text-gray-900 mb-4">{title}</h1>
    <p className="text-gray-500">Coming soon...</p>
    {children}
  </div>
);

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
  const [userName, setUserName] = useState('');

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
        setUserName(response.data.user?.fullName || '');
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

  const handleLoginSuccess = useCallback((userData) => {
    setUserName(userData?.fullName || '');
    setIsAuthenticated(true);
  }, []);

  const handleLogout = useCallback(() => {
    setUserName('');
    setIsAuthenticated(false);
  }, []);

  return (
    <Routes>
      <Route
        path="/login"
        element={
          isAuthenticated ? (
            <Navigate to="/dashboard" replace />
          ) : (
            <AuthPage onLoginSuccess={handleLoginSuccess} />
          )
        }
      />
      <Route
        path="/dashboard"
        element={
          <ProtectedRoute isAuthenticated={isAuthenticated} isLoading={isLoading}>
            <AppLayout onLogout={handleLogout} userName={userName}>
              <DashboardPage />
            </AppLayout>
          </ProtectedRoute>
        }
      />
      <Route
        path="/transactions"
        element={
          <ProtectedRoute isAuthenticated={isAuthenticated} isLoading={isLoading}>
            <AppLayout onLogout={handleLogout} userName={userName}>
              <PlaceholderPage title="Transactions" />
            </AppLayout>
          </ProtectedRoute>
        }
      />
      <Route
        path="/budgets"
        element={
          <ProtectedRoute isAuthenticated={isAuthenticated} isLoading={isLoading}>
            <AppLayout onLogout={handleLogout} userName={userName}>
              <PlaceholderPage title="Budgets" />
            </AppLayout>
          </ProtectedRoute>
        }
      />
      <Route
        path="/goals"
        element={
          <ProtectedRoute isAuthenticated={isAuthenticated} isLoading={isLoading}>
            <AppLayout onLogout={handleLogout} userName={userName}>
              <PlaceholderPage title="Goals" />
            </AppLayout>
          </ProtectedRoute>
        }
      />
      <Route
        path="/settings"
        element={
          <ProtectedRoute isAuthenticated={isAuthenticated} isLoading={isLoading}>
            <AppLayout onLogout={handleLogout} userName={userName}>
              <PlaceholderPage title="Settings" />
            </AppLayout>
          </ProtectedRoute>
        }
      />
      <Route
        path="/accounts"
        element={
          <ProtectedRoute isAuthenticated={isAuthenticated} isLoading={isLoading}>
            <AppLayout onLogout={handleLogout} userName={userName}>
              <AccountsPage />
            </AppLayout>
          </ProtectedRoute>
        }
      />
      <Route
        path="/profile"
        element={
          <ProtectedRoute isAuthenticated={isAuthenticated} isLoading={isLoading}>
            <AppLayout onLogout={handleLogout} userName={userName}>
              <ProfilePage onLogout={handleLogout} />
            </AppLayout>
          </ProtectedRoute>
        }
      />
      <Route path="/" element={<Navigate to={isAuthenticated ? '/dashboard' : '/login'} replace />} />
      <Route path="*" element={<Navigate to={isAuthenticated ? '/dashboard' : '/login'} replace />} />
    </Routes>
  );
}

export default App;