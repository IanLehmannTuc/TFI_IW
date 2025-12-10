import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { User, AuthResponse } from '../types';
import { apiRequest } from '../services/api';

interface AuthContextType {
  user: User | null;
  token: string | null;
  isLoading: boolean;
  login: (data: AuthResponse) => void;
  logout: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(localStorage.getItem('token'));
  const [isLoading, setIsLoading] = useState<boolean>(true);

  const fetchProfile = async () => {
    try {
       // IS2025-005: Load profile to get CUIL/Matricula
       const profile = await apiRequest<User>('/auth/perfil');
       setUser(profile);
    } catch (error) {
       console.error("Could not fetch full profile details", error);
       // If profile fetch fails, user might be invalid or token expired
       logout();
    }
  };

  useEffect(() => {
    const initAuth = async () => {
      const storedToken = localStorage.getItem('token');
      if (storedToken) {
         setToken(storedToken);
         await fetchProfile();
      }
      setIsLoading(false);
    };

    initAuth();
  }, []);

  const login = (data: AuthResponse) => {
    localStorage.setItem('token', data.token);
    setToken(data.token);
    
    // Set minimal user initially to allow UI to react, but immediately fetch full profile
    setUser({
        id: 'current',
        email: data.email,
        autoridad: data.autoridad,
        // Name and CUIL will come from profile
    });

    // Immediately fetch full profile to fill CUIL and matricula
    fetchProfile();
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('activePatientId'); // Clear active session if any
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ 
        user, 
        token, 
        isLoading, 
        login, 
        logout, 
        isAuthenticated: !!token 
    }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};