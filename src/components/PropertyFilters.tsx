import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { MagnifyingGlassIcon } from "@heroicons/react/24/outline";
import {ChevronDownIcon, ChevronUpIcon} from "lucide-react";

// ... (imports existants)

interface PropertyFiltersProps {
  onFilterChange: (filters: any) => void;
  loading: boolean;
  className?: string;
}

const PropertyFilters: React.FC<PropertyFiltersProps> = ({ onFilterChange, loading, className = '' }) => {
  const [searchParams, setSearchParams] = useSearchParams();
  const [showAdvancedFilters, setShowAdvancedFilters] = useState(false);
  const [activeFilters, setActiveFilters] = useState<string[]>([]);

  // Initialiser les filtres depuis les paramètres d'URL
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

  // Mettre à jour les filtres actifs
  useEffect(() => {
    const newActiveFilters: string[] = [];
    if (filters.searchQuery) newActiveFilters.push(`Recherche: ${filters.searchQuery}`);
    if (filters.transactionType) {
      newActiveFilters.push(`Type: ${filters.transactionType === 'SALE' ? 'Vente' : 'Location'}`);
    }
    if (filters.type) newActiveFilters.push(`Bien: ${filters.type === 'APPARTMENT' ? 'Appartement' : 'Maison'}`);
    if (filters.minPrice) newActiveFilters.push(`Prix min: ${filters.minPrice}€`);
    if (filters.maxPrice) newActiveFilters.push(`Prix max: ${filters.maxPrice}€`);
    if (filters.minSurface) newActiveFilters.push(`Surface min: ${filters.minSurface}m²`);
    if (filters.maxSurface) newActiveFilters.push(`Surface max: ${filters.maxSurface}m²`);
    if (filters.rooms) newActiveFilters.push(`Pièces: ${filters.rooms}+`);
    if (filters.bedrooms) newActiveFilters.push(`Chambres: ${filters.bedrooms}+`);

    setActiveFilters(newActiveFilters);
  }, [filters]);

  // Mettre à jour les paramètres d'URL lorsque les filtres changent
  useEffect(() => {
    const params = new URLSearchParams();
    Object.entries(filters).forEach(([key, value]) => {
      if (value) {
        params.set(key, value.toString());
      }
    });
    setSearchParams(params, { replace: true });

    // Appeler la fonction de rappel avec les filtres actuels
    onFilterChange(filters);
  }, [filters, onFilterChange, setSearchParams]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFilters(prev => ({ ...prev, [name]: value }));
  };

  const handleResetFilters = () => {
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
  };

  const removeFilter = (filterToRemove: string) => {
    const [key] = filterToRemove.split(': ');
    const filterMap: Record<string, string> = {
      'Recherche': 'searchQuery',
      'Type': 'transactionType',
      'Bien': 'type',
      'Prix min': 'minPrice',
      'Prix max': 'maxPrice',
      'Surface min': 'minSurface',
      'Surface max': 'maxSurface',
      'Pièces': 'rooms',
      'Chambres': 'bedrooms',
    };

    const filterKey = filterMap[key];
    if (filterKey) {
      setFilters(prev => ({ ...prev, [filterKey]: '' }));
    }
  };

  return (
      <div className={`bg-white shadow rounded-lg p-4 ${className}`}>
        {/* Barre de recherche */}
        <div className="mb-4">
          <label htmlFor="search" className="sr-only">Rechercher</label>
          <div className="relative rounded-md shadow-sm">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <MagnifyingGlassIcon className="h-5 w-5 text-gray-400" aria-hidden="true" />
            </div>
            <input
                type="text"
                name="searchQuery"
                id="search"
                value={filters.searchQuery}
                onChange={handleInputChange}
                className="focus:ring-indigo-500 focus:border-indigo-500 block w-full pl-10 sm:text-sm border-gray-300 rounded-md p-2 border"
                placeholder="Rechercher un bien, une ville, un quartier..."
            />
          </div>
        </div>

        {/* Filtres de base */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-4">
          <div>
            <label htmlFor="transactionType" className="block text-sm font-medium text-gray-700 mb-1">Transaction</label>
            <select
                id="transactionType"
                name="transactionType"
                value={filters.transactionType}
                onChange={handleInputChange}
                className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"
            >
              <option value="SALE">À vendre</option>
              <option value="RENT">À louer</option>
            </select>
          </div>

          <div>
            <label htmlFor="type" className="block text-sm font-medium text-gray-700 mb-1">Type de bien</label>
            <select
                id="type"
                name="type"
                value={filters.type}
                onChange={handleInputChange}
                className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"
            >
              <option value="">Tous les types</option>
              <option value="APPARTMENT">Appartement</option>
              <option value="HOUSE">Maison</option>
            </select>
          </div>

          <div>
            <label htmlFor="minPrice" className="block text-sm font-medium text-gray-700 mb-1">Prix max</label>
            <div className="mt-1 relative rounded-md shadow-sm">
              <input
                  type="number"
                  name="maxPrice"
                  id="maxPrice"
                  value={filters.maxPrice}
                  onChange={handleInputChange}
                  placeholder="Prix maximum"
                  className="focus:ring-indigo-500 focus:border-indigo-500 block w-full pl-3 pr-12 sm:text-sm border-gray-300 rounded-md p-2 border"
              />
              <div className="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none">
                <span className="text-gray-500 sm:text-sm">€</span>
              </div>
            </div>
          </div>
        </div>

        {/* Bouton pour afficher/masquer les filtres avancés */}
        <div className="flex justify-between items-center mb-4">
          <button
              type="button"
              onClick={() => setShowAdvancedFilters(!showAdvancedFilters)}
              className="text-sm text-indigo-600 hover:text-indigo-500 flex items-center"
          >
            {showAdvancedFilters ? (
                <>
                  <ChevronUpIcon className="h-4 w-4 mr-1" />
                  Masquer les filtres avancés
                </>
            ) : (
                <>
                  <ChevronDownIcon className="h-4 w-4 mr-1" />
                  Afficher les filtres avancés
                </>
            )}
          </button>
          <button
              type="button"
              onClick={handleResetFilters}
              className="text-sm text-gray-600 hover:text-gray-800"
              disabled={loading}
          >
            Réinitialiser les filtres
          </button>
        </div>

        {/* Filtres avancés */}
        {showAdvancedFilters && (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-4 pt-4 border-t border-gray-200">
              <div>
                <label htmlFor="minPrice" className="block text-sm font-medium text-gray-700 mb-1">Prix min</label>
                <div className="mt-1 relative rounded-md shadow-sm">
                  <input
                      type="number"
                      name="minPrice"
                      id="minPrice"
                      value={filters.minPrice}
                      onChange={handleInputChange}
                      placeholder="Prix minimum"
                      className="focus:ring-indigo-500 focus:border-indigo-500 block w-full pl-3 pr-12 sm:text-sm border-gray-300 rounded-md p-2 border"
                  />
                  <div className="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none">
                    <span className="text-gray-500 sm:text-sm">€</span>
                  </div>
                </div>
              </div>

              <div>
                <label htmlFor="minSurface" className="block text-sm font-medium text-gray-700 mb-1">Surface min</label>
                <div className="mt-1 relative rounded-md shadow-sm">
                  <input
                      type="number"
                      name="minSurface"
                      id="minSurface"
                      value={filters.minSurface}
                      onChange={handleInputChange}
                      placeholder="Surface minimum"
                      className="focus:ring-indigo-500 focus:border-indigo-500 block w-full pl-3 pr-12 sm:text-sm border-gray-300 rounded-md p-2 border"
                  />
                  <div className="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none">
                    <span className="text-gray-500 sm:text-sm">m²</span>
                  </div>
                </div>
              </div>

              <div>
                <label htmlFor="maxSurface" className="block text-sm font-medium text-gray-700 mb-1">Surface max</label>
                <div className="mt-1 relative rounded-md shadow-sm">
                  <input
                      type="number"
                      name="maxSurface"
                      id="maxSurface"
                      value={filters.maxSurface}
                      onChange={handleInputChange}
                      placeholder="Surface maximum"
                      className="focus:ring-indigo-500 focus:border-indigo-500 block w-full pl-3 pr-12 sm:text-sm border-gray-300 rounded-md p-2 border"
                  />
                  <div className="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none">
                    <span className="text-gray-500 sm:text-sm">m²</span>
                  </div>
                </div>
              </div>

              <div>
                <label htmlFor="rooms" className="block text-sm font-medium text-gray-700 mb-1">Pièces</label>
                <select
                    id="rooms"
                    name="rooms"
                    value={filters.rooms}
                    onChange={handleInputChange}
                    className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"
                >
                  <option value="">Toutes</option>
                  <option value="1">1+ pièce</option>
                  <option value="2">2+ pièces</option>
                  <option value="3">3+ pièces</option>
                  <option value="4">4+ pièces</option>
                  <option value="5">5+ pièces</option>
                </select>
              </div>

              <div>
                <label htmlFor="bedrooms" className="block text-sm font-medium text-gray-700 mb-1">Chambres</label>
                <select
                    id="bedrooms"
                    name="bedrooms"
                    value={filters.bedrooms}
                    onChange={handleInputChange}
                    className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"
                >
                  <option value="">Toutes</option>
                  <option value="1">1+ chambre</option>
                  <option value="2">2+ chambres</option>
                  <option value="3">3+ chambres</option>
                  <option value="4">4+ chambres</option>
                </select>
              </div>
            </div>
        )}

        {/* Filtres actifs */}
        {activeFilters.length > 0 && (
            <div className="flex flex-wrap gap-2 mt-4 pt-4 border-t border-gray-200">
              {activeFilters.map((filter, index) => (
                  <span
                      key={index}
                      className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-indigo-100 text-indigo-800"
                  >
              {filter}
                    <button
                        type="button"
                        onClick={() => removeFilter(filter)}
                        className="ml-1.5 inline-flex items-center justify-center h-4 w-4 rounded-full text-indigo-400 hover:bg-indigo-200 hover:text-indigo-500 focus:outline-none focus:bg-indigo-500 focus:text-white"
                    >
                <span className="sr-only">Supprimer le filtre</span>
                <svg className="h-2 w-2" stroke="currentColor" fill="none" viewBox="0 0 8 8">
                  <path strokeLinecap="round" strokeWidth="1.5" d="M1 1l6 6m0-6L1 7" />
                </svg>
              </button>
            </span>
              ))}
            </div>
        )}
      </div>
  );
};

export default PropertyFilters;