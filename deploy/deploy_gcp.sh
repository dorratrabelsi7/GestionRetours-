#!/bin/bash

# ============================================================================
# 🚀 Script de Déploiement GCP - Gestion des Retours Produits
# Pour: Projet gestionretours-496316 (ID: 984806313426)
# ============================================================================

set -e

# ============================================================================
# VARIABLES DE CONFIGURATION
# ============================================================================

PROJECT_ID="${1:-gestionretours-496316}"
REGION="${2:-europe-west1}"
SERVICE_NAME="gestion-retours-backend"
IMAGE_NAME="gestion-retours"
IMAGE_TAG="1.0.0"
GCR_IMAGE="gcr.io/${PROJECT_ID}/${IMAGE_NAME}:${IMAGE_TAG}"
CLOUD_SQL_INSTANCE_NAME="gestion-retours-db"
DB_NAME="gestion_retours"
DB_USER="gestion_admin"
DB_PASSWORD="${3:-GestionRetours2024!}"
MEMORY="512Mi"
CPU="1"
TIMEOUT="3600"
MIN_INSTANCES="0"
MAX_INSTANCES="10"

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

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
echo "║      🚀 DÉPLOIEMENT GCP CLOUD RUN - GESTION DES RETOURS       ║"
echo "║              Projet: $PROJECT_ID                  ║"
echo "║              Région: $REGION                            ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo -e "${NC}"

# Step 1: Validate inputs
log_info "📋 Validation des paramètres..."
if [ -z "$PROJECT_ID" ] || [ -z "$REGION" ]; then
    log_error "Usage: $0 <PROJECT_ID> <REGION> [DB_PASSWORD]"
    log_error "Exemple: $0 gestionretours-496316 europe-west1 GestionRetours2024!"
    exit 1
fi
log_success "Configuration: PROJECT_ID=$PROJECT_ID, REGION=$REGION"

# Step 2: Set GCP project
log_info "🔧 Configuration du projet GCP..."
gcloud config set project "$PROJECT_ID" || {
    log_error "Impossible de définir le projet $PROJECT_ID"
    exit 1
}
log_success "Projet GCP configuré: $PROJECT_ID"

# Step 3: Build the Docker image
log_info "🐳 Construction de l'image Docker..."
docker build -t $GCR_IMAGE \
    --build-arg JAR_FILE=target/*.jar \
    . || {
    log_error "Impossible de construire l'image Docker"
    exit 1
}
log_success "Image Docker construite: $GCR_IMAGE"

# Step 4: Configure Docker authentication
log_info "🔐 Configuration de l'authentification Docker..."
gcloud auth configure-docker --quiet || {
    log_error "Impossible de configurer Docker auth"
    exit 1
}
log_success "Docker auth configurée"

# Step 5: Push image to GCR
log_info "📤 Envoi de l'image vers Google Container Registry..."
docker push $GCR_IMAGE || {
    log_error "Impossible de pousser l'image vers GCR"
    exit 1
}
log_success "Image poussée vers GCR: $GCR_IMAGE"

# Step 6: Enable required APIs
log_info "🔌 Activation des APIs GCP requises..."
gcloud services enable run.googleapis.com \
    cloudsql.googleapis.com \
    compute.googleapis.com \
    --project=$PROJECT_ID || log_warning "APIs déjà activées ou erreur"
log_success "APIs activées"

# Step 7: Check if Cloud SQL instance exists
log_info "🗄️  Vérification de l'instance Cloud SQL..."
if gcloud sql instances describe $CLOUD_SQL_INSTANCE_NAME --project=$PROJECT_ID &>/dev/null; then
    log_success "Instance Cloud SQL trouvée: $CLOUD_SQL_INSTANCE_NAME"
    CLOUD_SQL_CONNECTION_NAME="$PROJECT_ID:$REGION:$CLOUD_SQL_INSTANCE_NAME"
else
    log_warning "Instance Cloud SQL non trouvée: $CLOUD_SQL_INSTANCE_NAME"
    log_info "ℹ️  Veuillez créer une instance Cloud SQL avant le déploiement"
    log_info "📌 Voir GCP_DEPLOYMENT.md pour les instructions"
    # Continuer quand même (peut être créée manuellement)
fi

# Step 8: Create or get service account
log_info "👤 Configuration du service account..."
SA_NAME="gestion-retours-sa"
SA_EMAIL="${SA_NAME}@${PROJECT_ID}.iam.gserviceaccount.com"

if gcloud iam service-accounts describe $SA_EMAIL --project=$PROJECT_ID &>/dev/null; then
    log_success "Service account trouvé: $SA_EMAIL"
else
    log_info "Création du service account..."
    gcloud iam service-accounts create $SA_NAME \
        --display-name="Service Account pour Gestion des Retours" \
        --project=$PROJECT_ID || log_warning "Service account peut déjà exister"
    log_success "Service account créé/trouvé"
fi

# Add Cloud SQL Client role
gcloud projects add-iam-policy-binding $PROJECT_ID \
    --member="serviceAccount:$SA_EMAIL" \
    --role="roles/cloudsql.client" \
    --condition=None \
    --quiet || log_warning "Rôle Cloud SQL Client peut déjà être attribué"

# Step 9: Deploy to Cloud Run
log_info "🚀 Déploiement sur Cloud Run..."
log_info "Service: $SERVICE_NAME"
log_info "Région: $REGION"
log_info "Image: $GCR_IMAGE"
log_info "Mémoire: $MEMORY | CPU: $CPU"

# Construire les environment variables
ENV_VARS="SPRING_PROFILES_ACTIVE=gcp,DB_PASSWORD=$DB_PASSWORD"

# Ajouter les variables Cloud SQL si l'instance existe
if [ ! -z "$CLOUD_SQL_CONNECTION_NAME" ]; then
    ENV_VARS="$ENV_VARS,CLOUDSQL_INSTANCES=$CLOUD_SQL_CONNECTION_NAME"
fi

gcloud run deploy $SERVICE_NAME \
    --image=$GCR_IMAGE \
    --platform=managed \
    --region=$REGION \
    --project=$PROJECT_ID \
    --service-account=$SA_EMAIL \
    --add-cloudsql-instances=$CLOUD_SQL_CONNECTION_NAME \
    --set-env-vars=$ENV_VARS \
    --memory=$MEMORY \
    --cpu=$CPU \
    --timeout=$TIMEOUT \
    --min-instances=$MIN_INSTANCES \
    --max-instances=$MAX_INSTANCES \
    --allow-unauthenticated \
    --quiet || {
    log_error "Impossible de déployer sur Cloud Run"
    exit 1
}

log_success "Service déployé sur Cloud Run!"

# Step 10: Get service URL
log_info "📍 Récupération de l'URL du service..."
SERVICE_URL=$(gcloud run services describe $SERVICE_NAME \
    --region=$REGION \
    --project=$PROJECT_ID \
    --format='value(status.url)')

log_success "URL du service: $SERVICE_URL"

# Step 11: Display access information
echo -e "\n${GREEN}════════════════════════════════════════════════════════════════${NC}"
echo -e "${GREEN}✅ DÉPLOIEMENT RÉUSSI!${NC}"
echo -e "${GREEN}════════════════════════════════════════════════════════════════${NC}\n"

echo "🔗 API Backend:"
echo "   - URL: $SERVICE_URL"
echo "   - Swagger: $SERVICE_URL/swagger-ui.html"
echo "   - API Docs: $SERVICE_URL/v3/api-docs"

echo ""
echo "📊 Informations de déploiement:"
echo "   - Project: $PROJECT_ID"
echo "   - Service: $SERVICE_NAME"
echo "   - Region: $REGION"
echo "   - Image: $GCR_IMAGE"
echo ""

echo "🗄️  Base de données:"
echo "   - Instance: $CLOUD_SQL_INSTANCE_NAME"
echo "   - Database: $DB_NAME"
echo "   - User: $DB_USER"
echo ""

echo "📝 Prochaines étapes:"
echo "   1. Vérifier les logs: gcloud run services logs read $SERVICE_NAME --region=$REGION --limit=50"
echo "   2. Tester l'API: curl $SERVICE_URL/api/utilisateurs"
echo "   3. Déployer le Frontend: yarn build && gsutil -m cp -r dist/* gs://gestion-retours-frontend-$PROJECT_ID/"
echo ""

echo "🐛 Dépannage:"
echo "   - Logs: gcloud run services logs read $SERVICE_NAME --region=$REGION --follow"
echo "   - Métriques: gcloud monitoring metrics-descriptors list --filter=resource.type=cloud_run_revision"
echo ""

log_success "Déploiement terminé avec succès!"
