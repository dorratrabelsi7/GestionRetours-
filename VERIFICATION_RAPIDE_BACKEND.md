# 🚀 GUIDE DE VÉRIFICATION RAPIDE DU BACKEND

## ✅ Vérifier Rapidement que le Backend est Complet

### Option 1: Vérification du Code Source (2 minutes)

#### 1. Vérifier les Entités (4 fichiers)
```bash
# Doit exister et contenir les champs spécifiés:
src/main/java/com/itbs/retour/entities/RetourProduit.java
src/main/java/com/itbs/retour/entities/NonConformite.java
src/main/java/com/itbs/retour/entities/Utilisateur.java
src/main/java/com/itbs/retour/entities/HistoriqueRetour.java

# Plus les Enums:
src/main/java/com/itbs/retour/entities/EtatTraitement.java
src/main/java/com/itbs/retour/entities/Gravite.java
src/main/java/com/itbs/retour/entities/Role.java
```

#### 2. Vérifier les Services (4 fichiers)
```bash
src/main/java/com/itbs/retour/services/RetourProduitService.java
src/main/java/com/itbs/retour/services/NonConformiteService.java
src/main/java/com/itbs/retour/services/HistoriqueRetourService.java
src/main/java/com/itbs/retour/services/UtilisateurService.java
```

#### 3. Vérifier les Controllers (4 fichiers)
```bash
src/main/java/com/itbs/retour/controllers/RetourProduitController.java
src/main/java/com/itbs/retour/controllers/NonConformiteController.java
src/main/java/com/itbs/retour/controllers/HistoriqueRetourController.java
src/main/java/com/itbs/retour/controllers/UtilisateurController.java
```

#### 4. Vérifier les Repositories (4 fichiers)
```bash
src/main/java/com/itbs/retour/repositories/RetourProduitRepository.java
src/main/java/com/itbs/retour/repositories/NonConformiteRepository.java
src/main/java/com/itbs/retour/repositories/HistoriqueRetourRepository.java
src/main/java/com/itbs/retour/repositories/UtilisateurRepository.java
```

#### 5. Vérifier les DTOs AVEC VALIDATION (4 fichiers)
```bash
src/main/java/com/itbs/retour/dto/RetourProduitDTO.java
src/main/java/com/itbs/retour/dto/NonConformiteDTO.java
src/main/java/com/itbs/retour/dto/HistoriqueRetourDTO.java
src/main/java/com/itbs/retour/dto/UtilisateurDTO.java

# Chaque DTO DOIT contenir:
# @NotBlank, @NotNull, @Size, @Email
# @Valid sur les endpoints
```

#### 6. Vérifier Swagger/OpenAPI
```bash
# Dans BackendProjetApplication.java, chercher:
# - @Bean public OpenAPI customOpenAPI()
# - springdoc-openapi dans pom.xml
```

---

### Option 2: Compilation Maven (5 minutes)

```bash
cd Backend_Projet
mvn clean compile
```

**Résultat attendu**: `BUILD SUCCESS`

Si vous voyez:
```
[INFO] BUILD SUCCESS
```

✅ Le projet compile correctement!

---

### Option 3: Tests avec Docker Compose (10 minutes)

```bash
cd Backend_Projet

# Démarrer les conteneurs
docker-compose up -d

# Attendre ~20 secondes pour le démarrage de MySQL et l'app
sleep 20

# Vérifier que le backend est UP
curl http://localhost:8080/api/swagger-ui.html

# Tester un endpoint simple
curl http://localhost:8080/api/utilisateurs

# Arrêter
docker-compose down
```

**Résultats attendus:**
- ✅ Swagger UI s'affiche
- ✅ Les endpoints répondent avec JSON
- ✅ Pas d'erreurs dans les logs

---

### Option 4: Vérification des Endpoints (2 minutes)

#### Endpoints Requis à Vérifier

**Retours Produits (Minimum 10)**
```
GET    /api/retours              → Liste tous
GET    /api/retours/{id}          → Détail
POST   /api/retours/add           → Créer (AVEC VALIDATION)
PUT    /api/retours/update/{id}   → Modifier
DELETE /api/retours/delete/{id}   → Supprimer
```

**Non-Conformités (Minimum 7)**
```
GET    /api/nonconformites        → Liste
GET    /api/nonconformites/{id}   → Détail
POST   /api/nonconformites/add    → Créer (AVEC VALIDATION)
```

**Historiques (Minimum 6)**
```
GET    /api/historiques           → Liste
POST   /api/historiques/add       → Créer
```

**Utilisateurs (Minimum 6)**
```
GET    /api/utilisateurs          → Liste
POST   /api/utilisateurs/add      → Créer (AVEC VALIDATION)
```

---

### Option 5: Validation des Données (1 minute)

```bash
# Essayer de créer un retour SANS produit (doit échouer)
curl -X POST http://localhost:8080/api/retours/add \
  -H "Content-Type: application/json" \
  -d '{
    "produit": "",
    "raison": "Test"
  }'

# Résultat attendu: HTTP 400 Bad Request avec message d'erreur
```

---

## ✅ CHECKLIST DE VÉRIFICATION RAPIDE

- [ ] Fichiers des 4 Entités présents
- [ ] Fichiers des 3 Enums présents
- [ ] Fichiers des 4 Services présents
- [ ] Fichiers des 4 Controllers présents
- [ ] Fichiers des 4 Repositories présents
- [ ] Fichiers des 4 DTOs présents avec @NotBlank, @NotNull, @Size
- [ ] BackendProjetApplication.java avec Swagger et CORS
- [ ] pom.xml avec spring-boot-starter-validation et springdoc-openapi
- [ ] docker-compose.yml présent
- [ ] Dockerfile présent
- [ ] application.properties présent
- [ ] README.md complet (>400 lignes)
- [ ] BACKEND_STATUS.md présent
- [ ] VERIFICATION_BACKEND_COMPLET.md présent
- [ ] CHECKLIST_PROJET_COMPLET.md présent

**Si TOUS cochés ✅ → BACKEND 100% COMPLET**

---

## 🎯 RÉSULTAT FINAL

### Si vous voyez tout cela:
```
✅ Tous les fichiers présents
✅ Compilation OK (BUILD SUCCESS)
✅ Docker Compose démarre sans erreur
✅ Swagger UI accessible
✅ Endpoints répondent
✅ Validation fonctionne (400 sur données invalides)
✅ Documentation complète
```

### Alors: **✅ LE BACKEND EST 100% COMPLET**

---

## 📞 En Cas de Problème

### Erreur de Compilation?
```bash
# Nettoyer et reconstruire
mvn clean install

# Ou forcer la mise à jour des dépendances
mvn clean install -U
```

### Docker ne démarre pas?
```bash
# Vérifier Docker est installé
docker --version
docker-compose --version

# Voir les logs
docker-compose logs backend
docker-compose logs db
```

### Swagger pas accessible?
```bash
# Vérifier le démarrage de l'app
curl http://localhost:8080/api/health

# Ou accéder directement à la doc
http://localhost:8080/api/swagger-ui.html
```

---

**Dernière mise à jour**: 6 mai 2026  
**Temps de vérification estimé**: 2-20 minutes selon la méthode
