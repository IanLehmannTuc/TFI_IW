import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { User, UserRole, AuthResponse } from '../types';
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

  useEffect(() => {
    const initAuth = async () => {
      const storedToken = localStorage.getItem('token');
      if (storedToken) {
        try {
           // We try to fetch the profile to validate token
           const profile = await apiRequest<User>('/auth/perfil');
           setUser(profile);
           setToken(storedToken);
        } catch (error) {
           console.error("Token invalid", error);
           logout();
        }
      }
      setIsLoading(false);
    };

    initAuth();
  }, []);

  const login = (data: AuthResponse) => {
    localStorage.setItem('token', data.token);
    setToken(data.token);
    // Construct user from response data mostly, or fetch profile
    const minimalUser: User = {
        id: 'current', // Backend doesn't return ID on login, but we need structure
        email: data.email,
        autoridad: data.autoridad
    };
    setUser(minimalUser);

    // Fetch full profile to ensure we have name and surname
    apiRequest<User>('/auth/perfil').then(profile => {
      setUser(profile);
    }).catch(e => console.error("Could not fetch full profile details", e));
  };

  const logout = () => {
    localStorage.removeItem('token');
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