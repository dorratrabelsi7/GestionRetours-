# GestionRetours

Application web pour le sujet 6 : systeme de gestion des retours produits.

Fonctionnalites principales :

- CRUD des retours produits, non-conformites, utilisateurs, historiques et stocks
- 4 acteurs applicatifs : `ADMIN`, `QUALITE`, `EMPLOYE`, `CLIENT`
- Enregistrement et suivi des retours avec client, quantite, etat et historique
- Validation/rejet des retours par le service qualite
- Mise a jour automatique du stock lorsqu'un retour est valide
- Documentation Swagger/OpenAPI et validation Spring Validator

Le repository est organise en monorepo avec deux applications separees :

```text
GestionRetours-/
  backend/             API REST Spring Boot
  frontend/            Interface Angular
  docker-compose.yml   Lancement local backend + frontend + MySQL
  README.md            Documentation globale
```

## Architecture

```text
Angular frontend
  -> REST HTTP
Spring Boot backend
  -> JDBC
MySQL / Cloud SQL
```

En local, Docker Compose lance :

- `mysql` : base MySQL 8
- `backend` : API Spring Boot sur `http://localhost:8080/api`
- `frontend` : Angular servi par Nginx sur `http://localhost:4200`

Sur GCP, les services restent separes mais dans le meme projet :

- Projet GCP : `gestionretours-496316`
- Numero projet : `984806313426`
- Backend : Cloud Run `gestion-retours-backend`
- Frontend : Cloud Run `gestion-retours-frontend`
- Base de donnees : Cloud SQL MySQL `gestion-retours-mysql`

## Lancer en local

```bash
docker compose up --build
```

URLs :

- Frontend : `http://localhost:4200`
- Backend API : `http://localhost:8080/api`
- Swagger : `http://localhost:8080/api/swagger-ui.html`

Docker Compose lance automatiquement MySQL avec :

- Base : `gestion_retours`
- User : `admin`
- Password : `admin123`

Endpoints principaux :

- Retours : `/api/retours`
- Non-conformites : `/api/nonconformites`
- Utilisateurs : `/api/utilisateurs`
- Historiques : `/api/historiques`
- Stock : `/api/stocks`

## Backend

Le backend est dans `backend/`.

Technologies :

- Java 11
- Spring Boot 2.7.14
- Spring Web
- Spring Data JPA
- Spring Validation
- MySQL
- Swagger/OpenAPI
- Docker

Documentation detaillee : `backend/README.md`

## Frontend

Le frontend est dans `frontend/`.

Technologies :

- Angular
- TypeScript
- Nginx pour l'image Docker de production

En developpement local sans Docker :

```bash
cd backend
.\mvnw.cmd spring-boot:run
```

Le backend utilise MySQL par defaut sur `localhost:3306`.

Dans un autre terminal :

```bash
cd frontend
npm install
npm start
```

## Deploiement GCP

Le script racine deploie le backend, le frontend et Cloud SQL dans le meme projet GCP :

```powershell
.\deploy_gcp_all.ps1 `
  -ProjectId gestionretours-496316 `
  -ProjectNumber 984806313426 `
  -Region europe-west1 `
  -DbPassword "admin123"
```

Le frontend est deploye comme un service Cloud Run separe. Il consomme l'URL publique du backend.
