import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

interface UserProfile {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  avatarUrl?: string;
  role: 'USER' | 'ADMIN' | 'AGENT';
  createdAt: string;
  updatedAt: string;
}

const Profile: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    phoneNumber: '',
  });

  useEffect(() => {
    const fetchProfile = async () => {
      if (!user) {
        navigate('/login');
        return;
      }

      try {
        setLoading(true);
        // TODO: Replace with actual API call
        // const response = await api.get('/users/me');
        // setProfile(response.data);

        // Mock data for now
        setTimeout(() => {
          setProfile({
            id: 1,
            email: user.email || 'john.doe@example.com',
            firstName: 'John',
            lastName: 'Doe',
            phoneNumber: '+33 6 12 34 56 78',
            avatarUrl: 'https://source.unsplash.com/random/200x200?portrait',
            role: 'USER',
            createdAt: '2023-01-15T10:30:00Z',
            updatedAt: '2023-05-10T14:20:00Z',
          });

          setFormData({
            firstName: 'John',
            lastName: 'Doe',
            phoneNumber: '+33 6 12 34 56 78',
          });

          setLoading(false);
        }, 500);
      } catch (err) {
        console.error('Error fetching profile:', err);
        setError('Impossible de charger le profil');
        setLoading(false);
      }
    };

    fetchProfile();
  }, [user, navigate]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      // TODO: Replace with actual API call
      // await api.put('/users/me', formData);

      // Update local state
      if (profile) {
        setProfile({
          ...profile,
          ...formData,
          updatedAt: new Date().toISOString(),
        });
      }

      setIsEditing(false);
      // Show success message
      alert('Profil mis à jour avec succès');
    } catch (err) {
      console.error('Error updating profile:', err);
      alert('Erreur lors de la mise à jour du profil');
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-indigo-500"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="bg-red-50 border-l-4 border-red-400 p-4">
          <div className="flex">
            <div className="flex-shrink-0">
              <svg className="h-5 w-5 text-red-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
              </svg>
            </div>
            <div className="ml-3">
              <p className="text-sm text-red-700">{error}</p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (!profile) {
    return null;
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="md:flex md:items-center md:justify-between">
        <div className="flex-1 min-w-0">
          <h2 className="text-2xl font-bold leading-7 text-gray-900 sm:text-3xl sm:truncate">
            Mon Profil
          </h2>
        </div>
        <div className="mt-4 flex md:mt-0 md:ml-4
        ">
          <button
            type="button"
            onClick={isEditing ? () => setIsEditing(false) : () => setIsEditing(true)}
            className="ml-3 inline-flex items-center px-4 py-2 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            {isEditing ? 'Annuler' : 'Modifier le profil'}
          </button>
          <button
            type="button"
            onClick={handleLogout}
            className="ml-3 inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
          >
            Déconnexion
          </button>
        </div>
      </div>

      <div className="mt-8">
        <div className="md:grid md:grid-cols-3 md:gap-6">
          <div className="md:col-span-1">
            <div className="px-4 sm:px-0">
              <h3 className="text-lg font-medium leading-6 text-gray-900">Informations personnelles</h3>
              <p className="mt-1 text-sm text-gray-600">
                Ces informations seront visibles par les autres utilisateurs.
              </p>
            </div>
          </div>
          <div className="mt-5 md:mt-0 md:col-span-2">
            <form onSubmit={handleSubmit}>
              <div className="shadow overflow-hidden sm:rounded-md">
                <div className="px-4 py-5 bg-white sm:p-6">
                  <div className="grid grid-cols-6 gap-6">
                    <div className="col-span-6 sm:col-span-3">
                      <label htmlFor="firstName" className="block text-sm font-medium text-gray-700">
                        Prénom
                      </label>
                      {isEditing ? (
                        <input
                          type="text"
                          name="firstName"
                          id="firstName"
                          value={formData.firstName}
                          onChange={handleInputChange}
                          className="mt-1 focus:ring-indigo-500 focus:border-indigo-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md"
                        />
                      ) : (
                        <p className="mt-1 text-sm text-gray-900">{profile.firstName}</p>
                      )}
                    </div>

                    <div className="col-span-6 sm:col-span-3">
                      <label htmlFor="lastName" className="block text-sm font-medium text-gray-700">
                        Nom
                      </label>
                      {isEditing ? (
                        <input
                          type="text"
                          name="lastName"
                          id="lastName"
                          value={formData.lastName}
                          onChange={handleInputChange}
                          className="mt-1 focus:ring-indigo-500 focus:border-indigo-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md"
                        />
                      ) : (
                        <p className="mt-1 text-sm text-gray-900">{profile.lastName}</p>
                      )}
                    </div>

                    <div className="col-span-6 sm:col-span-4">
                      <label htmlFor="email" className="block text-sm font-medium text-gray-700">
                        Adresse email
                      </label>
                      <p className="mt-1 text-sm text-gray-900">{profile.email}</p>
                      <p className="mt-1 text-xs text-gray-500">
                        L'adresse email ne peut pas être modifiée.
                      </p>
                    </div>

                    <div className="col-span-6 sm:col-span-4">
                      <label htmlFor="phoneNumber" className="block text-sm font-medium text-gray-700">
                        Téléphone
                      </label>
                      {isEditing ? (
                        <input
                          type="text"
                          name="phoneNumber"
                          id="phoneNumber"
                          value={formData.phoneNumber}
                          onChange={handleInputChange}
                          className="mt-1 focus:ring-indigo-500 focus:border-indigo-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md"
                        />
                      ) : (
                        <p className="mt-1 text-sm text-gray-900">{profile.phoneNumber || 'Non renseigné'}</p>
                      )}
                    </div>

                    <div className="col-span-6 sm:col-span-4">
                      <label className="block text-sm font-medium text-gray-700">
                        Photo de profil
                      </label>
                      <div className="mt-2 flex items-center">
                        <span className="inline-block h-12 w-12 rounded-full overflow-hidden bg-gray-100">
                          {profile.avatarUrl ? (
                            <img
                              src={profile.avatarUrl}
                              alt={`${profile.firstName} ${profile.lastName}`}
                              className="h-full w-full text-gray-300"
                            />
                          ) : (
                            <svg
                              className="h-full w-full text-gray-300"
                              fill="currentColor"
                              viewBox="0 0 24 24"
                            >
                              <path d="M24 20.993V24H0v-2.996A14.977 14.977 0 0112.004 15c4.904 0 9.26 2.354 11.996 5.993zM16.002 8.999a4 4 0 11-8 0 4 4 0 018 0z" />
                            </svg>
                          )}
                        </span>
                        {isEditing && (
                          <button
                            type="button"
                            className="ml-5 bg-white py-2 px-3 border border-gray-300 rounded-md shadow-sm text-sm leading-4 font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                          >
                            Changer
                          </button>
                        )}
                      </div>
                    </div>
                  </div>
                </div>

                {isEditing && (
                  <div className="px-4 py-3 bg-gray-50 text-right sm:px-6">
                    <button
                      type="submit"
                      className="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                    >
                      Enregistrer les modifications
                    </button>
                  </div>
                )}
              </div>
            </form>
          </div>
        </div>
      </div>

      <div className="hidden sm:block" aria-hidden="true">
        <div className="py-5">
          <div className="border-t border-gray-200"></div>
        </div>
      </div>

      <div className="mt-10 sm:mt-0">
        <div className="md:grid md:grid-cols-3 md:gap-6">
          <div className="md:col-span-1">
            <div className="px-4 sm:px-0">
              <h3 className="text-lg font-medium leading-6 text-gray-900">Sécurité</h3>
              <p className="mt-1 text-sm text-gray-600">
                Mettez à jour votre mot de passe ou désactivez votre compte.
              </p>
            </div>
          </div>
          <div className="mt-5 md:mt-0 md:col-span-2">
            <div className="shadow overflow-hidden sm:rounded-md">
              <div className="px-4 py-5 bg-white sm:p-6">
                <div className="grid grid-cols-6 gap-6">
                  <div className="col-span-6">
                    <button
                      type="button"
                      onClick={() => navigate('/change-password')}
                      className="inline-flex items-center px-4 py-2 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                    >
                      Changer de mot de passe
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Profile;
