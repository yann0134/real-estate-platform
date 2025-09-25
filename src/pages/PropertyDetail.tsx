import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

interface Property {
  id: number;
  title: string;
  description: string;
  price: number;
  surface: number;
  rooms: number;
  bedrooms: number;
  type: 'APPARTMENT' | 'HOUSE';
  transactionType: 'SALE' | 'RENT';
  city: string;
  mainImageUrl: string;
  images: string[];
  isFavorite: boolean;
  createdAt: string;
}

const PropertyDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [property, setProperty] = useState<Property | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchProperty = async () => {
      try {
        setLoading(true);
        // TODO: Replace with actual API call
        // const response = await api.get(`/properties/${id}`);
        // setProperty(response.data);

        // Mock data for now
        setTimeout(() => {
          setProperty({
            id: Number(id),
            title: 'Belle propriété à vendre',
            description: 'Superbe propriété avec de nombreux avantages...',
            price: 350000,
            surface: 120,
            rooms: 5,
            bedrooms: 3,
            type: 'HOUSE',
            transactionType: 'SALE',
            city: 'Paris',
            mainImageUrl: 'https://source.unsplash.com/random/800x600?house',
            images: [
              'https://source.unsplash.com/random/800x600?living-room',
              'https://source.unsplash.com/random/800x600?kitchen',
              'https://source.unsplash.com/random/800x600?bedroom',
              'https://source.unsplash.com/random/800x600?bathroom',
            ],
            isFavorite: false,
            createdAt: new Date().toISOString(),
          });
          setLoading(false);
        }, 500);
      } catch (err) {
        console.error('Error fetching property:', err);
        setError('Impossible de charger les détails de la propriété');
        setLoading(false);
      }
    };

    fetchProperty();
  }, [id]);

  const toggleFavorite = async () => {
    if (!property) return;

    try {
      // TODO: Replace with actual API call
      // await api.post(`/properties/${property.id}/favorite`);
      setProperty(prev => prev ? { ...prev, isFavorite: !prev.isFavorite } : null);
    } catch (err) {
      console.error('Error toggling favorite:', err);
    }
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
        <button
          onClick={() => navigate('/properties')}
          className="mt-4 px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700"
        >
          Retour aux propriétés
        </button>
      </div>
    );
  }

  if (!property) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 text-center">
        <h2 className="text-2xl font-bold text-gray-900">Propriété non trouvée</h2>
        <button
          onClick={() => navigate('/properties')}
          className="mt-4 px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700"
        >
          Retour aux propriétés
        </button>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <button
        onClick={() => navigate(-1)}
        className="mb-6 flex items-center text-indigo-600 hover:text-indigo-800"
      >
        <svg className="h-5 w-5 mr-1" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
          <path fillRule="evenodd" d="M9.707 16.707a1 1 0 01-1.414 0l-6-6a1 1 0 010-1.414l6-6a1 1 0 011.414 1.414L5.414 9H17a1 1 0 110 2H5.414l4.293 4.293a1 1 0 010 1.414z" clipRule="evenodd" />
        </svg>
        Retour
      </button>

      <div className="bg-white shadow overflow-hidden sm:rounded-lg">
        {/* En-tête avec image principale */}
        <div className="relative h-96 overflow-hidden">
          <img
            className="w-full h-full object-cover"
            src={property.mainImageUrl}
            alt={property.title}
          />
          <button
            onClick={toggleFavorite}
            className={`absolute top-4 right-4 p-2 rounded-full ${
              property.isFavorite ? 'bg-red-500 text-white' : 'bg-white text-gray-700'
            }`}
            aria-label={property.isFavorite ? 'Retirer des favoris' : 'Ajouter aux favoris'}
          >
            <svg
              className="h-6 w-6"
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 20 20"
              fill={property.isFavorite ? 'currentColor' : 'none'}
              stroke="currentColor"
              strokeWidth="2"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"
              />
            </svg>
          </button>
          <div className="absolute bottom-4 left-4 bg-indigo-600 text-white text-sm font-bold px-2 py-1 rounded">
            {property.transactionType === 'SALE' ? 'À vendre' : 'À louer'}
          </div>
        </div>

        {/* Galerie d'images */}
        <div className="px-6 py-4">
          <div className="flex space-x-2 overflow-x-auto pb-2">
            {property.images.map((image, index) => (
              <div key={index} className="flex-shrink-0 w-24 h-24">
                <img
                  src={image}
                  alt={`Vue ${index + 1} de la propriété`}
                  className="w-full h-full object-cover rounded"
                />
              </div>
            ))}
          </div>
        </div>

        {/* Détails de la propriété */}
        <div className="px-6 py-4">
          <div className="flex justify-between items-start">
            <div>
              <h1 className="text-2xl font-bold text-gray-900">{property.title}</h1>
              <p className="mt-1 text-gray-500">{property.city}</p>
            </div>
            <div className="text-right">
              <p className="text-2xl font-bold text-indigo-600">
                {new Intl.NumberFormat('fr-FR', {
                  style: 'currency',
                  currency: 'EUR',
                  maximumFractionDigits: 0
                }).format(property.price)}
                {property.transactionType === 'RENT' && ' / mois'}
              </p>
            </div>
          </div>

          <div className="mt-6 grid grid-cols-2 md:grid-cols-4 gap-4 text-center">
            <div className="p-3 bg-gray-50 rounded-lg">
              <p className="text-sm text-gray-500">Surface</p>
              <p className="font-medium">{property.surface} m²</p>
            </div>
            <div className="p-3 bg-gray-50 rounded-lg">
              <p className="text-sm text-gray-500">Pièces</p>
              <p className="font-medium">{property.rooms}</p>
            </div>
            <div className="p-3 bg-gray-50 rounded-lg">
              <p className="text-sm text-gray-500">Chambres</p>
              <p className="font-medium">{property.bedrooms}</p>
            </div>
            <div className="p-3 bg-gray-50 rounded-lg">
              <p className="text-sm text-gray-500">Type</p>
              <p className="font-medium">
                {property.type === 'APPARTMENT' ? 'Appartement' : 'Maison'}
              </p>
            </div>
          </div>

          <div className="mt-8">
            <h2 className="text-lg font-medium text-gray-900">Description</h2>
            <p className="mt-2 text-gray-600">{property.description}</p>
          </div>

          <div className="mt-8">
            <h2 className="text-lg font-medium text-gray-900">Caractéristiques</h2>
            <div className="mt-4 grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="flex items-center">
                <svg className="h-5 w-5 text-gray-400 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
                </svg>
                <span>Type: {property.type === 'APPARTMENT' ? 'Appartement' : 'Maison'}</span>
              </div>
              <div className="flex items-center">
                <svg className="h-5 w-5 text-gray-400 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                </svg>
                <span>Surface: {property.surface} m²</span>
              </div>
              <div className="flex items-center">
                <svg className="h-5 w-5 text-gray-400 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
                </svg>
                <span>Pièces: {property.rooms}</span>
              </div>
              <div className="flex items-center">
                <svg className="h-5 w-5 text-gray-400 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
                </svg>
                <span>Chambres: {property.bedrooms}</span>
              </div>
            </div>
          </div>
        </div>

        {/* Boutons d'action */}
        <div className="px-6 py-4 bg-gray-50 flex justify-between">
          <button
            onClick={() => navigate(`/appointments/new?propertyId=${property.id}`)}
            className="px-6 py-2 border border-transparent text-base font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            Prendre rendez-vous
          </button>
          <button
            className="px-6 py-2 border border-gray-300 text-base font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            Contacter l'agence
          </button>
        </div>
      </div>
    </div>
  );
};

export default PropertyDetail;
