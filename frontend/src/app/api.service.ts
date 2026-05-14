import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

export interface RetourProduit {
  id?: number;
  produit: string;
  raison: string;
  etatTraitement: 'EN_ATTENTE' | 'VALIDE' | 'REJETE';
  date: string;
  nomClient?: string;
}

export interface NonConformite {
  id?: number;
  description: string;
  gravite: 'FAIBLE' | 'MOYENNE' | 'ELEVEE' | 'CRITIQUE' | 'MINEURE';
  date: string;
  produitRetour?: string;
}

export interface Utilisateur {
  id?: number;
  nom: string;
  email: string;
  role: 'ADMIN' | 'QUALITE' | 'EMPLOYE' | 'CLIENT';
}

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly apiUrl = environment.apiUrl;

  constructor(private readonly http: HttpClient) {}

  getRetours(): Observable<RetourProduit[]> {
    return this.http.get<RetourProduit[]>(`${this.apiUrl}/retours`);
  }

  addRetour(retour: RetourProduit): Observable<RetourProduit> {
    return this.http.post<RetourProduit>(`${this.apiUrl}/retours/add`, retour);
  }

  validateRetour(id: number): Observable<string> {
    return this.http.put(`${this.apiUrl}/retours/valider/${id}`, null, { responseType: 'text' });
  }

  rejectRetour(id: number): Observable<string> {
    return this.http.put(`${this.apiUrl}/retours/rejeter/${id}`, null, { responseType: 'text' });
  }

  deleteRetour(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/retours/delete/${id}`, { responseType: 'text' });
  }

  getNonConformites(): Observable<NonConformite[]> {
    return this.http.get<NonConformite[]>(`${this.apiUrl}/nonconformites`);
  }

  addNonConformite(nonConformite: NonConformite): Observable<NonConformite> {
    return this.http.post<NonConformite>(`${this.apiUrl}/nonconformites/add`, nonConformite);
  }

  getUtilisateurs(): Observable<Utilisateur[]> {
    return this.http.get<Utilisateur[]>(`${this.apiUrl}/utilisateurs`);
  }

  addUtilisateur(utilisateur: Utilisateur): Observable<Utilisateur> {
    return this.http.post<Utilisateur>(`${this.apiUrl}/utilisateurs/add`, utilisateur);
  }
}
