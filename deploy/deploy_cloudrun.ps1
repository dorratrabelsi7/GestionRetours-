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

# gcloud writes some normal progress messages to stderr on Windows.
# Keep PowerShell from treating those messages as terminating errors.
$ErrorActionPreference = "Continue"

$ServiceName = "gestion-retours-backend"
$InstanceName = "gestion-retours-mysql"
$DbName = "gestion_retours"
$DbUser = "admin"
$ImageName = "gestion-retours"
$ImageTag = "1.0.1"
$Image = "gcr.io/$ProjectId/$ImageName`:$ImageTag"
$ConnectionName = "$ProjectId`:$Region`:$InstanceName"
$RuntimeServiceAccount = "$ProjectNumber-compute@developer.gserviceaccount.com"
$DbUrl = "jdbc:mysql:///$DbName`?cloudSqlInstance=$ConnectionName&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"

function Write-Step($Message) {
    Write-Host ""
    Write-Host "==> $Message"
}

Write-Step "Verification de gcloud"
$Gcloud = (Get-Command gcloud.ps1 -ErrorAction SilentlyContinue).Source
if (-not $Gcloud) {
    $Gcloud = (Get-Command gcloud.cmd -ErrorAction SilentlyContinue).Source
}
if (-not $Gcloud) {
    $Gcloud = (Get-Command gcloud -ErrorAction SilentlyContinue).Source
}
if (-not $Gcloud) {
    throw "gcloud CLI n'est pas installe."
}

Write-Step "Configuration du projet GCP: $ProjectId"
& $Gcloud config set project $ProjectId | Out-Null

Write-Step "Activation des APIs necessaires"
& $Gcloud services enable `
    run.googleapis.com `
    sqladmin.googleapis.com `
    compute.googleapis.com `
    cloudbuild.googleapis.com `
    containerregistry.googleapis.com `
    --project=$ProjectId

Write-Step "Autorisation Cloud SQL Client pour Cloud Run"
& $Gcloud projects add-iam-policy-binding $ProjectId `
    --member="serviceAccount:$RuntimeServiceAccount" `
    --role="roles/cloudsql.client" `
    --quiet | Out-Null

Write-Step "Creation/verif instance Cloud SQL: $InstanceName"
$existingInstances = & $Gcloud sql instances list `
    --project=$ProjectId `
    --format="value(name)"
$instanceExists = ($existingInstances -contains $InstanceName)

if (-not $instanceExists) {
    & $Gcloud sql instances create $InstanceName `
        --database-version=MYSQL_8_0 `
        --tier=db-f1-micro `
        --region=$Region `
        --availability-type=ZONAL `
        --project=$ProjectId `
        --quiet
}

Write-Step "Creation/verif base et utilisateur"
& $Gcloud sql databases create $DbName `
    --instance=$InstanceName `
    --project=$ProjectId `
    --quiet 1>$null 2>$null

& $Gcloud sql users create $DbUser `
    --instance=$InstanceName `
    --password=$DbPassword `
    --project=$ProjectId `
    --quiet 1>$null 2>$null
$userCreated = ($LASTEXITCODE -eq 0)

if (-not $userCreated) {
    & $Gcloud sql users set-password $DbUser `
        --instance=$InstanceName `
        --password=$DbPassword `
        --project=$ProjectId `
        --quiet
}

Write-Step "Build et push de l'image Docker avec Cloud Build"
& $Gcloud builds submit `
    --tag $Image `
    --project=$ProjectId `
    --timeout=1800s

Write-Step "Deploiement Cloud Run"
$envVars = @(
    "SPRING_PROFILES_ACTIVE=gcp",
    "SPRING_DATASOURCE_URL=$DbUrl",
    "SPRING_DATASOURCE_USERNAME=$DbUser",
    "SPRING_DATASOURCE_PASSWORD=$DbPassword",
    "SPRING_JPA_HIBERNATE_DDL_AUTO=update",
    "SERVER_SERVLET_CONTEXT_PATH=/api"
) -join ","

& $Gcloud run deploy $ServiceName `
    --image=$Image `
    --platform=managed `
    --region=$Region `
    --allow-unauthenticated `
    --service-account=$RuntimeServiceAccount `
    --memory=512Mi `
    --cpu=1 `
    --timeout=300 `
    --max-instances=10 `
    --min-instances=0 `
    --set-env-vars=$envVars `
    --project=$ProjectId `
    --quiet

$ServiceUrl = & $Gcloud run services describe $ServiceName `
    --platform=managed `
    --region=$Region `
    --format="value(status.url)" `
    --project=$ProjectId

Write-Host ""
Write-Host "Deploiement termine."
Write-Host "API:        $ServiceUrl/api"
Write-Host "Swagger:    $ServiceUrl/api/swagger-ui.html"
Write-Host "OpenAPI:    $ServiceUrl/api/v3/api-docs"
Write-Host "Cloud SQL:  $ConnectionName"
