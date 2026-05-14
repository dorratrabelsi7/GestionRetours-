# 🚀 Guide de Déploiement GCP - Système de Gestion des Retours

## 📋 Informations Projet

- **Projet GCP ID**: `gestionretours-496316`
- **Numéro Projet**: `984806313426`
- **Région**: `europe-west1` (Belgique)
- **Backend Service**: Cloud Run
- **Database**: Cloud SQL (MySQL 8.0)
- **Frontend**: Cloud Storage + Cloud CDN

---

## ✅ Prérequis

### 1. Outils Locaux Requis
```bash
# Installer gcloud CLI
# https://cloud.google.com/sdk/docs/install

# Vérifier l'installation
gcloud --version
gcloud auth login
gcloud config set project gestionretours-496316
```

### 2. Permissions Requises
- Cloud Run Admin
- Cloud SQL Admin
- Container Registry Service Agent
- Service Account User

---

## 🗄️ Étape 1 : Configurer Cloud SQL

### 1.1 Créer l'Instance Cloud SQL

```bash
# Variables
PROJECT_ID="gestionretours-496316"
REGION="europe-west1"
INSTANCE_NAME="gestion-retours-db"
DB_NAME="gestion_retours"
DB_USER="gestion_admin"
DB_PASSWORD="GestionRetours2024!"  # À sécuriser avec Secret Manager

# Créer l'instance MySQL
gcloud sql instances create $INSTANCE_NAME \
  --database-version=MYSQL_8_0 \
  --tier=db-f1-micro \
  --region=$REGION \
  --project=$PROJECT_ID \
  --backup \
  --enable-bin-log \
  --enable-point-in-time-recovery

# ✅ Attendre ~5-10 minutes
```

### 1.2 Créer la Base de Données et l'Utilisateur

```bash
# Créer la base de données
gcloud sql databases create $DB_NAME \
  --instance=$INSTANCE_NAME \
  --project=$PROJECT_ID

# Créer l'utilisateur
gcloud sql users create $DB_USER \
  --instance=$INSTANCE_NAME \
  --password=$DB_PASSWORD \
  --project=$PROJECT_ID

# Vérifier la création
gcloud sql databases list --instance=$INSTANCE_NAME --project=$PROJECT_ID
gcloud sql users list --instance=$INSTANCE_NAME --project=$PROJECT_ID
```

### 1.3 Autoriser Cloud Run à accéder à Cloud SQL

```bash
# Obtenir l'adresse IP de l'instance Cloud SQL
gcloud sql instances describe $INSTANCE_NAME \
  --project=$PROJECT_ID \
  --format='get(ipAddresses[0].ipAddress)'

# Alternative: Utiliser Cloud SQL Auth Proxy (RECOMMANDÉ)
# Voir la section "Déployer sur Cloud Run"
```

---

## 🐳 Étape 2 : Construire et Pousser l'Image Docker

### 2.1 Configurer Container Registry

```bash
# Activer Container Registry API
gcloud services enable containerregistry.googleapis.com \
  --project=$PROJECT_ID

# Configurer Docker pour GCR
gcloud auth configure-docker \
  --quiet \
  --project=$PROJECT_ID
```

### 2.2 Construire l'Image

```bash
# Depuis le répertoire du projet
cd /path/to/Backend_Projet

# Construire l'image
docker build -t gcr.io/$PROJECT_ID/gestion-retours:1.0.0 .

# Vérifier l'image
docker images | grep gestion-retours
```

### 2.3 Pousser vers GCR

```bash
# Pousser l'image
docker push gcr.io/$PROJECT_ID/gestion-retours:1.0.0

# Vérifier sur GCR
gcloud container images list --project=$PROJECT_ID
gcloud container images describe gcr.io/$PROJECT_ID/gestion-retours:1.0.0 \
  --project=$PROJECT_ID
```

---

## 🚀 Étape 3 : Déployer sur Cloud Run

### 3.1 Option A : Avec Cloud SQL Auth Proxy (RECOMMANDÉ)

```bash
# Créer un service account
SA_NAME="cloud-sql-client"
gcloud iam service-accounts create $SA_NAME \
  --display-name="Cloud SQL Client pour Gestion Retours" \
  --project=$PROJECT_ID

# Ajouter les rôles
gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:${SA_NAME}@${PROJECT_ID}.iam.gserviceaccount.com" \
  --role="roles/cloudsql.client"

# Déployer sur Cloud Run avec Cloud SQL Auth Proxy
gcloud run deploy gestion-retours-backend \
  --image=gcr.io/$PROJECT_ID/gestion-retours:1.0.0 \
  --platform=managed \
  --region=$REGION \
  --project=$PROJECT_ID \
  --service-account="${SA_NAME}@${PROJECT_ID}.iam.gserviceaccount.com" \
  --add-cloudsql-instances=$PROJECT_ID:$REGION:$INSTANCE_NAME \
  --set-env-vars=SPRING_PROFILES_ACTIVE=gcp,DB_PASSWORD=$DB_PASSWORD \
  --memory=512Mi \
  --cpu=1 \
  --timeout=3600 \
  --min-instances=0 \
  --max-instances=10 \
  --allow-unauthenticated
```

### 3.2 Option B : Avec Direct Connection

```bash
# Obtenir l'IP publique de Cloud SQL
CLOUD_SQL_IP=$(gcloud sql instances describe $INSTANCE_NAME \
  --project=$PROJECT_ID \
  --format='get(ipAddresses[0].ipAddress)')

# Déployer
gcloud run deploy gestion-retours-backend \
  --image=gcr.io/$PROJECT_ID/gestion-retours:1.0.0 \
  --platform=managed \
  --region=$REGION \
  --project=$PROJECT_ID \
  --set-env-vars=SPRING_PROFILES_ACTIVE=gcp,CLOUD_SQL_IP=$CLOUD_SQL_IP,DB_PASSWORD=$DB_PASSWORD \
  --memory=512Mi \
  --cpu=1 \
  --timeout=3600 \
  --allow-unauthenticated
```

### 3.3 Vérifier le Déploiement

```bash
# Obtenir l'URL du service
gcloud run services describe gestion-retours-backend \
  --region=$REGION \
  --project=$PROJECT_ID \
  --format='value(status.url)'

# Tester l'API
curl https://<URL>/api/v3/api-docs
curl https://<URL>/api/swagger-ui.html
```

---

## 🌍 Étape 4 : Déployer le Frontend Angular

### 4.1 Construire le Frontend

```bash
# Frontend: Projet Angular
cd /path/to/frontend-angular

# Construire la version de production
ng build --configuration production

# Vérifier les fichiers
ls -la dist/gestion-retours/
```

### 4.2 Uploader sur Cloud Storage

```bash
# Créer un bucket
BUCKET_NAME="gestion-retours-frontend-${PROJECT_ID}"

gsutil mb -p $PROJECT_ID \
  -l $REGION \
  gs://$BUCKET_NAME/

# Configurer pour hosting web
gsutil web set -m index.html -e index.html gs://$BUCKET_NAME/

# Uploader les fichiers
gsutil -m cp -r dist/gestion-retours/* gs://$BUCKET_NAME/

# Rendre public
gsutil iam ch allUsers:objectViewer gs://$BUCKET_NAME/
```

### 4.3 Configurer Cloud CDN

```bash
# Créer un Load Balancer avec Cloud CDN
# Console GCP > Compute Engine > Load Balancing
# Ou utiliser gcloud:

gcloud compute backend-buckets create gestion-retours-backend-bucket \
  --gcs-uri-prefix=gs://$BUCKET_NAME/ \
  --enable-cdn

gcloud compute url-maps create gestion-retours-lb \
  --default-backend-bucket=gestion-retours-backend-bucket

gcloud compute ssl-certificates create gestion-retours-ssl \
  --certificate=/path/to/cert.pem \
  --private-key=/path/to/key.pem

gcloud compute target-https-proxies create gestion-retours-https \
  --url-map=gestion-retours-lb \
  --ssl-certificates=gestion-retours-ssl

gcloud compute forwarding-rules create gestion-retours-frontend \
  --global \
  --target-https-proxy=gestion-retours-https \
  --address=gestion-retours-ip \
  --ports=443
```

---

## 🔗 Configuration de la Communication Backend-Frontend

### Configuration Frontend (environment.prod.ts)
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://<URL-BACKEND>/api',
  apiVersion: 'v1'
};
```

### Configuration CORS Backend

Ajouter à `config/SecurityConfig.java` :
```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://<FRONTEND-URL>")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

---

## 📊 Étape 5 : Monitoring et Logs

### 5.1 Voir les Logs

```bash
# Logs Cloud Run
gcloud run services describe gestion-retours-backend \
  --region=$REGION \
  --project=$PROJECT_ID

# Stream des logs en temps réel
gcloud run services logs read gestion-retours-backend \
  --region=$REGION \
  --project=$PROJECT_ID \
  --limit=50 \
  --follow

# Logs Cloud SQL
gcloud sql operations list \
  --instance=$INSTANCE_NAME \
  --project=$PROJECT_ID
```

### 5.2 Monitoring

```bash
# Créer une alerte Cloud Monitoring
gcloud monitoring alert-policies create \
  --notification-channels=<CHANNEL_ID> \
  --display-name="Gestion Retours - Error Rate" \
  --condition-display-name="High Error Rate" \
  --condition-threshold-value=0.05
```

---

## 🔐 Sécurité - Bonnes Pratiques

### 1. Gérer les Secrets

```bash
# Utiliser Secret Manager pour les passwords
echo -n "$DB_PASSWORD" | gcloud secrets create db-password \
  --data-file=- \
  --project=$PROJECT_ID

# Accéder dans le code
# Via environment variables ou Cloud Secret Manager client library
```

### 2. Certificats SSL

```bash
# Générer un certificat Let's Encrypt
certbot certonly --standalone -d gestion-retours.example.com

# Importer dans Cloud Run
# Utiliser Cloud Load Balancer ou Google Managed Certificates
```

### 3. Firewall Cloud SQL

```bash
# Restreindre l'accès
gcloud sql instances patch $INSTANCE_NAME \
  --require-ssl \
  --project=$PROJECT_ID
```

---

## 📋 Checklist de Déploiement

- [ ] Cloud SQL instance créée et testée
- [ ] Base de données et utilisateur créés
- [ ] Docker image construite et testée localement
- [ ] Image poussée vers Container Registry
- [ ] Service Cloud Run déployé et accessible
- [ ] CORS configuré sur Backend
- [ ] Frontend construit et uploadé sur Cloud Storage
- [ ] Cloud CDN configuré
- [ ] Domaine personnalisé configuré (optionnel)
- [ ] Monitoring et alertes en place
- [ ] Tests end-to-end réussis

---

## 🐛 Troubleshooting

### Erreur: "Cloud SQL connection refused"
```bash
# Vérifier l'instance Cloud SQL
gcloud sql instances describe $INSTANCE_NAME --project=$PROJECT_ID

# Vérifier les Cloud SQL Auth Proxy logs
# Cloud Run > Logs
```

### Erreur: "403 Forbidden" sur Cloud Storage
```bash
# Vérifier les permissions
gsutil iam ch allUsers:objectViewer gs://$BUCKET_NAME/
```

### Frontend ne communique pas avec Backend
```bash
# Vérifier CORS
# Voir les headers: Access-Control-Allow-Origin
curl -H "Origin: https://frontend.com" https://backend/api/utilisateurs -v
```

---

## 💰 Estimation des Coûts (Gratuit/Tier)

| Service | Tier Gratuit | Coût Estimé |
|---------|-------------|------------|
| Cloud Run | 2M requêtes/mois | ~$5/mois |
| Cloud SQL | - | ~$10-20/mois |
| Cloud Storage | 5GB | Inclus |
| Cloud CDN | 1TB/mois | ~$0.12/GB |
| **Total** | - | **~$20-30/mois** |

---

## 📞 Support et Documentation

- GCP Cloud Run Docs: https://cloud.google.com/run/docs
- Cloud SQL Docs: https://cloud.google.com/sql/docs
- Cloud Storage Docs: https://cloud.google.com/storage/docs

