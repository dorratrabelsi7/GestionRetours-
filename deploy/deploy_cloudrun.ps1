# ============================================================================
# 🚀 Script de Déploiement GCP - Version PowerShell (Windows)
# Système de Gestion des Retours Produits
# ============================================================================

param(
    [Parameter(Mandatory=$true, HelpMessage="GCP Project ID")]
    [string]$ProjectId,
    
    [Parameter(Mandatory=$false, HelpMessage="GCP Region")]
    [string]$Region = "europe-west1",
    
    [Parameter(Mandatory=$false, HelpMessage="Database Password")]
    [string]$DbPassword = "admin123"
)

# Colors for output
$Green = "`e[32m"
$Blue = "`e[34m"
$Yellow = "`e[33m"
$Red = "`e[31m"
$Reset = "`e[0m"

function Write-Success {
    param([string]$Message)
    Write-Host "${Green}✅ $Message${Reset}"
}

function Write-Info {
    param([string]$Message)
    Write-Host "${Blue}ℹ️  $Message${Reset}"
}

function Write-Warning {
    param([string]$Message)
    Write-Host "${Yellow}⚠️  $Message${Reset}"
}

function Write-Error {
    param([string]$Message)
    Write-Host "${Red}❌ $Message${Reset}"
    exit 1
}

# ============================================================================
# MAIN DEPLOYMENT STEPS
# ============================================================================

Write-Host ""
Write-Host "${Blue}╔════════════════════════════════════════════════════════════════╗${Reset}"
Write-Host "${Blue}║    🚀 DÉPLOIEMENT GCP CLOUD RUN - GESTION DES RETOURS        ║${Reset}"
Write-Host "${Blue}╚════════════════════════════════════════════════════════════════╝${Reset}"
Write-Host ""

# Step 1: Validate inputs
Write-Info "📋 Validation des paramètres..."
if ([string]::IsNullOrEmpty($ProjectId)) {
    Write-Error "PROJECT_ID est requis!"
}
Write-Success "Configuration: PROJECT_ID=$ProjectId, REGION=$Region"

# Step 2: Check gcloud installation
Write-Info "🔧 Vérification de gcloud CLI..."
if (-not (Get-Command gcloud -ErrorAction SilentlyContinue)) {
    Write-Error "gcloud CLI n'est pas installé. Installez-le depuis: https://cloud.google.com/sdk"
}
Write-Success "gcloud CLI trouvé"

# Step 3: Set GCP project
Write-Info "🔧 Configuration du projet GCP..."
& gcloud config set project $ProjectId 2>&1 | Out-Null
if ($LASTEXITCODE -ne 0) {
    Write-Error "Impossible de définir le projet $ProjectId"
}
Write-Success "Projet GCP configuré: $ProjectId"

# Step 4: Enable required APIs
Write-Info "🔌 Activation des APIs GCP..."
$apis = @(
    "run.googleapis.com",
    "sqladmin.googleapis.com",
    "compute.googleapis.com",
    "artifactregistry.googleapis.com",
    "cloudbuild.googleapis.com"
)

foreach ($api in $apis) {
    & gcloud services enable $api --project=$ProjectId 2>&1 | Out-Null
}
Write-Success "APIs activées"

# Step 5: Check for Cloud SQL instance
$InstanceName = "gestion-retours-mysql"
$DbName = "gestion_retours"
$DbUser = "admin"

Write-Info "🗄️  Vérification de l'instance Cloud SQL..."
$instanceExists = & gcloud sql instances describe $InstanceName --project=$ProjectId 2>&1 | Select-String "name" | Measure-Object | Select-Object -ExpandProperty Count

if ($instanceExists -eq 0) {
    Write-Warning "Instance Cloud SQL '$InstanceName' non trouvée"
    Write-Info "📦 Création de l'instance Cloud SQL..."
    
    & gcloud sql instances create $InstanceName `
        --database-version=MYSQL_8_0 `
        --tier=db-f1-micro `
        --region=$Region `
        --availability-type=REGIONAL `
        --backup-start-time=03:00 `
        --project=$ProjectId `
        --quiet
    
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Erreur lors de la création de l'instance Cloud SQL"
    }
    
    Write-Success "Instance Cloud SQL créée"
    Write-Info "⏳ Attendre quelques minutes que l'instance soit prête..."
    Start-Sleep -Seconds 30
} else {
    Write-Success "Instance Cloud SQL trouvée: $InstanceName"
}

# Step 6: Create database and user
Write-Info "🗄️  Configuration de la base de données..."

& gcloud sql databases create $DbName `
    --instance=$InstanceName `
    --project=$ProjectId `
    --quiet 2>&1 | Out-Null

& gcloud sql users create $DbUser `
    --instance=$InstanceName `
    --password=$DbPassword `
    --project=$ProjectId `
    --quiet 2>&1 | Out-Null

if ($LASTEXITCODE -ne 0) {
    Write-Info "Mise à jour du mot de passe pour $DbUser..."
    & gcloud sql users set-password $DbUser `
        --instance=$InstanceName `
        --password=$DbPassword `
        --project=$ProjectId `
        --quiet
}

Write-Success "Base de données configurée"

# Step 7: Build Docker image with Cloud Build
Write-Info "🐳 Construction de l'image Docker avec Cloud Build..."

if (-not (Test-Path "Dockerfile")) {
    Write-Error "Dockerfile non trouvé"
}

$ImageName = "gestion-retours"
$ImageTag = "1.0.0"
$GcrImage = "gcr.io/${ProjectId}/${ImageName}:${ImageTag}"

& gcloud builds submit `
    --tag $GcrImage `
    --project=$ProjectId `
    --region=$Region `
    --timeout=1800s

if ($LASTEXITCODE -ne 0) {
    Write-Error "Erreur lors de la construction de l'image Docker"
}

Write-Success "Image Docker construite et pushée: $GcrImage"

# Step 8: Get Cloud SQL connection string
Write-Info "🔗 Récupération de la chaîne de connexion Cloud SQL..."

$ConnectionName = & gcloud sql instances describe $InstanceName `
    --project=$ProjectId `
    --format='value(connectionName)' 2>&1 | Select-Object -First 1

if ([string]::IsNullOrEmpty($ConnectionName)) {
    Write-Error "Impossible d'obtenir la chaîne de connexion Cloud SQL"
}

$DbUrl = "jdbc:mysql://localhost:3306/${DbName}?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"

Write-Success "Chaîne de connexion: $ConnectionName"

# Step 9: Deploy to Cloud Run
$ServiceName = "gestion-retours-backend"
Write-Info "🚀 Déploiement sur Cloud Run..."

$envVars = @(
    "SPRING_DATASOURCE_URL=${DbUrl}",
    "SPRING_DATASOURCE_USERNAME=${DbUser}",
    "SPRING_DATASOURCE_PASSWORD=${DbPassword}",
    "SPRING_JPA_HIBERNATE_DDL_AUTO=update",
    "SPRING_PROFILES_ACTIVE=prod",
    "LOGGING_LEVEL_ROOT=INFO",
    "LOGGING_LEVEL_COM_ITBS_RETOUR=DEBUG"
) -join ","

& gcloud run deploy $ServiceName `
    --image $GcrImage `
    --platform managed `
    --region $Region `
    --allow-unauthenticated `
    --memory 512Mi `
    --timeout 3600 `
    --max-instances 10 `
    --min-instances 0 `
    --set-env-vars=$envVars `
    --project=$ProjectId `
    --quiet

if ($LASTEXITCODE -ne 0) {
    Write-Error "Erreur lors du déploiement sur Cloud Run"
}

Write-Success "Service déployé sur Cloud Run"

# Step 10: Get service URL
Write-Info "📍 Récupération de l'URL du service..."

$ServiceUrl = & gcloud run services describe $ServiceName `
    --platform managed `
    --region $Region `
    --format 'value(status.url)' `
    --project=$ProjectId 2>&1 | Select-Object -First 1

if ([string]::IsNullOrEmpty($ServiceUrl)) {
    Write-Error "Impossible d'obtenir l'URL du service"
}

# Display summary
Write-Host ""
Write-Host "${Green}╔════════════════════════════════════════════════════════════════╗${Reset}"
Write-Host "${Green}║              ✅ DÉPLOIEMENT RÉUSSI !                          ║${Reset}"
Write-Host "${Green}╚════════════════════════════════════════════════════════════════╝${Reset}"
Write-Host ""

$summary = @"

📊 RÉSUMÉ DU DÉPLOIEMENT:
─────────────────────────────────────────────────────────────────

  🎯 Projet GCP:           $ProjectId
  📍 Région:               $Region
  🐳 Image Docker:         $GcrImage
  🌐 Service Cloud Run:    $ServiceName
  🗄️  Instance SQL:        $InstanceName
  💾 Base de données:      $DbName

🔗 ACCÈS À L'APPLICATION:
─────────────────────────────────────────────────────────────────

  📡 API Base URL:
     ${ServiceUrl}/api

  📖 Swagger UI (Documentation API):
     ${ServiceUrl}/api/swagger-ui.html

  📋 OpenAPI JSON:
     ${ServiceUrl}/api/v3/api-docs

🆔 Chaîne de connexion Cloud SQL:
─────────────────────────────────────────────────────────────────

  $ConnectionName

💾 Variables d'environnement:
─────────────────────────────────────────────────────────────────

  SPRING_DATASOURCE_URL: $DbUrl
  SPRING_DATASOURCE_USERNAME: $DbUser
  SPRING_PROFILES_ACTIVE: prod

📝 PROCHAINES ÉTAPES:
─────────────────────────────────────────────────────────────────

  1. Vérifier que l'application est en ligne:
     Invoke-WebRequest ${ServiceUrl}/api/v3/api-docs

  2. Consulter les logs Cloud Run:
     gcloud run logs read $ServiceName --region=$Region

  3. Configurer CORS si nécessaire (frontend)
     Ajouter ${ServiceUrl} à la configuration CORS

📚 RESSOURCES UTILES:
─────────────────────────────────────────────────────────────────

  Cloud Run Dashboard:
  https://console.cloud.google.com/run?project=$ProjectId

  Cloud SQL Console:
  https://console.cloud.google.com/sql?project=$ProjectId

─────────────────────────────────────────────────────────────────

"@

Write-Host $summary

Write-Success "Déploiement terminé avec succès!"

# Step 11: Optional - Run health check
Write-Info "🏥 Vérification de santé..."
Start-Sleep -Seconds 5

try {
    $response = Invoke-WebRequest "${ServiceUrl}/api/v3/api-docs" -ErrorAction SilentlyContinue
    if ($response.StatusCode -eq 200) {
        Write-Success "✅ Application en ligne et fonctionnelle!"
    }
} catch {
    Write-Warning "⚠️  Impossible de vérifier la santé"
    Write-Info "L'application peut être en cours de démarrage, attendez quelques minutes..."
}

exit 0
