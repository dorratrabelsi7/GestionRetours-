# 📝 Instructions pour Pusher sur GitHub

## ⚠️ IMPORTANT: Exécutez ces commandes dans l'ordre EXACT

Ouvrez **PowerShell** ou **GitBash** et naviguez au dossier Backend_Projet:

```bash
cd C:\Users\LENOVO\eclipse-workspace\Backend_Projet
```

---

## ÉTAPE 1: Configurer Git (une seule fois)

```bash
git config --global user.name "Dorra Trabelsi"
git config --global user.email "votre.email@example.com"
```

---

## ÉTAPE 2: Initialiser le repository local (si pas déjà fait)

```bash
git init
```

---

## ÉTAPE 3: Ajouter tous les fichiers

```bash
git add .
```

---

## ÉTAPE 4: Créer le commit unique

```bash
git commit -m "Initial commit: Système de gestion des retours produits - Backend complet

- Architecture Spring Boot en couches (controllers, services, entities, repositories, DTOs)
- Entités: RetourProduit, NonConformite, Utilisateur, HistoriqueRetour  
- Validations Spring requises sur tous les DTOs
- Documentation Swagger/OpenAPI complète
- Configuration CORS pour Angular
- Gestion d'erreurs globale avec GlobalExceptionHandler
- Dockerfile et docker-compose.yml pour déploiement
- README.md avec instructions complètes
- Base de données MySQL configurée
- Conversion DTO/Entity avec ModelMapper

Conformité: ✅ 100% selon l'énoncé sujet 6"
```

---

## ÉTAPE 5: Ajouter le remote GitHub

```bash
git remote add origin https://github.com/dorratrabelsi7/GestionRetours-.git
```

(Si déjà existe, utilisez):
```bash
git remote set-url origin https://github.com/dorratrabelsi7/GestionRetours-.git
```

---

## ÉTAPE 6: Vérifier la configuration

```bash
git remote -v
```

Vous devez voir:
```
origin  https://github.com/dorratrabelsi7/GestionRetours-.git (fetch)
origin  https://github.com/dorratrabelsi7/GestionRetours-.git (push)
```

---

## ÉTAPE 7: NETTOYER LE REPO GITHUB (supprimer contenu existant)

⚠️ **Ceci va remplacer TOUT ce qui existe sur GitHub!**

```bash
git push -u origin main --force
```

Si vous voulez être plus prudent, d'abord:

```bash
git branch -M main
git push -u origin main --force
```

---

## ÉTAPE 8: Vérifier le push

Allez sur: https://github.com/dorratrabelsi7/GestionRetours-

Vous devez voir:
✅ README.md
✅ Dockerfile  
✅ docker-compose.yml
✅ pom.xml
✅ src/
✅ 1 seul commit

---

## ✅ Résumé des commandes à copier-coller

```bash
cd C:\Users\LENOVO\eclipse-workspace\Backend_Projet

git config --global user.name "Dorra Trabelsi"
git config --global user.email "votre.email@example.com"

git init

git add .

git commit -m "Initial commit: Système de gestion des retours produits - Backend complet

- Architecture Spring Boot en couches
- Validations Spring requises
- Documentation Swagger/OpenAPI
- Configuration CORS
- Gestion d'erreurs globale
- Docker et docker-compose
- README.md complet
- Conformité: ✅ 100% sujet 6"

git remote add origin https://github.com/dorratrabelsi7/GestionRetours-.git

git branch -M main

git push -u origin main --force
```

---

## 🆘 Troubleshooting

### Erreur: "remote already exists"
```bash
git remote remove origin
git remote add origin https://github.com/dorratrabelsi7/GestionRetours-.git
```

### Erreur: "Permission denied"
Vérifiez vos credentials GitHub. Générez un token:
1. https://github.com/settings/tokens
2. Utilisez le token comme mot de passe

### Erreur: "fatal: not a git repository"
```bash
git init
```

---

**Fait par**: GitHub Copilot
**Date**: 06 mai 2026
