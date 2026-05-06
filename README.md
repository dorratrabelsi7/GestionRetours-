# 🔄 Système de Gestion des Retours Produits

## 📋 Description du Projet

**Sujet 6**: Système de gestion des retours produits et des non-conformités

Cette application est une **API REST Spring Boot** permettant de gérer:
- ✅ Enregistrement et suivi des retours produits
- ✅ Gestion des non-conformités
- ✅ Historique des actions effectuées
- ✅ Gestion des utilisateurs avec rôles (ADMIN, QUALITE, EMPLOYE, CLIENT)
- ✅ Validation et traitement des retours par le service qualité

---

## 🛠️ Technologies Utilisées

### Backend
- **Java 17** - Langage de programmation
- **Spring Boot 4.0.6** - Framework web
- **Spring Data JPA** - ORM et persistance des données
- **MySQL 8.0** - Base de données relationnelle
- **Lombok** - Réduction du boilerplate
- **ModelMapper** - Conversion DTO/Entity
- **Spring Validation** - Validation des données ✅ **REQUIS**
- **Swagger/OpenAPI** - Documentation des APIs
- **Maven** - Gestion des dépendances

### DevOps
- **Docker** - Containerisation
- **Docker Compose** - Orchestration des conteneurs

---

## 📦 Structure du Projet

```
Backend_Projet/
├── src/main/java/com/itbs/
│   ├── projet/
│   │   └── BackendProjetApplication.java      # Classe principale
│   └── retour/
│       ├── controllers/                         # Endpoints REST
│       │   ├── RetourProduitController.java
│       │   ├── NonConformiteController.java
│       │   ├── HistoriqueRetourController.java
│       │   ├── UtilisateurController.java
│       │   └── GlobalExceptionHandler.java     # Gestion d'erreurs globale
│       ├── services/                            # Logique métier
│       │   ├── RetourProduitService.java
│       │   ├── NonConformiteService.java
│       │   ├── HistoriqueRetourService.java
│       │   └── UtilisateurService.java
│       ├── entities/                            # Modèles JPA
│       │   ├── RetourProduit.java
│       │   ├── NonConformite.java
│       │   ├── HistoriqueRetour.java
│       │   ├── Utilisateur.java
│       │   ├── EtatTraitement.java            # Enum
│       │   ├── Gravite.java                   # Enum
│       │   └── Role.java                      # Enum
│       ├── dto/                                 # Data Transfer Objects (avec validation)
│       │   ├── RetourProduitDTO.java
│       │   ├── NonConformiteDTO.java
│       │   ├── HistoriqueRetourDTO.java
│       │   └── UtilisateurDTO.java
│       ├── repositories/                        # Accès aux données
│       │   ├── RetourProduitRepository.java
│       │   ├── NonConformiteRepository.java
│       │   ├── HistoriqueRetourRepository.java
│       │   └── UtilisateurRepository.java
│       └── convertors/                          # Conversion DTO/Entity
│           ├── RetourProduitConvertor.java
│           ├── NonConformiteConvertor.java
│           ├── HistoriqueRetourConvertor.java
│           └── UtilisateurConvertor.java
├── src/main/resources/
│   └── application.properties                   # Configuration Spring
├── pom.xml                                      # Dépendances Maven
├── Dockerfile                                   # Image Docker
├── docker-compose.yml                           # Orchestration Docker
└── README.md                                    # Ce fichier

```

---

## 🚀 Installation et Exécution

### Prérequis
- Java 17+
- Maven 3.8+
- Docker & Docker Compose (optionnel)
- MySQL 8.0 (pour exécution sans Docker)

### Option 1: Exécution Locale (sans Docker)

#### 1. Cloner le repository
```bash
git clone https://github.com/dorratrabelsi7/GestionRetours-.git
cd Backend_Projet
```

#### 2. Configurer la base de données
Créez une base de données MySQL:
```sql
CREATE DATABASE gestion_retours;
```

#### 3. Configurer l'application
Modifiez `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gestion_retours?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=votre_mot_de_passe
spring.jpa.hibernate.ddl-auto=update
```

#### 4. Installer les dépendances et lancer
```bash
mvn clean install
mvn spring-boot:run
```

L'application démarre sur: `http://localhost:8080/api`

### Option 2: Exécution avec Docker Compose ⭐ **RECOMMANDÉ**

#### 1. Cloner le repository
```bash
git clone https://github.com/dorratrabelsi7/GestionRetours-.git
cd Backend_Projet
```

#### 2. Lancer avec Docker Compose
```bash
docker-compose up -d
```

#### 3. Vérifier le démarrage
```bash
docker-compose logs -f backend
```

L'application démarre sur: `http://localhost:8080/api`

#### 4. Arrêter les conteneurs
```bash
docker-compose down
```

---

## 📚 Documentation API - Swagger/OpenAPI

Une fois l'application lancée, accédez à la documentation interactive:

🔗 **URL**: `http://localhost:8080/api/swagger-ui.html`

Vous pouvez y:
- Consulter tous les endpoints
- Voir les modèles de données
- Tester les APIs directement

---

## 🔌 Endpoints Disponibles

### Retours Produits
```
GET    /api/retours                    # Tous les retours
GET    /api/retours/{id}               # Un retour par ID
GET    /api/retours/etat/{etat}        # Retours par état
GET    /api/retours/client/{clientId}  # Retours d'un client
GET    /api/retours/periode            # Retours par période
POST   /api/retours/add                # Créer un retour
PUT    /api/retours/update/{id}        # Modifier un retour
PUT    /api/retours/valider/{id}       # Valider un retour
PUT    /api/retours/rejeter/{id}       # Rejeter un retour
DELETE /api/retours/delete/{id}        # Supprimer un retour
```

### Non-Conformités
```
GET    /api/nonconformites             # Tous les non-conformités
GET    /api/nonconformites/{id}        # Une non-conformité par ID
GET    /api/nonconformites/gravite/{gravite}  # Par gravité
GET    /api/nonconformites/retour/{retourId}  # D'un retour
POST   /api/nonconformites/add         # Créer
PUT    /api/nonconformites/update/{id} # Modifier
DELETE /api/nonconformites/delete/{id} # Supprimer
```

### Historiques
```
GET    /api/historiques                # Tous les historiques
GET    /api/historiques/{id}           # Un historique par ID
GET    /api/historiques/retour/{retourId}   # D'un retour
GET    /api/historiques/employe/{employeId} # D'un employé
GET    /api/historiques/periode        # Par période
POST   /api/historiques/add            # Créer
DELETE /api/historiques/delete/{id}    # Supprimer
```

### Utilisateurs
```
GET    /api/utilisateurs               # Tous les utilisateurs
GET    /api/utilisateurs/{id}          # Un utilisateur par ID
GET    /api/utilisateurs/role/{role}   # Par rôle
POST   /api/utilisateurs/add           # Créer
PUT    /api/utilisateurs/update/{id}   # Modifier
DELETE /api/utilisateurs/delete/{id}   # Supprimer
```

---

## 🔐 Validation des Données

**Toutes les données sont validées** au niveau des DTOs avec:
- `@NotNull` - Champ obligatoire
- `@NotBlank` - Chaîne non vide
- `@Size` - Taille minimale/maximale
- `@Email` - Format email valide

**Exemple d'erreur de validation**:
```json
{
  "status": 400,
  "message": "Erreur de validation",
  "errors": {
    "produit": "Le produit doit avoir entre 3 et 100 caractères",
    "email": "L'email doit être valide"
  }
}
```

---

## 🌐 Configuration CORS

L'API accepte les requêtes CORS depuis:
- `http://localhost:4200` (Angular par défaut)
- `http://localhost:3000` (React par défaut)
- Tous les domaines (peut être restreint en production)

Modifiez `BackendProjetApplication.java` pour restreindre les origines en production.

---

## 📊 Modèles de Données

### RetourProduit
- **id** (int) - Identifiant unique
- **produit** (String) - Nom du produit ✅ Validé
- **raison** (String) - Raison du retour ✅ Validé
- **etatTraitement** (Enum) - EN_ATTENTE, EN_COURS, VALIDE, REJETE
- **date** (Date) - Date du retour
- **client** (Utilisateur) - Client qui effectue le retour

### NonConformite
- **id** (int) - Identifiant unique
- **description** (String) - Description ✅ Validé
- **gravite** (Enum) - FAIBLE, MOYENNE, ELEVEE
- **date** (Date) - Date de détection
- **retour** (RetourProduit) - Retour associé

### Utilisateur
- **id** (int) - Identifiant unique
- **nom** (String) - Nom ✅ Validé
- **email** (String) - Email ✅ Validé
- **role** (Enum) - ADMIN, QUALITE, EMPLOYE, CLIENT

### HistoriqueRetour
- **id** (int) - Identifiant unique
- **action** (String) - Action effectuée ✅ Validé
- **date** (Date) - Date de l'action
- **retour** (RetourProduit) - Retour associé
- **employe** (Utilisateur) - Employé responsable

---

## 🐳 Configuration Docker

### Dockerfile
- Build multi-stage pour optimiser la taille
- Alpine Linux pour légèreté
- Health check inclus

### docker-compose.yml
- MySQL 8.0 avec volume persistant
- Backend Spring Boot
- Network bridge pour communication
- Variables d'environnement centralisées

---

## 📝 Exemples de Requêtes

### Créer un retour
```bash
curl -X POST http://localhost:8080/api/retours/add \
  -H "Content-Type: application/json" \
  -d '{
    "produit": "Laptop Dell XPS",
    "raison": "Problème d'\''écran",
    "etatTraitement": "EN_ATTENTE",
    "date": "2026-05-06",
    "nomClient": "Jean Dupont"
  }'
```

### Valider un retour
```bash
curl -X PUT http://localhost:8080/api/retours/valider/1
```

### Récupérer les retours en attente
```bash
curl http://localhost:8080/api/retours/etat/EN_ATTENTE
```

---

## 🧪 Tests

Pour lancer les tests:
```bash
mvn test
```

---

## 📈 Déploiement Cloud (Google Cloud Platform)

### Prérequis
- Compte GCP
- Google Cloud SDK installé

### Étapes de déploiement
```bash
# 1. Authentifier
gcloud auth login

# 2. Configurer le projet
gcloud config set project your-project-id

# 3. Créer une instance Cloud SQL (MySQL)
gcloud sql instances create gestion-retours-db \
  --database-version=MYSQL_8_0 \
  --region=europe-west1

# 4. Déployer sur Cloud Run
gcloud run deploy gestion-retours-backend \
  --source . \
  --platform managed \
  --region europe-west1 \
  --allow-unauthenticated
```

---

## 🤝 Contribution

Les contributions sont bienvenues! Veuillez:
1. Fork le repository
2. Créer une branche feature (`git checkout -b feature/AmazingFeature`)
3. Commit vos changements (`git commit -m 'Add AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

---

## 📞 Support

Pour toute question ou problème, contactez:
- **Email**: support@gestionretours.com
- **Issues GitHub**: [Créer une issue](https://github.com/dorratrabelsi7/GestionRetours-/issues)

---

## 📄 License

Ce projet est sous license MIT - voir le fichier [LICENSE](LICENSE) pour plus de détails.

---

## ✅ Conformité au Sujet

✅ **Architecture propre** - Séparation en couches (controllers, services, entities, repositories)
✅ **Validation des données** - Spring Validator sur tous les DTOs
✅ **Documentation API** - Swagger/OpenAPI intégré
✅ **CRUD complet** - Pour toutes les entités
✅ **Docker** - Dockerfile et docker-compose.yml
✅ **README.md** - Documentation complète

---

**Dernière mise à jour**: 06 mai 2026
**Version**: 1.0.0
**Statut**: Production Ready ✅
