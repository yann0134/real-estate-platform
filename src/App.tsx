import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import { API_BASE_URL } from './config';
import axios from 'axios';

// Configuration d'Axios
axios.defaults.baseURL = API_BASE_URL;
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Composant de débogage
const DebugInfo = () => {
  const { isAuthenticated, loading, user } = useAuth();
  const location = useLocation();
  
  return (
    <div style={{ 
      position: 'fixed', 
      bottom: 0, 
      left: 0, 
      right: 0, 
      background: 'rgba(0,0,0,0.8)', 
      color: 'white', 
      padding: '10px',
      zIndex: 1000,
      fontSize: '12px',
      fontFamily: 'monospace'
    }}>
      <div>Route actuelle: {location.pathname}</div>
      <div>Authentifié: {isAuthenticated ? 'Oui' : 'Non'}</div>
      <div>Chargement: {loading ? 'Oui' : 'Non'}</div>
      <div>Utilisateur: {user ? user.email : 'Aucun'}</div>
      <div>Token: {localStorage.getItem('token') ? 'Présent' : 'Absent'}</div>
    </div>
  );
};

// Pages
const Login = React.lazy(() => import('./pages/Login'));
const Register = React.lazy(() => import('./pages/Register'));
const Dashboard = React.lazy(() => import('./pages/Dashboard'));
const Properties = React.lazy(() => import('./pages/PropertiesPage'));
const PropertyDetail = React.lazy(() => import("./pages/PropertyDetail.tsx"));
const Appointments = React.lazy(() => import('./pages/Appointments'));
const Profile = React.lazy(() => import('./pages/Profile'));

// Composant de route protégée
const PrivateRoute: React.FC<{ element: React.ReactNode }> = ({ element }) => {
  const { isAuthenticated, loading } = useAuth();
  
  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-indigo-500"></div>
      </div>
    );
  }
  
  return isAuthenticated ? (
    <>{element}</>
  ) : (
    <Navigate to="/login" state={{ from: window.location.pathname }} replace />
  );
};

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="min-h-screen bg-gray-50">
          <React.Suspense fallback={
            <div className="flex justify-center items-center min-h-screen">
              <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-indigo-500"></div>
            </div>
          }>
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              
              {/* Routes protégées */}
              <Route path="/" element={
                <PrivateRoute element={<Dashboard />} />
              } />
              <Route path="/properties" element={
                <PrivateRoute element={<Properties />} />
              } />
              <Route path="/properties/:id" element={
                <PrivateRoute element={<PropertyDetail />} />
              } />
              <Route path="/appointments" element={
                <PrivateRoute element={<Appointments />} />
              } />
              <Route path="/profile" element={
                <PrivateRoute element={<Profile />} />
              } />
            </Routes>
            
            {/* Composant de débogage */}
            {process.env.NODE_ENV === 'development' && <DebugInfo />}
          </React.Suspense>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
