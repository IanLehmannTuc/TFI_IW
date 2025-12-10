import { API_BASE_URL } from '../constants';
import { ErrorResponse } from '../types';

interface RequestOptions extends RequestInit {
  headers?: Record<string, string>;
}

export class ApiError extends Error {
  status: number;
  timestamp?: string;

  constructor(message: string, status: number, timestamp?: string) {
    super(message);
    this.status = status;
    this.timestamp = timestamp;
  }
}

const getHeaders = (): Record<string, string> => {
  const token = localStorage.getItem('token');
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
  };
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  return headers;
};

export const apiRequest = async <T>(endpoint: string, options: RequestOptions = {}): Promise<T> => {
  const url = `${API_BASE_URL}${endpoint}`;
  const headers = { ...getHeaders(), ...options.headers };

  try {
    const response = await fetch(url, { ...options, headers });


    if (response.status === 401 && !endpoint.includes('/auth/login')) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.hash = '#/login';
      throw new ApiError('Sesión expirada', 401);
    }

    if (response.status === 204) {
        return {} as T;
    }

    if (!response.ok) {
      let errorMessage = `Error ${response.status}`;
      let timestamp = undefined;

      try {
        const errorText = await response.text();
        if (errorText) {
             try {
                 const errorJson = JSON.parse(errorText);


                 if (errorJson.mensaje) {
                     errorMessage = errorJson.mensaje;
                     timestamp = errorJson.timestamp;
                 }

                 else if (errorJson.message) {
                     errorMessage = errorJson.message;
                 } else if (errorJson.error && typeof errorJson.error === 'string') {
                     errorMessage = errorJson.error;
                 }


                 if (errorJson.errors && Array.isArray(errorJson.errors)) {
                     const validationMessages = errorJson.errors
                        .map((e: any) => e.message || e.defaultMessage)
                        .filter(Boolean)
                        .join(', ');

                     if (validationMessages) {
                         errorMessage = validationMessages;
                     }
                 }
             } catch {
                 if (!errorText.trim().startsWith('<') && errorText.length < 300) {
                     errorMessage = errorText;
                 }
             }
        }
      } catch (e) {
          console.error("Error reading error response", e);
      }

      throw new ApiError(errorMessage, response.status, timestamp);
    }

    return await response.json();
  } catch (error) {
    if (error instanceof ApiError) throw error;
    throw new Error(error instanceof Error ? error.message : 'Error de red desconocido');
  }
};