# 🚀 DÉMARRAGE RAPIDE - Windows PowerShell

## ✅ Prérequis (Installation)

### 1️⃣ Installer Docker Desktop

1. Télécharger: https://www.docker.com/products/docker-desktop
2. Installer et redémarrer
3. Vérifier:
```powershell
docker --version
docker-compose --version
```

### 2️⃣ Installer Git

1. Télécharger: https://git-scm.com/download/win
2. Installer avec les paramètres par défaut
3. Vérifier:
```powershell
git --version
```

### 3️⃣ Installer Java 17 (optionnel - pour build local)

1. Télécharger: https://www.oracle.com/java/technologies/downloads/
2. Installer
3. Vérifier:
```powershell
java -version
```

### 4️⃣ Installer Google Cloud SDK (pour GCP)

1. Télécharger: https://cloud.google.com/sdk/docs/install-cloud-sdk
2. Installer
3. Initialiser:
```powershell
gcloud init
```

### 5️⃣ Installer Node.js & Angular (pour frontend)

1. Télécharger: https://nodejs.org/ (LTS)
2. Installer
3. Installer Angular CLI:
```powershell
npm install -g @angular/cli
```

---

## 🚀 DÉMARRAGE LOCAL (3 commandes)

### Étape 1: Cloner le backend

```powershell
# Aller au dossier workspace
cd C:\Users\LENOVO\eclipse-workspace

# Cloner le repository
git clone https://github.com/dorratrabelsi7/GestionRetours-.git
cd Backend_Projet
```

### Étape 2: Démarrer avec Docker

```powershell
# Démarrer l'application complète (MySQL + Backend)
docker-compose up -d

# Vérifier que tout fonctionne (attendre 30-40 secondes)
docker-compose logs -f backend

# Quand vous voyez "Started BackendProjetApplication", c'est prêt!
# Appuyez sur Ctrl+C pour arrêter les logs
```

### Étape 3: Tester l'API

**Swagger UI** (Recommandé):
```
http://localhost:8080/api/swagger-ui.html
```

**Ou via PowerShell**:
```powershell
Invoke-WebRequest http://localhost:8080/api/utilisateurs | ConvertTo-Json
```

---

## 📦 Arrêter l'application

```powershell
# Arrêter les conteneurs
docker-compose down

# Arrêter et supprimer les volumes (réinitialiser BD)
docker-compose down -v
```

---

## 🔍 Dépanner

### Erreur: "Port 8080 already in use"

```powershell
# Voir les processus qui utilisent le port
netstat -ano | findstr :8080

# Arrêter le processus (remplacer PID)
taskkill /PID <PID> /F
```

### Erreur: "Cannot start service mysql"

```powershell
# Attendre quelques secondes et relancer
docker-compose down
docker-compose up -d

# Vérifier les logs
docker-compose logs mysql
```

### Voir les logs en temps réel

```powershell
# Backend logs
docker-compose logs -f backend

# MySQL logs
docker-compose logs -f mysql

# Tous les logs
docker-compose logs -f
```

---

## 📝 Tester les Endpoints

### Via Swagger UI

```
http://localhost:8080/api/swagger-ui.html
```

Vous pouvez directement tester tous les endpoints graphiquement!

### Via PowerShell

```powershell
# Récupérer tous les utilisateurs
Invoke-WebRequest http://localhost:8080/api/utilisateurs

# Créer un utilisateur
$body = @{
    nom = "Jean Dupont"
    email = "jean@test.com"
    role = "QUALITE"
} | ConvertTo-Json

Invoke-WebRequest -Uri http://localhost:8080/api/utilisateurs/add `
  -Method POST `
  -ContentType "application/json" `
  -Body $body

# Récupérer les retours
Invoke-WebRequest http://localhost:8080/api/retours

# Créer un retour
$retour = @{
    produit = "Laptop"
    raison = "Écran cassé"
    etatTraitement = "EN_ATTENTE"
    date = "2026-05-13"
    nomClient = "Client ABC"
} | ConvertTo-Json

Invoke-WebRequest -Uri http://localhost:8080/api/retours/add `
  -Method POST `
  -ContentType "application/json" `
  -Body $retour
```

---

## ☁️ DÉPLOIEMENT GCP

### Prérequis

```powershell
# Vérifier gcloud
gcloud --version

# Se connecter à GCP
gcloud auth login

# Vérifier le projet
gcloud config list
```

### Déployer avec le script PowerShell

```powershell
# S'assurer d'être dans le dossier Backend_Projet
cd C:\Users\LENOVO\eclipse-workspace\Backend_Projet

# Exécuter le script
.\deploy\deploy_cloudrun.ps1 -ProjectId backend-projet-12345 -Region europe-west1

# Le script va:
# ✅ Créer la base de données Cloud SQL
# ✅ Builder l'image Docker
# ✅ Pousser vers Google Container Registry
# ✅ Déployer sur Cloud Run
# ✅ Afficher l'URL de l'application

# L'URL ressemble à:
# https://gestion-retours-backend-xxxxx-ew.a.run.app/api
```

---

## 🎨 CRÉER LE FRONTEND ANGULAR

### Étape 1: Installer Node.js et Angular CLI

```powershell
# Vérifier l'installation
node --version   # v20.x
npm --version    # 10.x
ng version       # Angular 17.x
```

### Étape 2: Créer le projet Angular

```powershell
# Aller au dossier parent
cd C:\Users\LENOVO\eclipse-workspace

# Créer le projet
ng new frontend-gestion-retours --routing --style=scss

# Attendre 5-10 minutes...

# Aller dans le dossier
cd frontend-gestion-retours
```

### Étape 3: Installer les dépendances

```powershell
npm install bootstrap @ng-bootstrap/ng-bootstrap
```

### Étape 4: Démarrer le développement

```powershell
# Terminal 1: Backend
cd C:\Users\LENOVO\eclipse-workspace\Backend_Projet
docker-compose up -d

# Terminal 2: Frontend
cd C:\Users\LENOVO\eclipse-workspace\frontend-gestion-retours
ng serve

# L'application sera sur:
# http://localhost:4200
```

### Étape 5: Créer les services et composants

Pour plus de détails, consultez: [FRONTEND_SETUP_GUIDE.md](../FRONTEND_SETUP_GUIDE.md)

---

## 🔗 Architecture Complète

```
┌──────────────────────────────────┐
│    Frontend Angular (4200)       │
│  - Utilisateurs                  │
│  - Retours Produits              │
│  - Non-conformités               │
│  - Historiques                   │
└────────────┬─────────────────────┘
             │ HTTP/HTTPS
             ▼
┌──────────────────────────────────┐
│   Backend Spring Boot (8080)     │
│  - Controllers REST              │
│  - Services Métier               │
│  - Validation Données            │
│  - Swagger/OpenAPI               │
└────────────┬─────────────────────┘
             │ JDBC
             ▼
┌──────────────────────────────────┐
│    MySQL Database (3306)         │
│  - Utilisateurs                  │
│  - Retours                       │
│  - Non-conformités               │
│  - Historiques                   │
└──────────────────────────────────┘
```

---

## 📊 Fichiers Importants

| Fichier | Rôle |
|---------|------|
| `README.md` | Documentation complète |
| `QUICK_START.md` | Démarrage rapide |
| `GCP_DEPLOYMENT_GUIDE.md` | Guide GCP détaillé |
| `FRONTEND_SETUP_GUIDE.md` | Guide frontend Angular |
| `docker-compose.yml` | Orchestration Docker |
| `Dockerfile` | Image Docker |
| `pom.xml` | Dépendances Maven |

---

## ✨ Checklist Démarrage Rapide

- [ ] Docker Desktop installé
- [ ] Git installé
- [ ] Backend cloné
- [ ] `docker-compose up -d` exécuté
- [ ] Backend fonctionnel (Swagger UI)
- [ ] Node.js installé (pour frontend)
- [ ] Angular CLI installé
- [ ] Frontend créé avec `ng new`
- [ ] Frontend démarré avec `ng serve`
- [ ] Communication API OK

---

## 🆘 Support Rapide

### Backend ne démarre pas
```powershell
# Vérifier les logs
docker-compose logs backend

# Redémarrer complètement
docker-compose down -v
docker-compose up -d
```

### MySQL connection refused
```powershell
# Attendre que MySQL soit prêt
docker-compose logs mysql

# Vérifier le healthcheck
docker-compose ps
```

### Frontend erreurs CORS
Ajouter à `src/main/resources/application.properties`:
```properties
server.cors.allowed-origins=http://localhost:4200
```

---

## 📞 Ressources

| Ressource | Lien |
|-----------|------|
| Docker Docs | https://docs.docker.com |
| Spring Boot Docs | https://docs.spring.io/spring-boot |
| Angular Docs | https://angular.io |
| Git Docs | https://git-scm.com/doc |
| GCP Docs | https://cloud.google.com/docs |

---

## 🎯 Commandes PowerShell Essentielles

```powershell
# Docker
docker ps                          # Voir les conteneurs actifs
docker logs <container>            # Voir les logs
docker-compose up -d               # Démarrer
docker-compose down                # Arrêter
docker-compose down -v             # Arrêter + supprimer volumes

# Git
git status                         # Voir les changements
git add .                          # Ajouter tous les fichiers
git commit -m "message"            # Committer
git push origin main               # Pousser

# Angular
ng serve                           # Démarrer le développement
ng generate component nom          # Créer un composant
ng build --prod                    # Build production

# GCP
gcloud auth login                  # Se connecter
gcloud config set project ID       # Configurer le projet
gcloud run services list           # Voir les services
gcloud run logs read service       # Voir les logs
```

---

**Prêt à démarrer?** Exécutez juste:

```powershell
docker-compose up -d
```

Puis ouvrez: http://localhost:8080/api/swagger-ui.html

**Bon développement!** 🚀
