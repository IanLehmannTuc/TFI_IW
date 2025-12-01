import { API_BASE_URL } from '../constants';

interface RequestOptions extends RequestInit {
  headers?: Record<string, string>;
}

export class ApiError extends Error {
  status: number;
  constructor(message: string, status: number) {
    super(message);
    this.status = status;
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

    if (response.status === 401) {
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
      
      try {
        const errorText = await response.text();
        if (errorText) {
             try {
                 const errorJson = JSON.parse(errorText);
                 
                 // 0. Check for specific 'mensaje' field (User custom API format)
                 if (errorJson.mensaje) {
                     errorMessage = errorJson.mensaje;
                 }
                 // 1. Check for standard 'message' field
                 else if (errorJson.message) {
                     errorMessage = errorJson.message;
                 } 
                 // 2. Check for 'error' field (sometimes used as message or type)
                 else if (errorJson.error && typeof errorJson.error === 'string') {
                     errorMessage = errorJson.error;
                 }
                 
                 // 3. Check for array of validation errors (common in Spring Boot @Valid)
                 if (errorJson.errors && Array.isArray(errorJson.errors)) {
                     const validationMessages = errorJson.errors
                        .map((e: any) => e.message || e.defaultMessage || e.field + ' inválido')
                        .filter(Boolean)
                        .join(', ');
                     
                     if (validationMessages) {
                         // If we have specific validation messages, use them
                         errorMessage = validationMessages;
                     }
                 }
             } catch {
                 // If not JSON, use text if it's short/readable (avoid printing full HTML pages)
                 if (!errorText.trim().startsWith('<') && errorText.length < 300) {
                     errorMessage = errorText;
                 }
             }
        }
      } catch (e) {
          console.error("Error reading error response", e);
      }
      
      throw new ApiError(errorMessage, response.status);
    }

    return await response.json();
  } catch (error) {
    if (error instanceof ApiError) throw error;
    throw new Error(error instanceof Error ? error.message : 'Error de red desconocido');
  }
};