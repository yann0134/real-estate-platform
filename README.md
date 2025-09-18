# AI Real Estate Backend

Une API REST Spring Boot avec assistant IA intégré pour une plateforme immobilière complète.

## 🚀 Fonctionnalités

- **Authentication JWT** : Inscription/connexion sécurisée avec rôles (ADMIN, USER)
- **Gestion des biens immobiliers** : CRUD complet avec filtres avancés
- **Gestion des images** : Téléchargement et stockage de fichiers
- **Recherche avancée** : Filtrage par prix, surface, nombre de pièces, etc.
- **Favoris** : Sauvegarde des biens préférés
- **Gestion des utilisateurs** : Profils et tableaux de bord personnalisés
- **Assistant IA** : Chatbot intelligent pour la recherche immobilière
- **Sécurité renforcée** : Validation des données et gestion des accès
- **API REST documentée** : Swagger/OpenAPI intégré
- **Base PostgreSQL** : Modèle relationnel optimisé

## 🏗️ Architecture

### Stack Technique
- **Java 17+**
- **Spring Boot 3**
- **Spring Data JPA + Hibernate**
- **PostgreSQL**
- **Spring Security + JWT**
- **OpenAI API** (GPT-3.5-turbo)
- **Swagger/OpenAPI**

### Sécurité IA
- ✅ L'IA n'a jamais accès direct à la base de données
- ✅ Toutes les requêtes passent par des services contrôlés
- ✅ Utilisation exclusive de JPA/Criteria API
- ✅ Journalisation complète des requêtes IA
- ✅ Validation et sanitisation des paramètres

## 🗄️ Modèle de données

### Utilisateurs
```java
@Entity
public class User {
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private UserRole role;  // ADMIN, USER
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "owner")
    private List<Property> properties;
    
    @OneToMany(mappedBy = "user")
    private List<Favorite> favorites;
}
```

### Biens Immobiliers
```java
@Entity
public class Property {
    private Long id;
    private String title;
    private String description;
    private PropertyType type;  // APARTMENT, HOUSE, OFFICE, etc.
    private BigDecimal price;
    private Double surface;
    private Integer rooms;
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer floor;
    private Integer totalFloors;
    private Integer constructionYear;
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private Double latitude;
    private Double longitude;
    private Boolean isFurnished;
    private Boolean hasElevator;
    private Boolean hasParking;
    private EnergyEfficiency energyEfficiency;  // A++, A+, A, B, C, etc.
    private Integer co2Emission;
    private PropertyStatus status;  // AVAILABLE, PENDING, SOLD, RENTED
    
    @ElementCollection
    private List<String> imageUrls;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;
}
```

### Favoris
```java
@Entity
public class Favorite {
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Property property;
}

## 📚 Documentation de l'API

### Authentification

- `POST /api/auth/register` - Inscription d'un nouvel utilisateur
- `POST /api/auth/login` - Connexion et récupération du token JWT

### Utilisateurs

- `GET /api/users/me` - Récupérer les informations de l'utilisateur connecté
- `PUT /api/users/me` - Mettre à jour le profil utilisateur
- `GET /api/users/me/properties` - Récupérer les biens de l'utilisateur
- `GET /api/users/me/favorites` - Récupérer les favoris de l'utilisateur

### Biens Immobiliers

- `GET /api/properties` - Rechercher des biens (avec filtres)
- `POST /api/properties` - Créer un nouveau bien (requiert authentification)
- `GET /api/properties/{id}` - Récupérer un bien par son ID
- `PUT /api/properties/{id}` - Mettre à jour un bien (propriétaire ou admin uniquement)
- `DELETE /api/properties/{id}` - Supprimer un bien (propriétaire ou admin uniquement)
- `POST /api/properties/{id}/images` - Ajouter des images à un bien
- `POST /api/properties/{id}/favorite` - Ajouter un bien aux favoris
- `DELETE /api/properties/{id}/favorite` - Retirer un bien des favoris

### Fichiers

- `POST /api/files/upload` - Téléverser un fichier (image)
- `GET /api/files/{filename}` - Télécharger un fichier

## 🔧 Installation

1. Cloner le dépôt
2. Configurer la base de données PostgreSQL
3. Mettre à jour les paramètres de connexion dans `application.properties`
4. Démarrer l'application :
   ```bash
   ./mvnw spring-boot:run
   ```
5. Accéder à la documentation Swagger : http://localhost:8080/api/swagger-ui.html

## 📝 Licence

Ce projet est sous licence MIT. Voir le fichier [LICENSE](LICENSE) pour plus de détails.

## 🙏 Remerciements

- Spring Boot
- Spring Security
- Hibernate
- PostgreSQL
- Swagger/OpenAPI
-- Favoris utilisateurs
favorites
├── id, user_id (FK), listing_id (FK)
└── created_at

-- Messages entre utilisateurs
messages
├── id, content, is_read
├── sender_id (FK), receiver_id (FK), listing_id (FK)
└── created_at

-- Requêtes IA (journalisation)
ai_queries
├── id, question, raw_results (JSONB)
├── ai_answer, response_time_ms
├── user_id (FK)
└── created_at
```

## 🤖 Assistant IA

### Comment ça marche ?

1. **Réception de la question** : L'utilisateur pose une question en français
   ```json
   {
     "question": "Montre-moi les appartements à Yaoundé avec 3 chambres pour moins de 250000"
   }
   ```

2. **Analyse sécurisée** : Le service `AIQueryService` :
   - Extrait les paramètres (ville, prix, type, chambres)
   - Exécute une requête JPA contrôlée sur PostgreSQL
   - Récupère les résultats sans exposer la BD à l'IA

3. **Génération de la réponse** : 
   - Appel à l'API OpenAI avec les résultats
   - Génération d'une réponse naturelle en français
   - Sauvegarde de la conversation

4. **Réponse structurée** :
   ```json
   {
     "answer": "J'ai trouvé 3 appartements à Yaoundé qui correspondent à vos critères...",
     "listings": [...],
     "totalResults": 3,
     "responseTimeMs": 1250
   }
   ```

### Endpoints IA

- `POST /api/ai/query` - Poser une question à l'IA
- `GET /api/ai/history` - Historique des conversations
- `GET /api/ai/admin/queries` - Dashboard admin (toutes les requêtes)

## 🔧 Configuration

### Variables d'environnement

```bash
# Base de données
DB_USERNAME=real_estate_user
DB_PASSWORD=your_password

# JWT
JWT_SECRET=your-jwt-secret-key-here

# OpenAI
OPENAI_API_KEY=your-openai-api-key-here
```

### Base PostgreSQL

```sql
-- Créer la base de données
CREATE DATABASE real_estate_db;
CREATE USER real_estate_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE real_estate_db TO real_estate_user;
```

## 🚀 Démarrage

1. **Cloner le projet**
```bash
git clone <repository-url>
cd ai-real-estate-backend
```

2. **Configuration PostgreSQL**
```bash
# Installer PostgreSQL
# Créer la base de données (voir section Configuration)
```

3. **Variables d'environnement**
```bash
cp .env.example .env
# Éditer .env avec vos clés
```

4. **Démarrage**
```bash
mvn spring-boot:run
```

5. **Documentation API**
- Swagger UI : http://localhost:8080/api/swagger-ui.html
- OpenAPI JSON : http://localhost:8080/api/v3/api-docs

## 🧪 Tests

```bash
# Tests unitaires
mvn test

# Tests d'intégration
mvn verify
```

## 📝 Exemples d'utilisation

### Authentification

```bash
# Inscription
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe",
    "role": "USER"
  }'
```

### Assistant IA

```bash
# Question à l'IA
curl -X POST http://localhost:8080/api/ai/query \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "question": "Je cherche un studio meublé à Douala pour moins de 150000 FCFA"
  }'
```

## 🔒 Sécurité

- **JWT Authentication** : Tous les endpoints protégés
- **Chiffrement bcrypt** : Mots de passe sécurisés
- **CORS configuré** : Accès frontend contrôlé
- **Validation complète** : Jakarta Validation
- **Logs de sécurité** : Traçabilité des actions IA

## 🎯 Roadmap

- [ ] Cache Redis pour les requêtes IA fréquentes
- [ ] Rate limiting sur les endpoints IA
- [ ] Support multi-langues (EN, FR)
- [ ] Intégration avec d'autres LLM (Claude, Mistral)
- [ ] Analytics avancées des requêtes IA
- [ ] Notifications temps réel (WebSocket)

---

**Architecture sécurisée ✅ | IA contrôlée ✅ | Prêt pour la production ✅**