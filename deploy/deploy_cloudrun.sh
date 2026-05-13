#!/bin/bash

# ============================================================================
# 🚀 Script de Déploiement - GCP Cloud Run
# Système de Gestion des Retours Produits
# ============================================================================

set -e  # Exit on error

# ============================================================================
# VARIABLES DE CONFIGURATION
# ============================================================================

PROJECT_ID="${1:-backend-projet-12345}"
REGION="${2:-europe-west1}"
SERVICE_NAME="gestion-retours-backend"
IMAGE_NAME="gestion-retours"
IMAGE_TAG="1.0.0"
GCR_IMAGE="gcr.io/${PROJECT_ID}/${IMAGE_NAME}:${IMAGE_TAG}"
INSTANCE_NAME="gestion-retours-mysql"
DB_NAME="gestion_retours"
DB_USER="admin"
DB_PASSWORD="${3:-admin123}"
MEMORY="512Mi"
TIMEOUT="3600"
MIN_INSTANCES="0"
MAX_INSTANCES="10"

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# ============================================================================
# FUNCTIONS
# ============================================================================

log_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

log_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

log_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

log_error() {
    echo -e "${RED}❌ $1${NC}"
}

# ============================================================================
# MAIN DEPLOYMENT STEPS
# ============================================================================

echo -e "${BLUE}"
echo "╔════════════════════════════════════════════════════════════════╗"
echo "║    🚀 DÉPLOIEMENT GCP CLOUD RUN - GESTION DES RETOURS        ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo -e "${NC}"

# Step 1: Validate inputs
log_info "📋 Validation des paramètres..."
if [ -z "$PROJECT_ID" ] || [ -z "$REGION" ]; then
    log_error "Usage: $0 <PROJECT_ID> <REGION> [DB_PASSWORD]"
    log_error "Exemple: $0 backend-projet-12345 europe-west1 admin123"
    exit 1
fi
log_success "Configuration: PROJECT_ID=$PROJECT_ID, REGION=$REGION"

# Step 2: Set GCP project
log_info "🔧 Configuration du projet GCP..."
gcloud config set project "$PROJECT_ID" 2>/dev/null || {
    log_error "Impossible de définir le projet $PROJECT_ID"
    exit 1
}
log_success "Projet GCP configuré: $PROJECT_ID"

# Step 3: Enable required APIs
log_info "🔌 Activation des APIs GCP..."
gcloud services enable run.googleapis.com \
    sqladmin.googleapis.com \
    compute.googleapis.com \
    artifactregistry.googleapis.com \
    cloudbuild.googleapis.com \
    --project="$PROJECT_ID" 2>/dev/null

log_success "APIs activées"

# Step 4: Check for Cloud SQL instance
log_info "🗄️  Vérification de l'instance Cloud SQL..."
if ! gcloud sql instances describe "$INSTANCE_NAME" --project="$PROJECT_ID" &>/dev/null; then
    log_warning "Instance Cloud SQL '$INSTANCE_NAME' non trouvée"
    log_info "📦 Création de l'instance Cloud SQL..."
    
    gcloud sql instances create "$INSTANCE_NAME" \
        --database-version=MYSQL_8_0 \
        --tier=db-f1-micro \
        --region="$REGION" \
        --availability-type=REGIONAL \
        --backup-start-time=03:00 \
        --project="$PROJECT_ID" \
        --quiet || {
        log_error "Erreur lors de la création de l'instance Cloud SQL"
        exit 1
    }
    
    log_success "Instance Cloud SQL créée"
    log_info "⏳ Attendre quelques minutes que l'instance soit prête..."
    sleep 30
else
    log_success "Instance Cloud SQL trouvée: $INSTANCE_NAME"
fi

# Step 5: Create database and user
log_info "🗄️  Configuration de la base de données..."
gcloud sql databases create "$DB_NAME" \
    --instance="$INSTANCE_NAME" \
    --project="$PROJECT_ID" \
    --quiet 2>/dev/null || log_warning "Base de données $DB_NAME existe déjà"

gcloud sql users create "$DB_USER" \
    --instance="$INSTANCE_NAME" \
    --password="$DB_PASSWORD" \
    --project="$PROJECT_ID" \
    --quiet 2>/dev/null || {
    log_info "Mise à jour du mot de passe pour $DB_USER..."
    gcloud sql users set-password "$DB_USER" \
        --instance="$INSTANCE_NAME" \
        --password="$DB_PASSWORD" \
        --project="$PROJECT_ID" \
        --quiet
}

log_success "Base de données configurée"

# Step 6: Build Docker image with Cloud Build
log_info "🐳 Construction de l'image Docker avec Cloud Build..."

# Check if Dockerfile exists
if [ ! -f "Dockerfile" ]; then
    log_error "Dockerfile non trouvé"
    exit 1
fi

gcloud builds submit \
    --tag "$GCR_IMAGE" \
    --project="$PROJECT_ID" \
    --region="$REGION" \
    --timeout=1800s \
    . || {
    log_error "Erreur lors de la construction de l'image Docker"
    exit 1
}

log_success "Image Docker construite et pushée: $GCR_IMAGE"

# Step 7: Get Cloud SQL connection string
log_info "🔗 Récupération de la chaîne de connexion Cloud SQL..."
CONNECTION_NAME=$(gcloud sql instances describe "$INSTANCE_NAME" \
    --project="$PROJECT_ID" \
    --format='value(connectionName)')

if [ -z "$CONNECTION_NAME" ]; then
    log_error "Impossible d'obtenir la chaîne de connexion Cloud SQL"
    exit 1
fi

DB_URL="jdbc:mysql://localhost:3306/${DB_NAME}?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"

log_success "Chaîne de connexion: $CONNECTION_NAME"

# Step 8: Deploy to Cloud Run
log_info "🚀 Déploiement sur Cloud Run..."

gcloud run deploy "$SERVICE_NAME" \
    --image "$GCR_IMAGE" \
    --platform managed \
    --region "$REGION" \
    --allow-unauthenticated \
    --memory "$MEMORY" \
    --timeout "$TIMEOUT" \
    --max-instances "$MAX_INSTANCES" \
    --min-instances "$MIN_INSTANCES" \
    --set-env-vars "SPRING_DATASOURCE_URL=${DB_URL},\
SPRING_DATASOURCE_USERNAME=${DB_USER},\
SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD},\
SPRING_JPA_HIBERNATE_DDL_AUTO=update,\
SPRING_PROFILES_ACTIVE=prod,\
LOGGING_LEVEL_ROOT=INFO,\
LOGGING_LEVEL_COM_ITBS_RETOUR=DEBUG" \
    --project="$PROJECT_ID" \
    --quiet || {
    log_error "Erreur lors du déploiement sur Cloud Run"
    exit 1
}

log_success "Service déployé sur Cloud Run"

# Step 9: Get service URL
log_info "📍 Récupération de l'URL du service..."
SERVICE_URL=$(gcloud run services describe "$SERVICE_NAME" \
    --platform managed \
    --region "$REGION" \
    --format 'value(status.url)' \
    --project="$PROJECT_ID")

if [ -z "$SERVICE_URL" ]; then
    log_error "Impossible d'obtenir l'URL du service"
    exit 1
fi

# Step 10: Configure Cloud SQL proxy authorization
log_info "🔐 Configuration des autorisations Cloud SQL..."
gcloud run services add-iam-policy-binding "$SERVICE_NAME" \
    --member="serviceAccount:${PROJECT_ID}@appspot.gserviceaccount.com" \
    --role="roles/cloudsql.client" \
    --platform managed \
    --region "$REGION" \
    --project="$PROJECT_ID" \
    --quiet 2>/dev/null || log_warning "Impossible de configurer les autorisations IAM"

log_success "Autorisations configurées"

# Step 11: Display summary
echo ""
echo -e "${GREEN}"
echo "╔════════════════════════════════════════════════════════════════╗"
echo "║              ✅ DÉPLOIEMENT RÉUSSI !                          ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo -e "${NC}"

cat << EOF

📊 RÉSUMÉ DU DÉPLOIEMENT:
─────────────────────────────────────────────────────────────────

  🎯 Projet GCP:           $PROJECT_ID
  📍 Région:               $REGION
  🐳 Image Docker:         $GCR_IMAGE
  🌐 Service Cloud Run:    $SERVICE_NAME
  🗄️  Instance SQL:        $INSTANCE_NAME
  💾 Base de données:      $DB_NAME

🔗 ACCÈS À L'APPLICATION:
─────────────────────────────────────────────────────────────────

  📡 API Base URL:
     $SERVICE_URL/api

  📖 Swagger UI (Documentation API):
     $SERVICE_URL/api/swagger-ui.html

  📋 OpenAPI JSON:
     $SERVICE_URL/api/v3/api-docs

  🗄️  Cloud SQL Instance:
     $INSTANCE_NAME

⚙️  VARIABLES D'ENVIRONNEMENT:
─────────────────────────────────────────────────────────────────

  SPRING_DATASOURCE_URL: jdbc:mysql://localhost:3306/$DB_NAME
  SPRING_DATASOURCE_USERNAME: $DB_USER
  SPRING_PROFILES_ACTIVE: prod

📝 PROCHAINES ÉTAPES:
─────────────────────────────────────────────────────────────────

  1. Vérifier que l'application est en ligne:
     curl $SERVICE_URL/api/v3/api-docs

  2. Consulter les logs Cloud Run:
     gcloud run logs read $SERVICE_NAME --region=$REGION

  3. Configurer CORS si nécessaire (frontend):
     Ajouter $SERVICE_URL à la configuration CORS

  4. (Optionnel) Configurer un domaine personnalisé:
     gcloud run domain-mappings create --service=$SERVICE_NAME \\
       --domain=api.votredomaine.com

🔒 SÉCURITÉ:
─────────────────────────────────────────────────────────────────

  ✅ IAM Service Account: ${PROJECT_ID}@appspot.gserviceaccount.com
  ✅ Cloud SQL Private IP: À configurer via VPC
  ✅ Cloud Run: Accès public (--allow-unauthenticated)

📚 RESSOURCES UTILES:
─────────────────────────────────────────────────────────────────

  Cloud Run Dashboard:
  https://console.cloud.google.com/run?project=$PROJECT_ID

  Cloud SQL Console:
  https://console.cloud.google.com/sql?project=$PROJECT_ID

  Artifacts Registry:
  https://console.cloud.google.com/artifacts?project=$PROJECT_ID

─────────────────────────────────────────────────────────────────

EOF

log_success "Déploiement terminé avec succès!"

# Step 12: Optional - Run health check
log_info "🏥 Vérification de santé..."
sleep 5

HEALTH_CHECK=$(curl -s -o /dev/null -w "%{http_code}" "$SERVICE_URL/api/v3/api-docs" 2>/dev/null)

if [ "$HEALTH_CHECK" = "200" ]; then
    log_success "✅ Application en ligne et fonctionnelle!"
else
    log_warning "⚠️  Impossible de vérifier la santé (Code: $HEALTH_CHECK)"
    log_info "L'application peut être en cours de démarrage, attendez quelques minutes..."
fi

exit 0
