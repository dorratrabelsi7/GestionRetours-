# 🚀 GUIDE DE TEST RAPIDE - Backend Projet

## 📋 Table des matières
1. [Lancement avec Docker Compose](#docker-compose)
2. [Lancement Local](#local)
3. [Tests des Endpoints](#endpoints)
4. [Vérification Swagger](#swagger)

---

## 🐳 Lancement avec Docker Compose {#docker-compose}

### Démarrer l'application

```bash
cd C:\Users\LENOVO\eclipse-workspace\Backend_Projet

# Lancer les conteneurs (MySQL + Backend)
docker-compose up -d

# Voir les logs
docker-compose logs -f backend

# Vérifier l'état
docker-compose ps
```

### Accès de l'application

- **Backend API**: `http://localhost:8080/api`
- **Swagger UI**: `http://localhost:8080/api/swagger-ui.html`
- **MySQL**: `localhost:3306`
  - Username: `root`
  - Password: `root`
  - Database: `gestion_retours`

### Arrêter l'application

```bash
docker-compose down

# Arrêter et supprimer les volumes
docker-compose down -v
```

---

## 💻 Lancement Local {#local}

### Prérequis
- Java 17+ installé
- Maven 3.8+ installé
- MySQL 8.0 en local sur port 3306

### 1. Créer la base de données

```sql
CREATE DATABASE gestion_retours;
```

### 2. Configurer application.properties

Modifier: `src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gestion_retours?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=votre_mot_de_passe
spring.jpa.hibernate.ddl-auto=update
```

### 3. Installer et lancer

```bash
cd C:\Users\LENOVO\eclipse-workspace\Backend_Projet

# Installer les dépendances
mvn clean install

# Lancer l'application
mvn spring-boot:run
```

### 4. Accès

- **API**: `http://localhost:8080/api`
- **Swagger**: `http://localhost:8080/api/swagger-ui.html`

---

## 🧪 Tests des Endpoints {#endpoints}

### 1. RETOURS PRODUITS

#### Lister tous les retours
```bash
curl -X GET http://localhost:8080/api/retours \
  -H "Content-Type: application/json"
```

#### Créer un retour
```bash
curl -X POST http://localhost:8080/api/retours/add \
  -H "Content-Type: application/json" \
  -d '{
    "produit": "Laptop Dell XPS",
    "raison": "Problème d'\''écran résolution insuffisante",
    "etatTraitement": "EN_ATTENTE",
    "date": "2026-05-06",
    "nomClient": "Jean Dupont"
  }'
```

#### Récupérer un retour par ID
```bash
curl -X GET http://localhost:8080/api/retours/1 \
  -H "Content-Type: application/json"
```

#### Filtrer par état
```bash
curl -X GET http://localhost:8080/api/retours/etat/EN_ATTENTE \
  -H "Content-Type: application/json"
```

#### Mettre à jour un retour
```bash
curl -X PUT http://localhost:8080/api/retours/update/1 \
  -H "Content-Type: application/json" \
  -d '{
    "produit": "Laptop Dell XPS 15",
    "raison": "Écran cassé",
    "etatTraitement": "EN_COURS",
    "date": "2026-05-06",
    "nomClient": "Jean Dupont"
  }'
```

#### Valider un retour
```bash
curl -X PUT http://localhost:8080/api/retours/valider/1
```

#### Rejeter un retour
```bash
curl -X PUT http://localhost:8080/api/retours/rejeter/1
```

#### Supprimer un retour
```bash
curl -X DELETE http://localhost:8080/api/retours/delete/1
```

---

### 2. NON-CONFORMITÉS

#### Lister toutes les non-conformités
```bash
curl -X GET http://localhost:8080/api/nonconformites \
  -H "Content-Type: application/json"
```

#### Créer une non-conformité
```bash
curl -X POST http://localhost:8080/api/nonconformites/add \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Écran cassé lors du déballage",
    "gravite": "ELEVEE",
    "date": "2026-05-06",
    "retourId": 1
  }'
```

#### Filtrer par gravité
```bash
curl -X GET http://localhost:8080/api/nonconformites/gravite/ELEVEE \
  -H "Content-Type: application/json"
```

#### Mettre à jour une non-conformité
```bash
curl -X PUT http://localhost:8080/api/nonconformites/update/1 \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Écran cassé - dégâts majeurs",
    "gravite": "ELEVEE",
    "date": "2026-05-06"
  }'
```

---

### 3. UTILISATEURS

#### Lister tous les utilisateurs
```bash
curl -X GET http://localhost:8080/api/utilisateurs \
  -H "Content-Type: application/json"
```

#### Créer un utilisateur
```bash
curl -X POST http://localhost:8080/api/utilisateurs/add \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Alice Durand",
    "email": "alice.durand@company.com",
    "role": "QUALITE"
  }'
```

#### Filtrer par rôle
```bash
curl -X GET http://localhost:8080/api/utilisateurs/role/ADMIN \
  -H "Content-Type: application/json"
```

#### Mettre à jour un utilisateur
```bash
curl -X PUT http://localhost:8080/api/utilisateurs/update/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Alice Durand",
    "email": "alice.durand.new@company.com",
    "role": "ADMIN"
  }'
```

---

### 4. HISTORIQUES

#### Lister tous les historiques
```bash
curl -X GET http://localhost:8080/api/historiques \
  -H "Content-Type: application/json"
```

#### Créer un historique
```bash
curl -X POST http://localhost:8080/api/historiques/add \
  -H "Content-Type: application/json" \
  -d '{
    "action": "Retour validé par la qualité",
    "date": "2026-05-06",
    "retourId": 1,
    "employeId": 1
  }'
```

#### Historique d'un retour
```bash
curl -X GET http://localhost:8080/api/historiques/retour/1 \
  -H "Content-Type: application/json"
```

---

## 📊 Tests de Validation {#validation}

### Erreur de validation (données invalides)

```bash
# Champ "produit" vide (erreur attendue: 400 Bad Request)
curl -X POST http://localhost:8080/api/retours/add \
  -H "Content-Type: application/json" \
  -d '{
    "produit": "",
    "raison": "Problème",
    "etatTraitement": "EN_ATTENTE",
    "date": "2026-05-06"
  }'

# Réponse attendue:
# {
#   "status": 400,
#   "message": "Erreur de validation",
#   "errors": {
#     "produit": "Le produit ne peut pas être vide"
#   }
# }
```

### Erreur ressource non trouvée

```bash
# ID inexistant (erreur attendue: 404 Not Found)
curl -X GET http://localhost:8080/api/retours/99999 \
  -H "Content-Type: application/json"

# Réponse attendue:
# {
#   "status": 404,
#   "message": "Retour non trouvé"
# }
```

---

## 🔍 Vérification Swagger {#swagger}

### Accéder à Swagger UI

1. Ouvrir dans le navigateur: `http://localhost:8080/api/swagger-ui.html`

2. Vous verrez tous les endpoints documentés avec:
   - Description de l'endpoint
   - Paramètres requis
   - Exemples de requête/réponse
   - Codes de réponse possibles

### Tester directement depuis Swagger

1. Cliquer sur un endpoint
2. Cliquer sur "Try it out"
3. Remplir les paramètres
4. Cliquer sur "Execute"
5. Voir la réponse

### Endpoints disponibles dans Swagger

**RetourProduit** (10 endpoints)
- GET /retours
- GET /retours/{id}
- GET /retours/etat/{etat}
- GET /retours/client/{clientId}
- GET /retours/periode
- POST /retours/add
- PUT /retours/update/{id}
- PUT /retours/valider/{id}
- PUT /retours/rejeter/{id}
- DELETE /retours/delete/{id}

**NonConformite** (7 endpoints)
- GET /nonconformites
- GET /nonconformites/{id}
- GET /nonconformites/gravite/{gravite}
- GET /nonconformites/retour/{retourId}
- POST /nonconformites/add
- PUT /nonconformites/update/{id}
- DELETE /nonconformites/delete/{id}

**HistoriqueRetour** (6 endpoints)
- GET /historiques
- GET /historiques/{id}
- GET /historiques/retour/{retourId}
- GET /historiques/employe/{employeId}
- GET /historiques/periode
- POST /historiques/add
- DELETE /historiques/delete/{id}

**Utilisateur** (6 endpoints)
- GET /utilisateurs
- GET /utilisateurs/{id}
- GET /utilisateurs/role/{role}
- POST /utilisateurs/add
- PUT /utilisateurs/update/{id}
- DELETE /utilisateurs/delete/{id}

---

## 🛠️ Commandes Utiles

### Docker Compose
```bash
# Démarrer en background
docker-compose up -d

# Voir les logs en temps réel
docker-compose logs -f

# Voir les logs du backend uniquement
docker-compose logs -f backend

# Arrêter sans supprimer
docker-compose stop

# Redémarrer
docker-compose restart

# Supprimer tout (images, volumes)
docker-compose down -v

# Rebuilder les images
docker-compose up -d --build
```

### Maven
```bash
# Nettoyer et installer
mvn clean install

# Lancer l'app
mvn spring-boot:run

# Exécuter les tests
mvn test

# Créer le jar
mvn package
```

### Logs d'application
```bash
# Depuis Docker
docker-compose logs -f backend

# Depuis local (dans le terminal)
# Visible dans la console Maven
```

---

## ✅ Checklist Vérification

- [ ] Docker Compose démaré (`docker-compose ps` montre 2 conteneurs)
- [ ] Backend accessible: `http://localhost:8080/api`
- [ ] Swagger UI fonctionnel: `http://localhost:8080/api/swagger-ui.html`
- [ ] Base de données créée: `mysql> show databases;` affiche `gestion_retours`
- [ ] Au moins un retour créé avec succès
- [ ] Validation fonctionne (erreur 400 quand données invalides)
- [ ] Swagger UI liste tous les 27 endpoints
- [ ] CORS fonctionne (pas d'erreur CORS dans navigateur)

---

## 🐛 Troubleshooting

### Port 8080 déjà utilisé
```bash
# Windows - trouver le processus
netstat -ano | findstr :8080

# Tuer le processus
taskkill /PID <PID> /F

# Ou changer le port dans docker-compose.yml
# ports:
#   - "8081:8080"
```

### MySQL ne démarre pas
```bash
# Vérifier les logs
docker-compose logs mysql

# Redémarrer MySQL
docker-compose restart mysql
```

### Backend se crash au démarrage
```bash
# Voir les logs détaillés
docker-compose logs -f backend

# Vérifier la configuration application.properties
# Vérifier la connexion à MySQL
```

### Erreur de connexion à la base de données
```bash
# Vérifier que MySQL est running
docker-compose ps

# Test de connexion
docker exec gestion-retours-mysql mysql -h localhost -u root -proot gestion_retours -e "SELECT 1;"
```

---

## 📞 Support

Pour plus d'informations:
- Consultez `README.md`
- Consultez `BACKEND_STATUS.md`
- Consultez `VERIFICATION_BACKEND_COMPLET.md`

---

**Dernière mise à jour**: 6 mai 2026  
**Version**: 1.0.0
