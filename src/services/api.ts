import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import { API_BASE_URL } from '../config';
import Cookies from 'js-cookie';

class ApiService {
  private api: AxiosInstance;

  constructor() {
    this.api = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        'Content-Type': 'application/json',
      },
      withCredentials: true, // Active l'envoi des cookies
    });

    // Intercepteur pour ajouter le token CSRF aux requêtes
    this.api.interceptors.request.use(
      (config) => {
        const token = Cookies.get('XSRF-TOKEN');
        if (token) {
          config.headers['X-XSRF-TOKEN'] = token;
        }
        
        // Ajout du token JWT si disponible
        const authToken = localStorage.getItem('token');
        if (authToken) {
          config.headers.Authorization = `Bearer ${authToken}`;
        }
        
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    // Gestion des erreurs globales
    this.api.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response?.status === 401) {
          // Déconnexion si non autorisé
          localStorage.removeItem('token');
          window.location.href = '/login';
        }
        return Promise.reject(error);
      }
    );
  }

  // Méthodes génériques
  async get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const response: AxiosResponse<T> = await this.api.get(url, config);
    return response.data;
  }

  async post<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response: AxiosResponse<T> = await this.api.post(url, data, config);
    return response.data;
  }

  async put<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response: AxiosResponse<T> = await this.api.put(url, data, config);
    return response.data;
  }

  async delete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const response: AxiosResponse<T> = await this.api.delete(url, config);
    return response.data;
  }

  // Méthodes spécifiques pour l'authentification
  async login(email: string, password: string) {
    return this.post<{ token: string; user: any }>('/auth/login', { email, password });
  }

  async register(userData: {
    email: string;
    password: string;
    firstName: string;
    lastName: string;
  }) {
    return this.post('/auth/register', userData);
  }

  async getCurrentUser() {
    return this.get('/users/me');
  }

  // Méthodes pour les propriétés
  async getProperties(params?: any) {
    return this.get('/properties', { params });
  }

  async getPropertyById(id: string | number) {
    return this.get(`/properties/${id}`);
  }

  async createProperty(propertyData: any) {
    return this.post('/properties', propertyData);
  }

  // Méthodes pour les rendez-vous
  async getAppointments() {
    return this.get('/appointments');
  }

  async createAppointment(appointmentData: any) {
    return this.post('/appointments', appointmentData);
  }
}

export const api = new ApiService();
