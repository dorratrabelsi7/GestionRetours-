# 📚 Guide Détaillé de l'API REST

## 🌐 Base URL
```
http://localhost:8080/api
```

## 📚 Documentation Interactive
Accédez à Swagger UI pour tester les endpoints:
```
http://localhost:8080/api/swagger-ui.html
```

---

## 👥 Utilisateurs

### 1️⃣ Récupérer tous les utilisateurs
```
GET /utilisateurs
```
**Réponse (200 OK)**:
```json
[
  {
    "id": 1,
    "nom": "Jean Dupont",
    "email": "jean@example.com",
    "role": "QUALITE",
    "telephone": "+216 98 123 456",
    "adresse": "123 Rue de Paris, Tunis",
    "actif": true
  }
]
```

### 2️⃣ Récupérer un utilisateur par ID
```
GET /utilisateurs/{id}
```
**Paramètres**:
- `id` (path): ID de l'utilisateur

**Réponse (200 OK)**:
```json
{
  "id": 1,
  "nom": "Jean Dupont",
  "email": "jean@example.com",
  "role": "QUALITE",
  "telephone": "+216 98 123 456",
  "adresse": "123 Rue de Paris, Tunis",
  "actif": true
}
```

### 3️⃣ Récupérer les utilisateurs par rôle
```
GET /utilisateurs/role/{role}
```
**Paramètres**:
- `role` (path): ADMIN, QUALITE, EMPLOYE, CLIENT

**Réponse (200 OK)**:
```json
[
  {
    "id": 1,
    "nom": "Jean Dupont",
    "email": "jean@example.com",
    "role": "QUALITE",
    "actif": true
  }
]
```

### 4️⃣ Créer un utilisateur
```
POST /utilisateurs
```
**Body**:
```json
{
  "nom": "Marie Martin",
  "email": "marie@example.com",
  "role": "EMPLOYE",
  "telephone": "+216 92 765 432",
  "adresse": "456 Avenue de Marseille, Tunis",
  "actif": true
}
```
**Réponse (201 CREATED)**:
```json
{
  "id": 2,
  "nom": "Marie Martin",
  "email": "marie@example.com",
  "role": "EMPLOYE",
  "actif": true
}
```

### 5️⃣ Modifier un utilisateur
```
PUT /utilisateurs/{id}
```
**Paramètres**:
- `id` (path): ID de l'utilisateur

**Body**: (même format que creation)

**Réponse (200 OK)**: Utilisateur modifié

### 6️⃣ Désactiver un utilisateur
```
PUT /utilisateurs/{id}/desactiver
```
**Réponse (200 OK)**:
```
"Utilisateur désactivé avec succès"
```

### 7️⃣ Activer un utilisateur
```
PUT /utilisateurs/{id}/activer
```
**Réponse (200 OK)**:
```
"Utilisateur activé avec succès"
```

### 8️⃣ Supprimer un utilisateur
```
DELETE /utilisateurs/{id}
```
**Réponse (200 OK)**:
```
"Utilisateur supprimé avec succès"
```

---

## 📦 Retours Produits

### 1️⃣ Récupérer tous les retours
```
GET /retours
```
**Réponse (200 OK)**:
```json
[
  {
    "id": 1,
    "produit": "Laptop Dell XPS",
    "raison": "Écran cassé lors de la livraison",
    "etatTraitement": "EN_ATTENTE",
    "date": "2026-05-06T10:30:00",
    "quantite": 1,
    "notes": "Produit emballé correctement",
    "clientId": 1,
    "nomClient": "Jean Dupont"
  }
]
```

### 2️⃣ Récupérer un retour par ID
```
GET /retours/{id}
```
**Paramètres**:
- `id` (path): ID du retour

### 3️⃣ Récupérer les retours par état
```
GET /retours/etat/{etat}
```
**Paramètres**:
- `etat` (path): EN_ATTENTE, EN_COURS, VALIDE, REJETE

### 4️⃣ Récupérer les retours d'un client
```
GET /retours/client/{clientId}
```
**Paramètres**:
- `clientId` (path): ID du client

### 5️⃣ Récupérer les retours par période
```
GET /retours/periode?dateDebut=2026-05-01T00:00:00&dateFin=2026-05-31T23:59:59
```
**Paramètres**:
- `dateDebut` (query): Date au format ISO 8601
- `dateFin` (query): Date au format ISO 8601

### 6️⃣ Créer un retour
```
POST /retours
```
**Body**:
```json
{
  "produit": "Laptop Dell XPS",
  "raison": "Écran cassé lors de la livraison",
  "etatTraitement": "EN_ATTENTE",
  "quantite": 1,
  "notes": "Produit emballé correctement",
  "clientId": 1
}
```
**Réponse (201 CREATED)**:
```json
{
  "id": 1,
  "produit": "Laptop Dell XPS",
  "raison": "Écran cassé lors de la livraison",
  "etatTraitement": "EN_ATTENTE",
  "date": "2026-05-06T10:30:00",
  "quantite": 1,
  "notes": "Produit emballé correctement",
  "clientId": 1
}
```

### 7️⃣ Modifier un retour
```
PUT /retours/{id}
```
**Body**: (même format que creation)

### 8️⃣ Valider un retour
```
PUT /retours/{id}/valider
```
**Réponse (200 OK)**:
```json
{
  "id": 1,
  "etatTraitement": "VALIDE",
  ...
}
```

### 9️⃣ Rejeter un retour
```
PUT /retours/{id}/rejeter
```
**Réponse (200 OK)**:
```json
{
  "id": 1,
  "etatTraitement": "REJETE",
  ...
}
```

### 🔟 Marquer un retour en cours
```
PUT /retours/{id}/en-cours
```
**Réponse (200 OK)**:
```json
{
  "id": 1,
  "etatTraitement": "EN_COURS",
  ...
}
```

### 1️⃣1️⃣ Supprimer un retour
```
DELETE /retours/{id}
```
**Réponse (200 OK)**:
```
"Retour supprimé avec succès"
```

---

## ⚠️ Non-Conformités

### 1️⃣ Récupérer toutes les non-conformités
```
GET /non-conformites
```

### 2️⃣ Récupérer une non-conformité par ID
```
GET /non-conformites/{id}
```

### 3️⃣ Récupérer les non-conformités par gravité
```
GET /non-conformites/gravite/{gravite}
```
**Paramètres**:
- `gravite` (path): FAIBLE, MOYENNE, ELEVEE

### 4️⃣ Récupérer les non-conformités d'un retour
```
GET /non-conformites/retour/{retourId}
```

### 5️⃣ Signaler une non-conformité
```
POST /non-conformites
```
**Body**:
```json
{
  "description": "Défaut de fabrication sur l'écran LCD",
  "gravite": "MOYENNE",
  "reference": "NC-2026-001",
  "retourId": 1
}
```
**Réponse (201 CREATED)**:
```json
{
  "id": 1,
  "description": "Défaut de fabrication sur l'écran LCD",
  "gravite": "MOYENNE",
  "date": "2026-05-06T10:30:00",
  "reference": "NC-2026-001",
  "retourId": 1
}
```

### 6️⃣ Modifier une non-conformité
```
PUT /non-conformites/{id}
```

### 7️⃣ Supprimer une non-conformité
```
DELETE /non-conformites/{id}
```

---

## 📋 Historique des Retours

### 1️⃣ Récupérer tous les historiques
```
GET /historique-retours
```

### 2️⃣ Récupérer un historique par ID
```
GET /historique-retours/{id}
```

### 3️⃣ Récupérer l'historique d'un retour
```
GET /historique-retours/retour/{retourId}
```

### 4️⃣ Récupérer l'historique d'un employé
```
GET /historique-retours/employe/{employeId}
```

### 5️⃣ Récupérer l'historique par période
```
GET /historique-retours/periode?dateDebut=2026-05-01T00:00:00&dateFin=2026-05-31T23:59:59
```

### 6️⃣ Enregistrer une action
```
POST /historique-retours
```
**Body**:
```json
{
  "action": "Retour reçu et inspecté",
  "retourId": 1,
  "employeId": 1
}
```
**Réponse (201 CREATED)**:
```json
{
  "id": 1,
  "action": "Retour reçu et inspecté",
  "date": "2026-05-06T10:30:00",
  "retourId": 1,
  "employeId": 1
}
```

### 7️⃣ Enregistrer avec changement de statut
```
POST /historique-retours/avec-statuts?statutAncien=EN_ATTENTE&statutNouveau=EN_COURS
```

### 8️⃣ Supprimer un historique
```
DELETE /historique-retours/{id}
```

---

## ✅ Validation des Données

Tous les endpoints POST/PUT valident les données. En cas d'erreur:

**Réponse (400 BAD REQUEST)**:
```json
{
  "status": 400,
  "message": "Erreur de validation",
  "errors": {
    "produit": "Le produit doit avoir entre 3 et 100 caractères",
    "email": "L'email doit être valide"
  }
}
```

---

## 🔍 Gestion des Erreurs

### 404 Not Found
```json
{
  "status": 404,
  "message": "Retour non trouvé avec l'ID: 999"
}
```

### 409 Conflict
```json
{
  "status": 409,
  "message": "Cet email existe déjà"
}
```

---

## 📊 Statistiques

### Compter les retours par état
```
GET /retours/statistiques/etat/{etat}
```
**Réponse**: `5`

### Compter les non-conformités par gravité
```
GET /non-conformites/statistiques/gravite/{gravite}
```
**Réponse**: `3`

---

## 🔄 Workflow Complet d'un Retour

1. **Créer un retour**: `POST /retours`
2. **Signaler des non-conformités**: `POST /non-conformites`
3. **Enregistrer les actions**: `POST /historique-retours`
4. **Mettre à jour l'état**: `PUT /retours/{id}/en-cours`
5. **Valider ou rejeter**: `PUT /retours/{id}/valider` ou `PUT /retours/{id}/rejeter`
6. **Consulter l'historique**: `GET /historique-retours/retour/{id}`

---

## 🛡️ Sécurité

- ✅ Validation des données obligatoire
- ✅ Gestion des erreurs globale
- ✅ CORS configuré
- ✅ Timestamps pour audit
- ✅ Rôles utilisateurs

---

**Version**: 1.0.0  
**Dernière mise à jour**: 09 mai 2026
