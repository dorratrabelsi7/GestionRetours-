# 🚀 Guide de Démarrage Rapide

## Installation en 5 minutes

### Option 1: Avec Docker Compose (Recommandé ⭐)

```bash
# 1. Cloner le repository
git clone https://github.com/dorratrabelsi7/GestionRetours-.git
cd Backend_Projet

# 2. Lancer l'application
docker-compose up -d

# 3. Attendre le démarrage (30 secondes environ)
docker-compose logs -f backend

# 4. Accéder à l'application
# API: http://localhost:8080/api
# Swagger UI: http://localhost:8080/api/swagger-ui.html
# Base de données: localhost:3306 (user: root, password: root)
```

### Option 2: Exécution Locale (sans Docker)

#### Prérequis
- Java 17+
- Maven 3.8+
- MySQL 8.0 en cours d'exécution

#### Étapes

```bash
# 1. Cloner le repository
git clone https://github.com/dorratrabelsi7/GestionRetours-.git
cd Backend_Projet

# 2. Créer la base de données
mysql -u root -p
> CREATE DATABASE gestion_retours;
> EXIT;

# 3. Configurer l'application (optionnel)
# Éditer: src/main/resources/application.properties
# spring.datasource.url=jdbc:mysql://localhost:3306/gestion_retours?useSSL=false&serverTimezone=UTC
# spring.datasource.username=root
# spring.datasource.password=votre_mot_de_passe

# 4. Lancer l'application
mvn clean spring-boot:run

# L'application démarre sur: http://localhost:8080/api
```

---

## 🧪 Tester l'API

### Test 1: Récupérer tous les utilisateurs
```bash
curl -X GET http://localhost:8080/api/utilisateurs
```

### Test 2: Créer un utilisateur
```bash
curl -X POST http://localhost:8080/api/utilisateurs \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Jean Dupont",
    "email": "jean@example.com",
    "role": "QUALITE",
    "telephone": "+216 98 123 456",
    "adresse": "123 Rue de Paris, Tunis"
  }'
```

### Test 3: Créer un retour
```bash
curl -X POST http://localhost:8080/api/retours \
  -H "Content-Type: application/json" \
  -d '{
    "produit": "Laptop Dell XPS",
    "raison": "Écran cassé",
    "etatTraitement": "EN_ATTENTE",
    "quantite": 1,
    "clientId": 1
  }'
```

---

## 📊 Accès à Swagger UI

1. Ouvrez votre navigateur
2. Allez à: `http://localhost:8080/api/swagger-ui.html`
3. Explorez et testez tous les endpoints
4. Consultez les modèles de données

---

## 🐳 Commandes Docker Utiles

### Voir les logs
```bash
docker-compose logs -f backend
docker-compose logs -f mysql
```

### Arrêter l'application
```bash
docker-compose down
```

### Arrêter et supprimer les volumes
```bash
docker-compose down -v
```

### Vérifier l'état des conteneurs
```bash
docker-compose ps
```

### Se connecter à la base de données
```bash
docker-compose exec mysql mysql -u root -p
# password: root
> USE gestion_retours;
> SHOW TABLES;
```

---

## 📁 Structure des Fichiers Importants

```
Backend_Projet/
├── src/main/java/com/itbs/retour/
│   ├── controllers/          # Endpoints REST
│   ├── services/             # Logique métier
│   ├── entities/             # Modèles de données
│   ├── dto/                  # Data Transfer Objects
│   ├── repositories/         # Accès aux données
│   └── convertors/           # Conversion DTO/Entity
├── src/main/resources/
│   └── application.properties # Configuration
├── pom.xml                   # Dépendances Maven
├── Dockerfile               # Image Docker
├── docker-compose.yml       # Orchestration
├── README.md                # Documentation générale
└── API-GUIDE.md             # Guide détaillé API
```

---

## 🔧 Configuration

### Variables d'Environnement
```
SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/gestion_retours?useSSL=false&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

### Ports
- **Backend**: 8080
- **MySQL**: 3306
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html

---

## ❓ Dépannage

### Erreur "Port 8080 already in use"
```bash
# Trouver le processus utilisant le port
lsof -i :8080
# Tuer le processus
kill -9 <PID>
```

### Erreur de connexion à la base de données
```bash
# Vérifier que MySQL est en cours d'exécution
docker-compose ps

# Vérifier les logs MySQL
docker-compose logs mysql

# Redémarrer les services
docker-compose restart
```

### Erreur "Compilation échouée"
```bash
# Nettoyer et reconstruire
mvn clean package -DskipTests

# Vérifier Java
java -version

# Vérifier Maven
mvn --version
```

---

## 📞 Support

- 📚 Consultez `README.md` pour plus de détails
- 📖 Consultez `API-GUIDE.md` pour tous les endpoints
- 🐛 Créez une issue sur GitHub
- 📧 Contactez: support@gestionretours.com

---

**Version**: 1.0.0  
**Dernière mise à jour**: 09 mai 2026  
**Prêt pour la production**: ✅
