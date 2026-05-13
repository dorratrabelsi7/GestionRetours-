# 🚀 Guide de Déploiement - Google Cloud Platform

## Table des matières
1. [Prérequis](#prérequis)
2. [Configuration GCP](#configuration-gcp)
3. [Déploiement Automatisé](#déploiement-automatisé)
4. [Déploiement Manuel](#déploiement-manuel)
5. [Vérification & Tests](#vérification--tests)
6. [Troubleshooting](#troubleshooting)

---

## 📋 Prérequis

### Outils requis
```bash
# Google Cloud SDK
curl https://sdk.cloud.google.com | bash
gcloud init

# Docker Desktop
# https://www.docker.com/products/docker-desktop

# Git
git --version  # v2.40+

# Java 17 (optionnel pour build local)
java -version
```

### Compte GCP
- Projet créé avec facturation activée
- Compte de service avec les rôles nécessaires
- gcloud CLI configuré: `gcloud init`

### Valeurs de configuration
```bash
# À remplacer par vos valeurs
PROJECT_ID="backend-projet-12345"           # ID unique du projet
REGION="europe-west1"                       # Région GCP
SERVICE_ACCOUNT_EMAIL="YOUR_EMAIL@gmail.com"
```

---

## ⚙️ Configuration GCP

### 1. Créer un projet GCP

```bash
# Via Console Cloud
# https://console.cloud.google.com/projectcreate

# Ou via gcloud
PROJECT_ID="backend-projet-12345"
gcloud projects create $PROJECT_ID --name="Backend Projet"
gcloud config set project $PROJECT_ID
```

### 2. Activer la facturation

```bash
# Via Console Cloud
# https://console.cloud.google.com/billing

# Vérifier la facturation
gcloud billing projects list
```

### 3. Activer les APIs requises

```bash
PROJECT_ID="backend-projet-12345"
gcloud config set project $PROJECT_ID

gcloud services enable \
  run.googleapis.com \
  sqladmin.googleapis.com \
  compute.googleapis.com \
  artifactregistry.googleapis.com \
  cloudbuild.googleapis.com \
  cloudresourcemanager.googleapis.com \
  iam.googleapis.com
```

### 4. Créer/Configurer un service account (optionnel)

```bash
SERVICE_ACCOUNT="gestion-retours-sa"
PROJECT_ID="backend-projet-12345"

# Créer le service account
gcloud iam service-accounts create $SERVICE_ACCOUNT \
  --display-name="Service Account pour GestionRetours Backend"

# Donner les rôles nécessaires
gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:${SERVICE_ACCOUNT}@${PROJECT_ID}.iam.gserviceaccount.com" \
  --role="roles/run.admin"

gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:${SERVICE_ACCOUNT}@${PROJECT_ID}.iam.gserviceaccount.com" \
  --role="roles/cloudsql.client"

# Créer une clé
gcloud iam service-accounts keys create key.json \
  --iam-account=${SERVICE_ACCOUNT}@${PROJECT_ID}.iam.gserviceaccount.com

# Activer la clé
export GOOGLE_APPLICATION_CREDENTIALS=$(pwd)/key.json
```

---

## 🚀 Déploiement Automatisé

### Méthode recommandée : Script Bash

```bash
# 1. Cloner le repository
git clone https://github.com/dorratrabelsi7/GestionRetours-.git
cd Backend_Projet

# 2. Rendre le script exécutable
chmod +x deploy/deploy_cloudrun.sh

# 3. Exécuter le script de déploiement
bash deploy/deploy_cloudrun.sh backend-projet-12345 europe-west1 admin123

# Paramètres:
# - PROJECT_ID (requis): backend-projet-12345
# - REGION (optionnel): europe-west1, us-central1, asia-east1
# - DB_PASSWORD (optionnel): admin123
```

### Exemple de sortie réussie

```
╔════════════════════════════════════════════════════════════════╗
║    🚀 DÉPLOIEMENT GCP CLOUD RUN - GESTION DES RETOURS        ║
╚════════════════════════════════════════════════════════════════╝

✅ Configuration: PROJECT_ID=backend-projet-12345, REGION=europe-west1
✅ Projet GCP configuré
✅ APIs activées
✅ Instance Cloud SQL trouvée
✅ Base de données configurée
✅ Image Docker construite et pushée
✅ Service déployé sur Cloud Run

╔════════════════════════════════════════════════════════════════╗
║              ✅ DÉPLOIEMENT RÉUSSI !                          ║
╚════════════════════════════════════════════════════════════════╝

📊 RÉSUMÉ DU DÉPLOIEMENT:
─────────────────────────────────────────────────────────────────
  🎯 Projet GCP:           backend-projet-12345
  📍 Région:               europe-west1
  🌐 Service Cloud Run:    https://gestion-retours-backend-xxxxx-ew.a.run.app/api
  🗄️  Instance SQL:        gestion-retours-mysql
```

---

## 📝 Déploiement Manuel

### Option 1: Builder l'image en local et pusher

```bash
PROJECT_ID="backend-projet-12345"
IMAGE_TAG="1.0.0"
REGION="europe-west1"

# 1. Authentifier Docker avec GCR
gcloud auth configure-docker gcr.io

# 2. Builder l'image
docker build -t gcr.io/$PROJECT_ID/gestion-retours:$IMAGE_TAG .

# 3. Pusher l'image vers Google Container Registry
docker push gcr.io/$PROJECT_ID/gestion-retours:$IMAGE_TAG

# 4. Vérifier que l'image est dans GCR
gcloud container images list --project=$PROJECT_ID
```

### Option 2: Utiliser Cloud Build (recommandé)

```bash
PROJECT_ID="backend-projet-12345"
IMAGE_TAG="1.0.0"

# Soumettre le build à Cloud Build
gcloud builds submit \
  --tag gcr.io/$PROJECT_ID/gestion-retours:$IMAGE_TAG \
  --region=europe-west1

# Vérifier les logs du build
gcloud builds log --limit 100
```

### Déployer sur Cloud Run

```bash
PROJECT_ID="backend-projet-12345"
SERVICE_NAME="gestion-retours-backend"
REGION="europe-west1"
IMAGE_TAG="1.0.0"

# 1. Créer l'instance Cloud SQL (si non existante)
gcloud sql instances create gestion-retours-mysql \
  --database-version=MYSQL_8_0 \
  --tier=db-f1-micro \
  --region=$REGION

# 2. Créer la base de données
gcloud sql databases create gestion_retours \
  --instance=gestion-retours-mysql

# 3. Créer l'utilisateur DB
gcloud sql users create admin \
  --instance=gestion-retours-mysql \
  --password=admin123

# 4. Déployer sur Cloud Run
gcloud run deploy $SERVICE_NAME \
  --image gcr.io/$PROJECT_ID/gestion-retours:$IMAGE_TAG \
  --platform managed \
  --region $REGION \
  --allow-unauthenticated \
  --memory 512Mi \
  --timeout 3600 \
  --max-instances 10 \
  --min-instances 0 \
  --set-env-vars "SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/gestion_retours?useSSL=false&serverTimezone=UTC,SPRING_DATASOURCE_USERNAME=admin,SPRING_DATASOURCE_PASSWORD=admin123,SPRING_JPA_HIBERNATE_DDL_AUTO=update,SPRING_PROFILES_ACTIVE=prod"
```

### Récupérer l'URL du service

```bash
gcloud run services describe gestion-retours-backend \
  --platform managed \
  --region europe-west1 \
  --format 'value(status.url)'

# Résultat:
# https://gestion-retours-backend-xxxxx-ew.a.run.app
```

---

## ✅ Vérification & Tests

### 1. Vérifier le statut du service

```bash
# Voir les services déployés
gcloud run services list --platform managed --region europe-west1

# Voir les détails du service
gcloud run services describe gestion-retours-backend \
  --platform managed \
  --region europe-west1
```

### 2. Vérifier les logs

```bash
# Afficher les 100 dernières lignes de log
gcloud run logs read gestion-retours-backend \
  --limit 100 \
  --region europe-west1

# Afficher les logs en temps réel
gcloud run logs read gestion-retours-backend \
  --limit 100 \
  --region europe-west1 \
  --follow
```

### 3. Tester l'API

```bash
# Remplacer l'URL par celle de votre service
SERVICE_URL="https://gestion-retours-backend-xxxxx-ew.a.run.app"

# Test de santé - OpenAPI JSON
curl -s $SERVICE_URL/api/v3/api-docs | head -20

# Test de santé - Swagger UI
curl -s -I $SERVICE_URL/api/swagger-ui.html | grep HTTP

# Récupérer tous les utilisateurs
curl -s $SERVICE_URL/api/utilisateurs | json_pp

# Créer un utilisateur (exemple)
curl -X POST $SERVICE_URL/api/utilisateurs/add \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Test User",
    "email": "test@example.com",
    "role": "QUALITE"
  }'
```

### 4. Vérifier Cloud SQL

```bash
# Afficher les instances SQL
gcloud sql instances list

# Se connecter à l'instance (nécessite Cloud SQL Proxy)
gcloud sql connect gestion-retours-mysql --user=admin

# Une fois connecté, exécuter:
USE gestion_retours;
SHOW TABLES;
```

---

## 🐛 Troubleshooting

### Erreur: "Permission denied" lors du build

```bash
# Solution: Configurer les rôles IAM
gcloud projects add-iam-policy-binding PROJECT_ID \
  --member="user:YOUR_EMAIL@gmail.com" \
  --role="roles/cloudbuild.builds.editor"

gcloud projects add-iam-policy-binding PROJECT_ID \
  --member="user:YOUR_EMAIL@gmail.com" \
  --role="roles/run.admin"
```

### Erreur: "Cloud SQL connection refused"

```bash
# Vérifier que Cloud SQL est prêt
gcloud sql instances describe gestion-retours-mysql

# Vérifier les variables d'environnement dans Cloud Run
gcloud run services describe gestion-retours-backend \
  --platform managed \
  --region europe-west1 \
  --format 'value(spec.template.spec.containers[0].env)'

# Redéployer avec les bonnes variables
gcloud run deploy gestion-retours-backend \
  --set-env-vars "SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/gestion_retours?useSSL=false"
```

### Erreur: "Application startup timeout"

```bash
# Augmenter le timeout dans Cloud Run
gcloud run deploy gestion-retours-backend \
  --timeout 3600 \
  --region europe-west1

# Vérifier les logs pour voir l'erreur
gcloud run logs read gestion-retours-backend --limit 50
```

### Erreur: "Dockerfile not found" lors du build

```bash
# Vérifier que le Dockerfile existe à la racine
ls -la Dockerfile

# Si absent, recréer à partir du modèle
# Voir README.md
```

### Service n'est pas accessible

```bash
# Vérifier que le service autorise l'accès non authentifié
gcloud run services add-iam-policy-binding gestion-retours-backend \
  --member="allUsers" \
  --role="roles/run.invoker" \
  --platform managed \
  --region europe-west1

# Vérifier le statut du déploiement
gcloud run revisions list --service gestion-retours-backend --region europe-west1
```

---

## 🔒 Sécurité & Best Practices

### 1. Limiter l'accès (optionnel)

```bash
# Supprimer l'accès public
gcloud run services remove-iam-policy-binding gestion-retours-backend \
  --member="allUsers" \
  --role="roles/run.invoker" \
  --platform managed \
  --region europe-west1

# Ajouter un accès spécifique
gcloud run services add-iam-policy-binding gestion-retours-backend \
  --member="user:specific@email.com" \
  --role="roles/run.invoker" \
  --platform managed \
  --region europe-west1
```

### 2. Configurer Cloud SQL avec IP privée

```bash
# À faire manuellement via Cloud Console pour plus de sécurité
# https://console.cloud.google.com/sql
```

### 3. Configurer un domaine personnalisé

```bash
# Ajouter un domaine personnalisé
gcloud run domain-mappings create \
  --service gestion-retours-backend \
  --domain api.votredomaine.com \
  --region europe-west1

# Ajouter le SSL automatique (gratuit)
# Cloud Run fournit automatiquement un certificat SSL
```

---

## 📊 Monitoring & Costs

### Afficher les coûts

```bash
# Via GCP Console
# https://console.cloud.google.com/billing

# Via CLI
gcloud billing accounts list
gcloud billing projects list
```

### Configurer des alertes

```bash
# Via Cloud Console
# https://console.cloud.google.com/monitoring
```

### Métriques disponibles

- **Cloud Run**: CPU, Memory, Request count, Latency
- **Cloud SQL**: Connections, Queries, Replication lag
- **Artifact Registry**: Storage usage

---

## 📚 Ressources Utiles

| Ressource | Lien |
|-----------|------|
| Cloud Run Documentation | https://cloud.google.com/run/docs |
| Cloud SQL Documentation | https://cloud.google.com/sql/docs |
| gcloud CLI Reference | https://cloud.google.com/sdk/gcloud/reference |
| Spring Boot GCP Integration | https://cloud.google.com/java/docs |
| GCP Console | https://console.cloud.google.com |

---

## 🔄 CI/CD avec GitHub Actions (Optionnel)

Créez `.github/workflows/deploy.yml`:

```yaml
name: Deploy to Cloud Run

on:
  push:
    branches: [ main ]

jobs:
  deploy:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v2
    
    - name: Setup Cloud SDK
      uses: google-github-actions/setup-gcloud@v0
      with:
        service_account_key: ${{ secrets.GCP_SA_KEY }}
        project_id: ${{ secrets.GCP_PROJECT_ID }}
        export_default_credentials: true
    
    - name: Build and Push to GCR
      run: |
        gcloud builds submit \
          --tag gcr.io/${{ secrets.GCP_PROJECT_ID }}/gestion-retours:latest
    
    - name: Deploy to Cloud Run
      run: |
        gcloud run deploy gestion-retours-backend \
          --image gcr.io/${{ secrets.GCP_PROJECT_ID }}/gestion-retours:latest \
          --platform managed \
          --region europe-west1 \
          --allow-unauthenticated
```

Ajouter les secrets GitHub:
- `GCP_SA_KEY`: Service account JSON key
- `GCP_PROJECT_ID`: Votre PROJECT_ID

---

## ✨ Commandes Utiles Récapitulatif

```bash
# Configuration
gcloud config set project PROJECT_ID
gcloud config get-value project

# Services Cloud Run
gcloud run services list --platform managed
gcloud run services describe SERVICE_NAME --platform managed --region REGION
gcloud run services update SERVICE_NAME --set-env-vars KEY=VALUE
gcloud run logs read SERVICE_NAME --limit 50 --follow

# Images Docker
gcloud container images list
gcloud container images describe gcr.io/PROJECT_ID/IMAGE
gcloud container images delete gcr.io/PROJECT_ID/IMAGE

# Cloud SQL
gcloud sql instances list
gcloud sql databases list --instance INSTANCE_NAME
gcloud sql connect INSTANCE_NAME --user admin

# Build
gcloud builds submit --tag IMAGE_TAG
gcloud builds list
gcloud builds log --limit 100
```

---

**Dernière mise à jour**: 13 Mai 2026  
**Version**: 1.0.0  
**Auteur**: Dorra Trabelsi
