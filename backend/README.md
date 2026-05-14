# Systeme de gestion des retours produits

Sujet 6 - Application web de gestion des retours produits, des non-conformites, de l'historique et du stock.

Ce repository contient le backend Spring Boot. Le frontend Angular est livre dans le dossier racine `frontend/` et consomme cette API REST.

## Technologies

- Java 11, compatible avec l'image Docker Java 17
- Spring Boot 2.7.14
- Spring Web, Spring Data JPA, Spring Validation
- MySQL 8
- Swagger/OpenAPI avec springdoc-openapi
- Docker et Docker Compose
- Google Cloud Run + Cloud SQL MySQL

## Architecture backend

```text
src/main/java/com/itbs/retour
  config/        Configuration Spring Security et OpenAPI
  controllers/   API REST
  dto/           DTO avec validation Spring Validator
  entities/      Entites JPA
  repositories/  Acces base de donnees
  services/      Logique metier
  convertors/    Conversion Entity <-> DTO
```

Entites demandees par l'enonce :

- `RetourProduit` : id, produit, client, raison, etatTraitement, date
- `NonConformite` : id, description, gravite, date, retour/produit
- `Utilisateur` : id, nom, email, role
- `HistoriqueRetour` : id, retour, action, employe, date
- `ProduitStock` : id, nomProduit, quantiteDisponible, quantiteRetournee

Fonctionnalites presentes :

- CRUD des retours, utilisateurs, non-conformites, historiques et stocks
- Suivi des retours par etat, client et periode
- Validation/rejet des retours par le service qualite
- Mise a jour automatique du stock lorsqu'un retour est valide
- Historique automatique lors de la creation, validation et rejet des retours
- 4 acteurs : `ADMIN`, `QUALITE`, `EMPLOYE`, `CLIENT`
- Documentation Swagger/OpenAPI
- Validation des donnees avec `@Valid`, `@NotBlank`, `@NotNull`, `@Email`, `@Size`

## Lancement local avec Docker

```bash
docker compose up --build
```

URLs locales :

- API : `http://localhost:8080/api`
- Swagger : `http://localhost:8080/api/swagger-ui.html`
- OpenAPI JSON : `http://localhost:8080/api/v3/api-docs`
- MySQL : `localhost:3306`, base `gestion_retours`, user `admin`, password `admin123`

## Lancement local sans Docker

Creer une base MySQL :

```sql
CREATE DATABASE gestion_retours;
```

Configurer `src/main/resources/application.properties`, puis lancer :

```bash
mvn spring-boot:run
```

## Deploiement backend sur GCP

Projet GCP :

- Project ID : `gestionretours-496316`
- Project number : `984806313426`
- Region par defaut : `europe-west1`

Prérequis :

- Google Cloud SDK installe
- `gcloud auth login`
- Facturation activee sur le projet GCP

Commande Windows PowerShell :

```powershell
.\deploy\deploy_cloudrun.ps1 `
  -ProjectId gestionretours-496316 `
  -ProjectNumber 984806313426 `
  -Region europe-west1 `
  -DbPassword "admin123"
```

Le script :

- active les APIs necessaires
- cree/verifie Cloud SQL MySQL
- cree la base `gestion_retours`
- cree/met a jour l'utilisateur `admin`
- donne le role `Cloud SQL Client` au service account Cloud Run
- construit l'image Docker avec Cloud Build
- deploie le backend sur Cloud Run avec le profil `gcp`

## Endpoints principaux

```text
GET    /api/retours
GET    /api/retours/{id}
POST   /api/retours/add
PUT    /api/retours/update/{id}
PUT    /api/retours/valider/{id}
PUT    /api/retours/rejeter/{id}
DELETE /api/retours/delete/{id}

GET    /api/nonconformites
POST   /api/nonconformites/add
PUT    /api/nonconformites/update/{id}
DELETE /api/nonconformites/delete/{id}

GET    /api/utilisateurs
POST   /api/utilisateurs/add
PUT    /api/utilisateurs/update/{id}
DELETE /api/utilisateurs/delete/{id}

GET    /api/historiques
POST   /api/historiques/add
DELETE /api/historiques/delete/{id}

GET    /api/stocks
GET    /api/stocks/{id}
GET    /api/stocks/produit/{nomProduit}
POST   /api/stocks/add
PUT    /api/stocks/update/{id}
DELETE /api/stocks/delete/{id}
```

## Frontend Angular

Le frontend Angular est dans le dossier racine `frontend/` :

```text
frontend/
  src/
  angular.json
  Dockerfile
```

En production, l'URL API doit pointer vers :

```text
https://URL_CLOUD_RUN/api
```

Le backend accepte deja `http://localhost:4200` pour le developpement Angular local.
