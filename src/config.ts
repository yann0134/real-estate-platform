// Configuration de l'API
export const API_BASE_URL = 'http://localhost:8080/api';

export const ENDPOINTS = {
  AUTH: {
    LOGIN: '/auth/login',
    REGISTER: '/auth/register',
    ME: '/users/me',
  },
  USERS: {
    BASE: '/users',
    PROFILE: '/users/profile',
  },
  PROPERTIES: {
    BASE: '/properties',
    SEARCH: '/properties/search',
    FEATURED: '/properties/featured',
  },
  APPOINTMENTS: {
    BASE: '/appointments',
    MY_APPOINTMENTS: '/appointments/my',
  },
};
