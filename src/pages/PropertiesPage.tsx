import React, { useState, useEffect } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import PropertyFilters from '../components/PropertyFilters';

interface Property {
  id: number;
  title: string;
  description: string;
  price: number;
  surface: number;
  rooms: number;
  bedrooms: number;
  type: string;
  transactionType: 'SALE' | 'RENT';
  city: string;
  mainImageUrl: string;
  isFavorite: boolean;
  createdAt: string;
}

const PropertiesPage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const [properties, setProperties] = useState<Property[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [filters, setFilters] = useState({
    type: searchParams.get('type') || '',
    transactionType: searchParams.get('transactionType') || 'SALE',
    minPrice: searchParams.get('minPrice') || '',
    maxPrice: searchParams.get('maxPrice') || '',
    minSurface: searchParams.get('minSurface') || '',
    maxSurface: searchParams.get('maxSurface') || '',
    rooms: searchParams.get('rooms') || '',
    bedrooms: searchParams.get('bedrooms') || '',
    sortBy: searchParams.get('sortBy') || 'createdAt',
    sortOrder: searchParams.get('sortOrder') || 'desc',
    searchQuery: searchParams.get('searchQuery') || '',
  });
  const [viewMode, setViewMode] = useState<'grid' | 'list'>('grid');
  const [favoritesOnly, setFavoritesOnly] = useState(false);
  const itemsPerPage = 12;

  // Charger les propriétés avec les filtres
  useEffect(() => {
    const fetchProperties = async () => {
      try {
        setLoading(true);
        setError('');
        
        // Construire les paramètres de requête
        const params = new URLSearchParams();
        Object.entries(filters).forEach(([key, value]) => {
          if (value) {
            params.set(key, value.toString());
          }
        });
        
        // Ajouter la pagination
        params.set('page', currentPage.toString());
        params.set('limit', itemsPerPage.toString());
        
        // Simuler un appel API (remplacer par un vrai appel)
        // const response = await api.get(`/properties?${params.toString()}`);
        // setProperties(response.data.items);
        // setTotalPages(response.data.totalPages);
        
        // Données factices pour la démo
        setTimeout(() => {
          const mockProperties: Property[] = Array.from({ length: itemsPerPage }, (_, i) => ({
            id: i + 1,
            title: i % 2 === 0 
              ? 'Appartement lumineux avec vue sur la Tour Eiffel' 
              : 'Maison de caractère avec jardin',
            description: 'Bien situé, calme et proche de tous les commerces. Idéal pour une famille ou investissement locatif.',
            price: i % 2 === 0 ? 1250000 : 450000,
            surface: i % 2 === 0 ? 85 : 120,
            rooms: i % 2 === 0 ? 4 : 5,
            bedrooms: i % 2 === 0 ? 2 : 3,
            type: i % 2 === 0 ? 'APPARTMENT' : 'HOUSE',
            transactionType: i % 3 === 0 ? 'RENT' : 'SALE',
            city: i % 2 === 0 ? 'Paris 7e' : 'Versailles',
            mainImageUrl: `https://source.unsplash.com/random/800x600?real-estate,${i}`,
            isFavorite: Math.random() > 0.7,
            createdAt: new Date(Date.now() - Math.floor(Math.random() * 30) * 24 * 60 * 60 * 1000).toISOString(),
          }));
          
          setProperties(mockProperties);
          setTotalPages(5); // Nombre de pages factice
          setLoading(false);
        }, 500);
        
      } catch (err) {
        console.error('Erreur lors du chargement des propriétés:', err);
        setError('Impossible de charger les propriétés. Veuillez réessayer plus tard.');
        setLoading(false);
      }
    };

    fetchProperties();
  }, [filters, currentPage]);

  // Gérer le changement de page
  const handlePageChange = (page: number) => {
    setCurrentPage(page);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  // Basculer entre les vues grille et liste
  const toggleViewMode = () => {
    setViewMode(prev => prev === 'grid' ? 'list' : 'grid');
  };

  // Basculer l'état de favori d'une propriété
  const toggleFavorite = async (propertyId: number) => {
    try {
      // Simuler une requête API pour mettre à jour les favoris
      // await api.post(`/properties/${propertyId}/favorite`);
      
      // Mettre à jour l'état local
      setProperties(prevProperties =>
        prevProperties.map(property =>
          property.id === propertyId
            ? { ...property, isFavorite: !property.isFavorite }
            : property
        )
      );
    } catch (err) {
      console.error('Erreur lors de la mise à jour des favoris:', err);
    }
  };

  // Filtrer les propriétés favorites si nécessaire
  const filteredProperties = favoritesOnly
    ? properties.filter(property => property.isFavorite)
    : properties;

  // Formater le prix
  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('fr-FR', { 
      style: 'currency', 
      currency: 'EUR',
      maximumFractionDigits: 0 
    }).format(price);
  };

  // Formater la date
  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', { 
      day: '2-digit', 
      month: '2-digit', 
      year: 'numeric' 
    });
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* En-tête avec image de fond */}
      <div className="relative bg-indigo-700">
        <div className="absolute inset-0">
          <img
            className="w-full h-full object-cover"
            src="https://source.unsplash.com/random/1920x400?real-estate"
            alt="Bien immobilier"
          />
          <div className="absolute inset-0 bg-indigo-700 mix-blend-multiply" aria-hidden="true" />
        </div>
        <div className="relative max-w-7xl mx-auto py-24 px-4 sm:py-32 sm:px-6 lg:px-8">
          <h1 className="text-4xl font-extrabold tracking-tight text-white sm:text-5xl lg:text-6xl">
            Nos biens immobiliers
          </h1>
          <p className="mt-6 text-xl text-indigo-100 max-w-3xl">
            Découvrez notre sélection de biens immobiliers soigneusement sélectionnés pour répondre à tous vos besoins.
          </p>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Filtres */}
        <PropertyFilters 
          onFilterChange={setFilters} 
          loading={loading} 
          className="mb-8"
        />

        {/* Barre d'outils */}
        <div className="flex flex-col sm:flex-row justify-between items-center mb-6">
          <div className="flex items-center mb-4 sm:mb-0">
            <span className="text-sm text-gray-700">
              {filteredProperties.length} {filteredProperties.length > 1 ? 'biens trouvés' : 'bien trouvé'}
              {filters.searchQuery && ` pour "${filters.searchQuery}"`}
            </span>
            
            <div className="ml-4 flex items-center">
              <span className="text-sm text-gray-700 mr-2">Vue :</span>
              <button
                type="button"
                onClick={toggleViewMode}
                className="p-2 rounded-md text-gray-400 hover:text-indigo-600 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                title={viewMode === 'grid' ? 'Passer en vue liste' : 'Passer en vue grille'}
              >
                {viewMode === 'grid' ? (
                  <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
                  </svg>
                ) : (
                  <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
                  </svg>
                )}
              </button>
            </div>
          </div>
          
          <div className="flex items-center">
            <div className="flex items-center">
              <input
                id="favorites-only"
                name="favorites-only"
                type="checkbox"
                checked={favoritesOnly}
                onChange={() => setFavoritesOnly(!favoritesOnly)}
                className="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
              />
              <label htmlFor="favorites-only" className="ml-2 block text-sm text-gray-700">
                Afficher uniquement les favoris
              </label>
            </div>
            
            <div className="ml-4">
              <select
                id="sort-by"
                name="sort-by"
                value={`${filters.sortBy}_${filters.sortOrder}`}
                onChange={(e) => {
                  const [sortBy, sortOrder] = e.target.value.split('_');
                  setFilters(prev => ({
                    ...prev,
                    sortBy,
                    sortOrder
                  }));
                }}
                className="block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"
              >
                <option value="createdAt_desc">Plus récent</option>
                <option value="createdAt_asc">Plus ancien</option>
                <option value="price_asc">Prix croissant</option>
                <option value="price_desc">Prix décroissant</option>
                <option value="surface_desc">Surface décroissante</option>
                <option value="surface_asc">Surface croissante</option>
              </select>
            </div>
          </div>
        </div>

        {/* Message de chargement */}
        {loading && (
          <div className="flex justify-center items-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-indigo-500"></div>
            <span className="ml-3 text-gray-700">Chargement des biens...</span>
          </div>
        )}

        {/* Message d'erreur */}
        {error && (
          <div className="bg-red-50 border-l-4 border-red-400 p-4 mb-6">
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
        )}

        {/* Liste vide */}
        {!loading && !error && filteredProperties.length === 0 && (
          <div className="text-center py-12">
            <svg
              className="mx-auto h-12 w-12 text-gray-400"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
              aria-hidden="true"
            >
              <path
                vectorEffect="non-scaling-stroke"
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
              />
            </svg>
            <h3 className="mt-2 text-sm font-medium text-gray-900">Aucun bien trouvé</h3>
            <p className="mt-1 text-sm text-gray-500">
              Aucun bien ne correspond à vos critères de recherche. Essayez de modifier vos filtres.
            </p>
            <div className="mt-6">
              <button
                type="button"
                onClick={() => {
                  setFilters({
                    type: '',
                    transactionType: 'SALE',
                    minPrice: '',
                    maxPrice: '',
                    minSurface: '',
                    maxSurface: '',
                    rooms: '',
                    bedrooms: '',
                    sortBy: 'createdAt',
                    sortOrder: 'desc',
                    searchQuery: '',
                  });
                  setFavoritesOnly(false);
                }}
                className="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
              >
                <svg className="-ml-1 mr-2 h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                  <path fillRule="evenodd" d="M4 2a1 1 0 011 1v2.101a7.002 7.002 0 0111.601 2.566 1 1 0 11-1.885.666A5.002 5.002 0 005.999 7H9a1 1 0 010 2H4a1 1 0 01-1-1V3a1 1 0 011-1zm.008 9.057a1 1 0 011.276.61A5.002 5.002 0 0014.001 13H11a1 1 0 110-2h5a1 1 0 011 1v5a1 1 0 11-2 0v-2.101a7.002 7.002 0 01-11.601-2.566 1 1 0 01.61-1.276z" clipRule="evenodd" />
                </svg>
                Réinitialiser les filtres
              </button>
            </div>
          </div>
        )}

        {/* Grille ou liste des propriétés */}
        {!loading && !error && filteredProperties.length > 0 && (
          <>
            {viewMode === 'grid' ? (
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                {filteredProperties.map((property) => (
                  <div key={property.id} className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow duration-200">
                    <div className="relative">
                      <Link to={`/properties/${property.id}`} className="block">
                        <img
                          className="w-full h-48 object-cover"
                          src={property.mainImageUrl}
                          alt={property.title}
                        />
                        <div className="absolute bottom-2 left-2 bg-indigo-600 text-white text-xs font-bold px-2 py-1 rounded">
                          {property.transactionType === 'SALE' ? 'À vendre' : 'À louer'}
                        </div>
                        <div className="absolute top-2 right-2">
                          <button
                            type="button"
                            onClick={(e) => {
                              e.preventDefault();
                              e.stopPropagation();
                              toggleFavorite(property.id);
                            }}
                            className={`p-2 rounded-full ${property.isFavorite ? 'bg-red-500 text-white' : 'bg-white text-gray-700'}`}
                            aria-label={property.isFavorite ? 'Retirer des favoris' : 'Ajouter aux favoris'}
                          >
                            <svg
                              className="h-5 w-5"
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
                        </div>
                      </Link>
                    </div>
                    <div className="p-4">
                      <div className="flex justify-between items-start">
                        <div>
                          <h3 className="text-lg font-semibold text-gray-900 truncate">
                            <Link to={`/properties/${property.id}`} className="hover:text-indigo-600">
                              {property.title}
                            </Link>
                          </h3>
                          <p className="text-sm text-gray-500 mt-1">{property.city}</p>
                        </div>
                        <div className="text-right">
                          <p className="text-lg font-bold text-indigo-600">
                            {formatPrice(property.price)}
                            {property.transactionType === 'RENT' && ' / mois'}
                          </p>
                        </div>
                      </div>
                      <div className="mt-4 flex items-center text-sm text-gray-500">
                        <span className="flex items-center">
                          <svg className="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
                          </svg>
                          {property.type === 'APPARTMENT' ? 'Appartement' : 'Maison'}
                        </span>
                        <span className="mx-2">•</span>
                        <span>{property.surface} m²</span>
                        <span className="mx-2">•</span>
                        <span>{property.rooms} pièces</span>
                      </div>
                      <div className="mt-3 flex items-center justify-between">
                        <span className="text-xs text-gray-500">
                          Ajouté le {formatDate(property.createdAt)}
                        </span>
                        <Link
                          to={`/properties/${property.id}`}
                          className="text-sm font-medium text-indigo-600 hover:text-indigo-500"
                        >
                          Voir plus <span aria-hidden="true">→</span>
                        </Link>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="bg-white shadow overflow-hidden sm:rounded-md">
                <ul className="divide-y divide-gray-200">
                  {filteredProperties.map((property) => (
                    <li key={property.id} className="hover:bg-gray-50">
                      <Link to={`/properties/${property.id}`} className="block">
                        <div className="px-4 py-4 sm:px-6">
                          <div className="flex items-center justify-between">
                            <div className="flex items-center">
                              <div className="flex-shrink-0 h-20 w-20 rounded-md overflow-hidden">
                                <img
                                  className="h-full w-full object-cover"
                                  src={property.mainImageUrl}
                                  alt={property.title}
                                />
                              </div>
                              <div className="ml-4">
                                <div className="text-sm font-medium text-indigo-600 truncate">
                                  {property.title}
                                </div>
                                <div className="text-sm text-gray-500">
                                  {property.city} • {property.type === 'APPARTMENT' ? 'Appartement' : 'Maison'} • {property.surface} m² • {property.rooms} pièces • {property.bedrooms} chambres
                                </div>
                              </div>
                            </div>
                            <div className="text-right">
                              <p className="text-lg font-bold text-indigo-600">
                                {formatPrice(property.price)}
                                {property.transactionType === 'RENT' && ' / mois'}
                              </p>
                              <div className="mt-1">
                                <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                                  property.transactionType === 'SALE' ? 'bg-green-100 text-green-800' : 'bg-blue-100 text-blue-800'
                                }`}>
                                  {property.transactionType === 'SALE' ? 'À vendre' : 'À louer'}
                                </span>
                              </div>
                            </div>
                          </div>
                        </div>
                      </Link>
                    </li>
                  ))}
                </ul>
              </div>
            )}

            {/* Pagination */}
            {totalPages > 1 && (
              <div className="mt-8 flex justify-center">
                <nav className="relative z-0 inline-flex rounded-md shadow-sm -space-x-px" aria-label="Pagination">
                  <button
                    onClick={() => handlePageChange(currentPage - 1)}
                    disabled={currentPage === 1}
                    className={`relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium ${
                      currentPage === 1 ? 'text-gray-300 cursor-not-allowed' : 'text-gray-500 hover:bg-gray-50'
                    }`}
                  >
                    <span className="sr-only">Précédent</span>
                    <svg className="h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                      <path fillRule="evenodd" d="M12.707 5.293a1 1 0 010 1.414L9.414 10l3.293 3.293a1 1 0 01-1.414 1.414l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 0z" clipRule="evenodd" />
                    </svg>
                  </button>
                  
                  {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
                    // Afficher les numéros de page de manière intelligente
                    let pageNum;
                    if (totalPages <= 5) {
                      pageNum = i + 1;
                    } else if (currentPage <= 3) {
                      pageNum = i + 1;
                    } else if (currentPage >= totalPages - 2) {
                      pageNum = totalPages - 4 + i;
                    } else {
                      pageNum = currentPage - 2 + i;
                    }
                    
                    return (
                      <button
                        key={pageNum}
                        onClick={() => handlePageChange(pageNum)}
                        className={`relative inline-flex items-center px-4 py-2 border text-sm font-medium ${
                          currentPage === pageNum
                            ? 'z-10 bg-indigo-50 border-indigo-500 text-indigo-600'
                            : 'bg-white border-gray-300 text-gray-500 hover:bg-gray-50'
                        }`}
                      >
                        {pageNum}
                      </button>
                    );
                  })}
                  
                  <button
                    onClick={() => handlePageChange(currentPage + 1)}
                    disabled={currentPage === totalPages}
                    className={`relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium ${
                      currentPage === totalPages ? 'text-gray-300 cursor-not-allowed' : 'text-gray-500 hover:bg-gray-50'
                    }`}
                  >
                    <span className="sr-only">Suivant</span>
                    <svg className="h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                      <path fillRule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clipRule="evenodd" />
                    </svg>
                  </button>
                </nav>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
};

export default PropertiesPage;
