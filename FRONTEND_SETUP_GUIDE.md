# 🎨 FRONTEND ANGULAR - Guide Complet

## 📋 Prérequis

Avant de commencer, installer :

### 1️⃣ Node.js & npm (OBLIGATOIRE)

**Télécharger et installer:**
- Site officiel: https://nodejs.org/
- Prendre la version **LTS (20.x ou 18.x)**

**Vérifier l'installation:**
```powershell
node --version     # v20.10.0 (ou plus)
npm --version      # 10.2.0 (ou plus)
```

### 2️⃣ Angular CLI

```powershell
# Installer globalement
npm install -g @angular/cli

# Vérifier
ng version
```

---

## 🚀 Créer le Projet Frontend

### Étape 1: Créer le projet Angular

```powershell
# Placer-vous au même niveau que Backend_Projet
cd C:\Users\LENOVO\eclipse-workspace

# Créer le projet
ng new frontend-gestion-retours --routing --style=scss

# Répondre aux questions:
# ✅ Do you want to add Angular routing? -> Y
# ✅ Which stylesheet format would you like to use? -> SCSS

# Attendre 5-10 min...
```

### Étape 2: Installer les dépendances supplémentaires

```powershell
cd frontend-gestion-retours

# Dépendances essentielles
npm install --save \
  bootstrap \
  @ng-bootstrap/ng-bootstrap \
  typescript \
  rxjs

# Dépendances développement
npm install --save-dev \
  @angular/cli \
  typescript
```

### Étape 3: Structure du projet

```
frontend-gestion-retours/
├── src/
│   ├── app/
│   │   ├── components/              ← Créer ces dossiers
│   │   │   ├── utilisateurs/
│   │   │   ├── retours/
│   │   │   ├── non-conformites/
│   │   │   ├── historiques/
│   │   │   ├── navbar/
│   │   │   └── shared/
│   │   ├── services/                ← Services API
│   │   │   ├── utilisateur.service.ts
│   │   │   ├── retour.service.ts
│   │   │   ├── non-conformite.service.ts
│   │   │   └── historique.service.ts
│   │   ├── models/                  ← Types TypeScript
│   │   ├── app.component.ts
│   │   ├── app.routing.module.ts
│   │   └── app.module.ts
│   ├── environments/
│   │   ├── environment.ts
│   │   └── environment.prod.ts
│   ├── assets/
│   ├── styles.scss
│   └── index.html
├── angular.json
├── tsconfig.json
├── package.json
└── README.md
```

---

## 📝 Fichiers à Créer

### 1️⃣ Configuration API (environments)

#### `src/environments/environment.ts`

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'  // Backend local
};
```

#### `src/environments/environment.prod.ts`

```typescript
export const environment = {
  production: true,
  apiUrl: 'https://gestion-retours-backend-xxxxx-ew.a.run.app/api'  // Backend GCP
};
```

### 2️⃣ Models TypeScript

#### `src/app/models/utilisateur.model.ts`

```typescript
export enum Role {
  ADMIN = 'ADMIN',
  QUALITE = 'QUALITE',
  EMPLOYE = 'EMPLOYE',
  CLIENT = 'CLIENT'
}

export interface Utilisateur {
  id?: number;
  nom: string;
  email: string;
  role: Role;
}
```

#### `src/app/models/retour.model.ts`

```typescript
export enum EtatTraitement {
  EN_ATTENTE = 'EN_ATTENTE',
  VALIDE = 'VALIDE',
  REJETE = 'REJETE'
}

export interface RetourProduit {
  id?: number;
  produit: string;
  raison: string;
  etatTraitement: EtatTraitement;
  date: Date;
  nomClient: string;
}
```

#### `src/app/models/non-conformite.model.ts`

```typescript
export enum Gravite {
  CRITIQUE = 'CRITIQUE',
  MOYENNE = 'MOYENNE',
  MINEURE = 'MINEURE'
}

export interface NonConformite {
  id?: number;
  description: string;
  gravite: Gravite;
  date: Date;
  produitRetour: string;
}
```

#### `src/app/models/historique.model.ts`

```typescript
export interface HistoriqueRetour {
  id?: number;
  action: string;
  date: Date;
  produitRetour: string;
  nomEmploye: string;
}
```

### 3️⃣ Services API

#### `src/app/services/utilisateur.service.ts`

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Utilisateur, Role } from '../models/utilisateur.model';

@Injectable({
  providedIn: 'root'
})
export class UtilisateurService {
  private apiUrl = `${environment.apiUrl}/utilisateurs`;

  constructor(private http: HttpClient) {}

  // Récupérer tous les utilisateurs
  getAll(): Observable<Utilisateur[]> {
    return this.http.get<Utilisateur[]>(this.apiUrl);
  }

  // Récupérer par ID
  getById(id: number): Observable<Utilisateur> {
    return this.http.get<Utilisateur>(`${this.apiUrl}/${id}`);
  }

  // Récupérer par rôle
  getByRole(role: Role): Observable<Utilisateur[]> {
    return this.http.get<Utilisateur[]>(`${this.apiUrl}/role/${role}`);
  }

  // Créer un utilisateur
  create(utilisateur: Utilisateur): Observable<Utilisateur> {
    return this.http.post<Utilisateur>(`${this.apiUrl}/add`, utilisateur);
  }

  // Mettre à jour
  update(id: number, utilisateur: Utilisateur): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, utilisateur);
  }

  // Supprimer
  delete(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}
```

#### `src/app/services/retour.service.ts`

```typescript
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { RetourProduit, EtatTraitement } from '../models/retour.model';

@Injectable({
  providedIn: 'root'
})
export class RetourService {
  private apiUrl = `${environment.apiUrl}/retours`;

  constructor(private http: HttpClient) {}

  // Récupérer tous les retours
  getAll(): Observable<RetourProduit[]> {
    return this.http.get<RetourProduit[]>(this.apiUrl);
  }

  // Récupérer par ID
  getById(id: number): Observable<RetourProduit> {
    return this.http.get<RetourProduit>(`${this.apiUrl}/${id}`);
  }

  // Par état
  getByEtat(etat: EtatTraitement): Observable<RetourProduit[]> {
    return this.http.get<RetourProduit[]>(`${this.apiUrl}/etat/${etat}`);
  }

  // Par client
  getByClient(clientId: number): Observable<RetourProduit[]> {
    return this.http.get<RetourProduit[]>(`${this.apiUrl}/client/${clientId}`);
  }

  // Par période
  getByPeriode(dateDebut: Date, dateFin: Date): Observable<RetourProduit[]> {
    let params = new HttpParams()
      .set('dateDebut', dateDebut.toISOString().split('T')[0])
      .set('dateFin', dateFin.toISOString().split('T')[0]);
    return this.http.get<RetourProduit[]>(`${this.apiUrl}/periode`, { params });
  }

  // Créer
  create(retour: RetourProduit): Observable<RetourProduit> {
    return this.http.post<RetourProduit>(`${this.apiUrl}/add`, retour);
  }

  // Valider
  valider(id: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/valider/${id}`, {});
  }

  // Rejeter
  rejeter(id: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/rejeter/${id}`, {});
  }

  // Mettre à jour
  update(id: number, retour: RetourProduit): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, retour);
  }

  // Supprimer
  delete(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}
```

#### `src/app/services/non-conformite.service.ts`

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { NonConformite, Gravite } from '../models/non-conformite.model';

@Injectable({
  providedIn: 'root'
})
export class NonConformiteService {
  private apiUrl = `${environment.apiUrl}/non-conformites`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<NonConformite[]> {
    return this.http.get<NonConformite[]>(this.apiUrl);
  }

  getById(id: number): Observable<NonConformite> {
    return this.http.get<NonConformite>(`${this.apiUrl}/${id}`);
  }

  getByGravite(gravite: Gravite): Observable<NonConformite[]> {
    return this.http.get<NonConformite[]>(`${this.apiUrl}/gravite/${gravite}`);
  }

  create(nonConformite: NonConformite): Observable<NonConformite> {
    return this.http.post<NonConformite>(`${this.apiUrl}/add`, nonConformite);
  }

  update(id: number, nonConformite: NonConformite): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, nonConformite);
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}
```

#### `src/app/services/historique.service.ts`

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { HistoriqueRetour } from '../models/historique.model';

@Injectable({
  providedIn: 'root'
})
export class HistoriqueService {
  private apiUrl = `${environment.apiUrl}/historiques`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<HistoriqueRetour[]> {
    return this.http.get<HistoriqueRetour[]>(this.apiUrl);
  }

  getByRetour(retourId: number): Observable<HistoriqueRetour[]> {
    return this.http.get<HistoriqueRetour[]>(`${this.apiUrl}/retour/${retourId}`);
  }

  create(historique: HistoriqueRetour): Observable<HistoriqueRetour> {
    return this.http.post<HistoriqueRetour>(`${this.apiUrl}/add`, historique);
  }
}
```

### 4️⃣ Module Principal avec HttpClient

#### `src/app/app.module.ts`

```typescript
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

@NgModule({
  declarations: [
    AppComponent,
    // Ajouter les composants ici
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    NgbModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
```

---

## ✅ Démarrer le Frontend

```powershell
# Se placer dans le dossier frontend
cd C:\Users\LENOVO\eclipse-workspace\frontend-gestion-retours

# Démarrer le serveur de développement
ng serve

# Ou avec rechargement automatique
ng serve --open

# L'application sera accessible sur:
# http://localhost:4200
```

---

## 🔗 Connecter Backend & Frontend

### Backend doit être en cours d'exécution

```powershell
# Terminal 1: Backend
cd C:\Users\LENOVO\eclipse-workspace\Backend_Projet
docker-compose up -d

# Vérifier:
# http://localhost:8080/api/swagger-ui.html
```

### Frontend en cours d'exécution

```powershell
# Terminal 2: Frontend
cd C:\Users\LENOVO\eclipse-workspace\frontend-gestion-retours
ng serve
```

### Test de connexion

```typescript
// Dans un composant Angular
import { UtilisateurService } from './services/utilisateur.service';

constructor(private utilisateurService: UtilisateurService) {}

ngOnInit() {
  this.utilisateurService.getAll().subscribe(
    (data) => console.log('Utilisateurs:', data),
    (error) => console.error('Erreur:', error)
  );
}
```

---

## 📦 Structure Recommandée pour les Composants

### Composant Utilisateurs

#### `src/app/components/utilisateurs/utilisateurs-list.component.ts`

```typescript
import { Component, OnInit } from '@angular/core';
import { UtilisateurService } from '../../services/utilisateur.service';
import { Utilisateur } from '../../models/utilisateur.model';

@Component({
  selector: 'app-utilisateurs-list',
  templateUrl: './utilisateurs-list.component.html',
  styleUrls: ['./utilisateurs-list.component.scss']
})
export class UtilisateursListComponent implements OnInit {
  utilisateurs: Utilisateur[] = [];
  loading = false;
  error: string | null = null;

  constructor(private service: UtilisateurService) {}

  ngOnInit() {
    this.loadUtilisateurs();
  }

  loadUtilisateurs() {
    this.loading = true;
    this.service.getAll().subscribe(
      (data) => {
        this.utilisateurs = data;
        this.loading = false;
      },
      (error) => {
        this.error = 'Erreur lors du chargement des utilisateurs';
        this.loading = false;
      }
    );
  }

  delete(id: number | undefined) {
    if (id && confirm('Êtes-vous sûr?')) {
      this.service.delete(id).subscribe(
        () => this.loadUtilisateurs(),
        (error) => this.error = 'Erreur lors de la suppression'
      );
    }
  }
}
```

#### `src/app/components/utilisateurs/utilisateurs-list.component.html`

```html
<div class="container mt-4">
  <h1>Gestion des Utilisateurs</h1>
  
  <button class="btn btn-primary mb-3" routerLink="/utilisateurs/new">
    Ajouter un utilisateur
  </button>

  <div *ngIf="loading" class="alert alert-info">
    Chargement...
  </div>

  <div *ngIf="error" class="alert alert-danger">
    {{ error }}
  </div>

  <table *ngIf="utilisateurs.length > 0" class="table table-striped">
    <thead>
      <tr>
        <th>ID</th>
        <th>Nom</th>
        <th>Email</th>
        <th>Rôle</th>
        <th>Actions</th>
      </tr>
    </thead>
    <tbody>
      <tr *ngFor="let u of utilisateurs">
        <td>{{ u.id }}</td>
        <td>{{ u.nom }}</td>
        <td>{{ u.email }}</td>
        <td>{{ u.role }}</td>
        <td>
          <button class="btn btn-sm btn-warning" [routerLink]="['/utilisateurs', u.id]">
            Éditer
          </button>
          <button class="btn btn-sm btn-danger" (click)="delete(u.id)">
            Supprimer
          </button>
        </td>
      </tr>
    </tbody>
  </table>

  <div *ngIf="utilisateurs.length === 0 && !loading" class="alert alert-warning">
    Aucun utilisateur trouvé.
  </div>
</div>
```

---

## 🎯 Checklist de Configuration

- [ ] Node.js installé (`node --version`)
- [ ] npm installé (`npm --version`)
- [ ] Angular CLI installé (`ng version`)
- [ ] Projet Angular créé
- [ ] Dépendances installées
- [ ] Models créés
- [ ] Services créés
- [ ] Variables d'environnement configurées
- [ ] Backend démarré (Docker)
- [ ] Frontend démarré (`ng serve`)
- [ ] Test de connexion API OK

---

## 🔄 Git - Ajouter le Frontend au Repo

```powershell
# Dans le dossier parent (eclipse-workspace)
cd C:\Users\LENOVO\eclipse-workspace

# Ajouter le frontend comme sous-module (optionnel)
git submodule add https://github.com/votre-username/frontend-gestion-retours.git

# Ou créer un repo séparé:
cd frontend-gestion-retours
git init
git add .
git commit -m "initial: Angular frontend project setup"
git remote add origin https://github.com/votre-username/frontend-gestion-retours.git
git push -u origin main
```

---

## 📚 Ressources

| Ressource | Lien |
|-----------|------|
| Angular Documentation | https://angular.io/docs |
| Angular CLI | https://angular.io/cli |
| Bootstrap 5 | https://getbootstrap.com |
| ng-bootstrap | https://ng-bootstrap.github.io |
| TypeScript Handbook | https://www.typescriptlang.org/docs |
| RxJS | https://rxjs.dev |

---

## ⚡ Commandes Essentielles Angular

```powershell
# Générer un composant
ng generate component components/utilisateurs/utilisateurs-list

# Générer un service
ng generate service services/utilisateur

# Build pour production
ng build --configuration production

# Tests unitaires
ng test

# Linter
ng lint

# Format code
ng format
```

---

**Prochaine étape:** Créer les composants un par un!  
**Questions?** Consultez la documentation officielle Angular.
