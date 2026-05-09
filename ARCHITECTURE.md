# 🏗️ Architecture du Projet

## 📊 Diagramme en Couches

```
┌─────────────────────────────────────────────────────────────┐
│                    CLIENT (Angular/Frontend)                 │
└────────────────────────────────────────────────────────────┬┘
                             │ HTTP/JSON
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                    CONTROLLERS (REST API)                     │
│  ├─ UtilisateurController                                    │
│  ├─ RetourProduitController                                  │
│  ├─ NonConformiteController                                  │
│  └─ HistoriqueRetourController                              │
└────────────────────────────────────────────────────────────┬┘
                             │ DTOs
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                    SERVICES (Logique Métier)                 │
│  ├─ UtilisateurService                                       │
│  ├─ RetourProduitService                                     │
│  ├─ NonConformiteService                                     │
│  └─ HistoriqueRetourService                                 │
└────────────────────────────────────────────────────────────┬┘
                             │ Entités
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                    REPOSITORIES (Accès aux données)           │
│  ├─ UtilisateurRepository                                    │
│  ├─ RetourProduitRepository                                  │
│  ├─ NonConformiteRepository                                  │
│  └─ HistoriqueRetourRepository                              │
└────────────────────────────────────────────────────────────┬┘
                             │ SQL
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                    DATABASE (MySQL 8.0)                      │
│  ├─ utilisateurs                                             │
│  ├─ retours_produits                                         │
│  ├─ non_conformites                                          │
│  └─ historique_retours                                       │
└─────────────────────────────────────────────────────────────┘
```

---

## 📦 Structure des Packages

### `com.itbs.retour.entities`
**Responsabilité**: Définir les modèles JPA

- **Utilisateur.java**
  - Identifiant unique (id)
  - Informations personnelles (nom, email, telephone, adresse)
  - Rôle (ADMIN, QUALITE, EMPLOYE, CLIENT)
  - Statut actif
  - Relations: OneToMany avec RetourProduit et HistoriqueRetour

- **RetourProduit.java**
  - Informations du retour (produit, raison, quantite)
  - État du traitement (EN_ATTENTE, EN_COURS, VALIDE, REJETE)
  - Date de création
  - Relation: ManyToOne avec Utilisateur (client)
  - Relations: OneToMany avec NonConformite et HistoriqueRetour

- **NonConformite.java**
  - Description de la non-conformité
  - Gravité (FAIBLE, MOYENNE, ELEVEE)
  - Référence unique
  - Date de détection
  - Relation: ManyToOne avec RetourProduit

- **HistoriqueRetour.java**
  - Description de l'action effectuée
  - Anciens et nouveaux statuts
  - Date de l'action
  - Relation: ManyToOne avec RetourProduit et Utilisateur (employe)

### `com.itbs.retour.dto`
**Responsabilité**: Transférer les données entre client et serveur

- **UtilisateurDTO.java**
  - Mêmes attributs que l'entité
  - Validations: @NotBlank, @Email, @NotNull
  - Annotations Swagger pour documentation

- **RetourProduitDTO.java**
  - Mêmes attributs que l'entité
  - Validations: @NotBlank, @Min, @Size
  - Format de date ISO 8601
  - clientId pour liaison

- **NonConformiteDTO.java**
  - Mêmes attributs que l'entité
  - retourId pour liaison
  - Validations complètes

- **HistoriqueRetourDTO.java**
  - Mêmes attributs que l'entité
  - retourId et employeId pour liaisons
  - Validations complètes

### `com.itbs.retour.repositories`
**Responsabilité**: Accéder à la base de données

- **UtilisateurRepository.java**
  ```java
  List<Utilisateur> findByRole(Role role)
  Optional<Utilisateur> findByEmail(String email)
  List<Utilisateur> findActiveUsersByRole(Role role)
  List<Utilisateur> findByActif(Boolean actif)
  ```

- **RetourProduitRepository.java**
  ```java
  List<RetourProduit> findByEtatTraitement(EtatTraitement etat)
  List<RetourProduit> findByClientId(int clientId)
  List<RetourProduit> findByDateBetween(LocalDateTime start, LocalDateTime end)
  List<RetourProduit> findByEtatTraitementOrderByDate(EtatTraitement etat)
  long countByEtatTraitement(EtatTraitement etat)
  ```

- **NonConformiteRepository.java**
  ```java
  List<NonConformite> findByGravite(Gravite gravite)
  List<NonConformite> findByRetourId(int retourId)
  Optional<NonConformite> findByReference(String reference)
  long countByGravite(Gravite gravite)
  ```

- **HistoriqueRetourRepository.java**
  ```java
  List<HistoriqueRetour> findByRetourId(int retourId)
  List<HistoriqueRetour> findByEmployeId(int employeId)
  List<HistoriqueRetour> findByDateBetween(LocalDateTime start, LocalDateTime end)
  List<HistoriqueRetour> findRecentHistoriqueByRetour(int retourId)
  ```

### `com.itbs.retour.services`
**Responsabilité**: Implémenter la logique métier

- **UtilisateurService.java**
  - CRUD complet
  - Recherche par rôle et email
  - Activation/Désactivation
  - Validation unicité email

- **RetourProduitService.java**
  - CRUD complet
  - Gestion des états (EN_ATTENTE, EN_COURS, VALIDE, REJETE)
  - Recherche par état, client, période
  - Statistiques par état

- **NonConformiteService.java**
  - CRUD complet
  - Génération de référence unique
  - Recherche par gravité
  - Statistiques par gravité

- **HistoriqueRetourService.java**
  - CRUD complet
  - Enregistrement avec changement de statuts
  - Recherche par retour, employé, période

### `com.itbs.retour.controllers`
**Responsabilité**: Exposer les APIs REST

- **UtilisateurController.java**
  - RequestMapping: `/utilisateurs`
  - CRUD: GET, POST, PUT, DELETE
  - Actions spéciales: activer, désactiver
  - Endpoints additionnels: par rôle, par statut

- **RetourProduitController.java**
  - RequestMapping: `/retours`
  - CRUD: GET, POST, PUT, DELETE
  - Actions spéciales: valider, rejeter, en-cours
  - Statistiques: compter par état

- **NonConformiteController.java**
  - RequestMapping: `/non-conformites`
  - CRUD: GET, POST, PUT, DELETE
  - Recherche: par gravité, par retour, par référence
  - Statistiques: compter par gravité

- **HistoriqueRetourController.java**
  - RequestMapping: `/historique-retours`
  - CRUD: GET, DELETE
  - POST: enregistrer action avec/sans statuts
  - Recherche: par retour, par employé, par période

### `com.itbs.retour.convertors`
**Responsabilité**: Convertir entre DTO et Entités

- Utilise ModelMapper pour le mapping automatique
- Méthode `toDto(Entity)` pour convertir entité en DTO
- Méthode `fromDto(DTO)` pour convertir DTO en entité
- Méthode `toListDto(List<Entity>)` pour lister

---

## 🔄 Flux de Données

### 1. Créer un retour

```
Frontend                    Backend                      Database
   │                          │                            │
   ├─ POST /retours ────────>│                            │
   │  RetourProduitDTO       │                            │
   │                          ├─ Valider DTO              │
   │                          ├─ fromDto()                │
   │                          ├─ enregistrerRetour()      │
   │                          ├─ INSERT ──────────────────>│
   │                          │<────────────── Retour       │
   │<─────── toDto() ────────│                            │
   │  RetourProduitDTO       │                            │
   │  201 CREATED            │                            │
```

### 2. Modifier un retour

```
Frontend                    Backend                      Database
   │                          │                            │
   ├─ PUT /retours/1 ───────>│                            │
   │  RetourProduitDTO       │                            │
   │                          ├─ Trouver retour ────────>│
   │                          │<──── Retour trouvé ──────│
   │                          ├─ Valider DTO              │
   │                          ├─ Mettre à jour            │
   │                          ├─ UPDATE ─────────────────>│
   │<─────── toDto() ────────│                            │
   │  RetourProduitDTO       │                            │
   │  200 OK                 │                            │
```

### 3. Valider un retour

```
Frontend                    Backend                      Database
   │                          │                            │
   ├─ PUT /retours/1/valider>│                            │
   │                          ├─ Trouver retour ────────>│
   │                          │<──── Retour trouvé ──────│
   │                          ├─ Changer état VALIDE      │
   │                          ├─ UPDATE ─────────────────>│
   │                          ├─ POST historique          │
   │                          ├─ INSERT ─────────────────>│
   │<──── 200 OK ────────────│                            │
```

---

## 🧩 Dépendances

```
Spring Boot 4.0.6
├── Spring Data JPA
├── Spring Web MVC
├── Spring Validation
├── Spring CORS
├── MySQL Driver
├── Lombok
├── ModelMapper
└── Swagger/OpenAPI
```

---

## 🔐 Sécurité et Validations

### Au niveau DTO
- `@NotNull` - Champ obligatoire
- `@NotBlank` - Chaîne non vide
- `@Email` - Format email
- `@Size(min, max)` - Taille
- `@Min/@Max` - Valeurs numériques

### Au niveau Service
- Vérification de l'existence (404 Not Found)
- Vérification d'unicité (409 Conflict)
- Gestion des cas limites

### Au niveau Database
- Constraints de clés étrangères
- Contraintes d'unicité
- Cascades de suppression

---

## 📈 Performance

### Optimisations
- Lazy loading des relations
- Queries personnalisées avec @Query
- Indexes sur les colonnes fréquentes
- Pagination possible (à ajouter)

### À Améliorer
- Ajouter pagination/tri
- Ajouter filtrage avancé
- Ajouter cache Redis
- Ajouter async pour les longs traitements

---

## 🧪 Points de Test

1. **Validation des données**
   - Tester champ vide
   - Tester email invalide
   - Tester taille maximale dépassée

2. **Logique métier**
   - Créer un retour → vérifier état initial
   - Valider un retour → vérifier historique
   - Rejeter un retour → vérifier état

3. **Gestion des erreurs**
   - Récupérer un ID inexistant → 404
   - Créer avec données invalides → 400
   - Email déjà utilisé → 409

---

## 📚 Patterns Utilisés

- **MVC**: Model-View-Controller
- **DAO**: Data Access Object (repositories)
- **DTO**: Data Transfer Object
- **Service Locator**: Injection de dépendances
- **Converter**: Transformation DTO/Entity

---

**Version**: 1.0.0  
**Dernière mise à jour**: 09 mai 2026
