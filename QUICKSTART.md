# 🚀 Guide de Démarrage Rapide - Système de Gestion des Retours

## 📥 Prérequis

- ✅ Git installé
- ✅ Docker & Docker Compose (recommandé)
- ✅ OU: Java 17+, Maven 3.8+, MySQL 8.0

---

## 🐳 Option 1: Déploiement avec Docker Compose (⭐ RECOMMANDÉ)

### Étape 1: Cloner le repository
```bash
git clone https://github.com/dorratrabelsi7/GestionRetours-.git
cd Backend_Projet
```

### Étape 2: Lancer l'application
```bash
docker-compose up -d
```

### Étape 3: Vérifier le démarrage
```bash
docker-compose logs -f backend
```

### Étape 4: Accéder à l'application

| Service | URL |
|---------|-----|
| API REST | http://localhost:8080/api |
| Swagger UI | http://localhost:8080/api/swagger-ui.html |
| MySQL | localhost:3306 (utilisateur: root, password: root) |

### Étape 5: Arrêter l'application
```bash
docker-compose down
```

---

## 🖥️ Option 2: Exécution Locale (sans Docker)

### Prérequis
- Java 17 ou supérieur
- Maven 3.8 ou supérieur
- MySQL 8.0 en cours d'exécution

### Étape 1: Créer la base de données MySQL
```bash
mysql -u root -p
```

```sql
CREATE DATABASE gestion_retours;
EXIT;
```

### Étape 2: Cloner et configurer
```bash
git clone https://github.com/dorratrabelsi7/GestionRetours-.git
cd Backend_Projet
```

Modifier `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gestion_retours?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=votre_mot_de_passe
```

### Étape 3: Compiler et lancer
```bash
mvn clean install
mvn spring-boot:run
```

### Étape 4: Accéder à l'application
```
http://localhost:8080/api/swagger-ui.html
```

---

## 🧪 Tester l'API

### Via Swagger UI
1. Ouvrir: http://localhost:8080/api/swagger-ui.html
2. Cliquer sur un endpoint
3. Cliquer "Try it out"
4. Remplir les paramètres
5. Cliquer "Execute"

### Via cURL

**Créer un utilisateur:**
```bash
curl -X POST http://localhost:8080/api/utilisateurs/add \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Jean Dupont",
    "email": "jean@example.com",
    "role": "CLIENT"
  }'
```

**Créer un retour produit:**
```bash
curl -X POST http://localhost:8080/api/retours/add \
  -H "Content-Type: application/json" \
  -d '{
    "produit": "Laptop Dell XPS",
    "raison": "Écran défectueux",
    "etatTraitement": "EN_ATTENTE",
    "date": "2026-05-10"
  }'
```

**Récupérer tous les retours:**
```bash
curl http://localhost:8080/api/retours
```

---

## 🔧 Configuration

### Variables d'Environnement (Docker)
Éditez `docker-compose.yml`:
```yaml
environment:
  SPRING_DATASOURCE_USERNAME: root
  SPRING_DATASOURCE_PASSWORD: votre_password
  SPRING_JPA_HIBERNATE_DDL_AUTO: update
```

### Configuration CORS
Pour autoriser d'autres domaines, éditez `BackendProjetApplication.java`:
```java
registry.addMapping("/api/**")
    .allowedOrigins("http://votre-domaine:port")
    .allowedMethods("GET", "POST", "PUT", "DELETE");
```

---

## 📊 Structure des Données

### États de Traitement (Enum)
- `EN_ATTENTE` - En attente de traitement
- `EN_COURS` - Traitement en cours
- `VALIDE` - Validé et accepté
- `REJETE` - Rejeté

### Niveaux de Gravité (Enum)
- `MINEUR` - Non-conformité mineure
- `MAJEUR` - Non-conformité majeure
- `CRITIQUE` - Non-conformité critique

### Rôles Utilisateurs (Enum)
- `CLIENT` - Client effectuant un retour
- `EMPLOYE` - Employé traitant la requête
- `ADMIN` - Administrateur système

---

## 🐛 Dépannage

### Le port 8080 est occupé
```bash
# Afficher le processus utilisant le port
netstat -ano | findstr :8080

# Changer le port dans application.properties
server.port=8081
```

### Erreur de connexion MySQL (Docker)
```bash
# Vérifier que le conteneur MySQL est actif
docker-compose ps

# Voir les logs MySQL
docker-compose logs mysql

# Redémarrer les services
docker-compose restart
```

### Base de données vide
Les tables sont créées automatiquement via JPA. Aucun seed data par défaut.

---

## 📝 Exemple de Workflow Complet

1. **Créer un client:**
```bash
POST /api/utilisateurs/add
{
  "nom": "Marie Durand",
  "email": "marie@example.com",
  "role": "CLIENT"
}
```

2. **Créer un retour:**
```bash
POST /api/retours/add
{
  "produit": "Smartphone Samsung",
  "raison": "Batterie défectueuse",
  "etatTraitement": "EN_ATTENTE",
  "date": "2026-05-10"
}
```

3. **Signaler une non-conformité:**
```bash
POST /api/nonconformites/add
{
  "description": "La batterie ne charge pas au-delà de 50%",
  "gravite": "MAJEUR",
  "date": "2026-05-10"
}
```

4. **Tracer l'action:**
```bash
POST /api/historiques/add
{
  "action": "Réception et évaluation du produit",
  "date": "2026-05-10"
}
```

5. **Valider le retour:**
```bash
PUT /api/retours/valider/1
```

---

## 📚 Documentation Complète

Pour une documentation complète, consultez:
- **README.md** - Vue d'ensemble du projet
- **API-GUIDE.md** - Guide détaillé des APIs
- **ARCHITECTURE.md** - Architecture du système
- **PROJECT_SUMMARY.md** - Synthèse complète

---

## 🌐 Déploiement Cloud

### Google Cloud Platform (GCP)

1. **Préparer le projet:**
```bash
gcloud auth login
gcloud config set project your-project-id
```

2. **Créer une instance Cloud SQL:**
```bash
gcloud sql instances create gestion-retours-db \
  --database-version=MYSQL_8_0 \
  --region=europe-west1
```

3. **Déployer sur Cloud Run:**
```bash
gcloud run deploy gestion-retours-backend \
  --source . \
  --platform managed \
  --region europe-west1 \
  --allow-unauthenticated
```

---

## 🆘 Support

- **Issues GitHub**: https://github.com/dorratrabelsi7/GestionRetours-/issues
- **Email**: support@gestionretours.com
- **Documentation**: Consultez les fichiers .md du projet

---

## ✅ Checklist de Vérification

- [ ] Repository cloné
- [ ] Docker Compose configuré (ou MySQL local)
- [ ] Application démarrée
- [ ] Swagger accessible sur http://localhost:8080/api/swagger-ui.html
- [ ] Créé un utilisateur test
- [ ] Créé un retour test
- [ ] API fonctionne correctement

---

**Bon développement! 🎉**
