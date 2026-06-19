// api.ts
const BASE_URL = 'http://localhost:8080/api';

export function getAuthToken(): string | null {
  if (typeof localStorage !== 'undefined') {
    return localStorage.getItem('jwt');
  }
  return null;
}

export function setAuthToken(token: string) {
  if (typeof localStorage !== 'undefined') {
    localStorage.setItem('jwt', token);
  }
}

export function removeAuthToken() {
  if (typeof localStorage !== 'undefined') {
    localStorage.removeItem('jwt');
  }
}

async function request(endpoint: string, options: RequestInit = {}) {
  const token = getAuthToken();
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string>)
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const response = await fetch(`${BASE_URL}${endpoint}`, {
    ...options,
    headers
  });

  if (!response.ok) {
    const errorBody = await response.text();
    throw new Error(errorBody || 'API Request failed');
  }

  return response.json().catch(() => ({}));
}

export const api = {
  get: (endpoint: string) => request(endpoint, { method: 'GET' }),
  post: (endpoint: string, body: any) => request(endpoint, { method: 'POST', body: JSON.stringify(body) }),
  put: (endpoint: string, body: any) => request(endpoint, { method: 'PUT', body: JSON.stringify(body) }),
  delete: (endpoint: string) => request(endpoint, { method: 'DELETE' })
};
