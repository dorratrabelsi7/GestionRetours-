param(
    [Parameter(Mandatory = $false)]
    [string]$ProjectId = "gestionretours-496316",

    [Parameter(Mandatory = $false)]
    [string]$ProjectNumber = "984806313426",

    [Parameter(Mandatory = $false)]
    [string]$Region = "europe-west1",

    [Parameter(Mandatory = $false)]
    [string]$DbPassword = "admin123"
)

$ErrorActionPreference = "Continue"

$RootDir = $PSScriptRoot
$BackendDir = Join-Path $RootDir "backend"
$FrontendDir = Join-Path $RootDir "frontend"
$BackendImage = "gcr.io/$ProjectId/gestion-retours:1.0.1"
$FrontendImage = "gcr.io/$ProjectId/gestion-retours-frontend:1.0.0"
$BackendService = "gestion-retours-backend"
$FrontendService = "gestion-retours-frontend"
$RuntimeServiceAccount = "$ProjectNumber-compute@developer.gserviceaccount.com"

function Write-Step($Message) {
    Write-Host ""
    Write-Host "==> $Message"
}

$Gcloud = (Get-Command gcloud.ps1 -ErrorAction SilentlyContinue).Source
if (-not $Gcloud) {
    $Gcloud = (Get-Command gcloud.cmd -ErrorAction SilentlyContinue).Source
}
if (-not $Gcloud) {
    throw "gcloud CLI n'est pas installe."
}

Write-Step "Configuration du projet GCP"
& $Gcloud config set project $ProjectId | Out-Null

Write-Step "Deploiement backend + Cloud SQL"
Push-Location $BackendDir
& ".\deploy\deploy_cloudrun.ps1" `
    -ProjectId $ProjectId `
    -ProjectNumber $ProjectNumber `
    -Region $Region `
    -DbPassword $DbPassword
Pop-Location

$BackendUrl = & $Gcloud run services describe $BackendService `
    --platform=managed `
    --region=$Region `
    --format="value(status.url)" `
    --project=$ProjectId

$ApiUrl = "$BackendUrl/api"

Write-Step "Build frontend avec API_URL=$ApiUrl"
Push-Location $FrontendDir
& $Gcloud builds submit `
    --project=$ProjectId `
    --timeout=1800s `
    --substitutions="_API_URL=$ApiUrl" `
    --config=cloudbuild.yaml
Pop-Location

Write-Step "Deploiement frontend Cloud Run"
& $Gcloud run deploy $FrontendService `
    --image=$FrontendImage `
    --platform=managed `
    --region=$Region `
    --allow-unauthenticated `
    --port=80 `
    --memory=256Mi `
    --cpu=1 `
    --timeout=120 `
    --max-instances=5 `
    --min-instances=0 `
    --project=$ProjectId `
    --quiet

$FrontendUrl = & $Gcloud run services describe $FrontendService `
    --platform=managed `
    --region=$Region `
    --format="value(status.url)" `
    --project=$ProjectId

Write-Host ""
Write-Host "Deploiement complet."
Write-Host "Frontend: $FrontendUrl"
Write-Host "Backend:  $ApiUrl"
Write-Host "Swagger:  $ApiUrl/swagger-ui.html"
