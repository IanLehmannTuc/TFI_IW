import React from 'react';
import { HashRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Layout from './components/Layout';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Admission from './pages/Admission';
import Queue from './pages/Queue';
import AttendPatient from './pages/AttendPatient';
import AdmissionsHistory from './pages/AdmissionsHistory';
import { UserRole } from './types';

const App: React.FC = () => {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          
          <Route path="/" element={
            <ProtectedRoute>
              <Layout>
                <Dashboard />
              </Layout>
            </ProtectedRoute>
          } />

          <Route path="/admission" element={
            <ProtectedRoute allowedRoles={[UserRole.ENFERMERO]}>
              <Layout>
                <Admission />
              </Layout>
            </ProtectedRoute>
          } />

          <Route path="/queue" element={
            <ProtectedRoute>
              <Layout>
                <Queue />
              </Layout>
            </ProtectedRoute>
          } />

           <Route path="/admissions" element={
            <ProtectedRoute>
              <Layout>
                <AdmissionsHistory />
              </Layout>
            </ProtectedRoute>
          } />

          <Route path="/attend" element={
            <ProtectedRoute allowedRoles={[UserRole.MEDICO]}>
              <Layout>
                <AttendPatient />
              </Layout>
            </ProtectedRoute>
          } />

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
};

export default App;