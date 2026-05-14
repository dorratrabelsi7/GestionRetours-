import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

export interface RetourProduit {
  id?: number;
  produit: string;
  raison: string;
  quantite: number;
  etatTraitement: 'EN_ATTENTE' | 'VALIDE' | 'REJETE';
  date: string;
  nomClient?: string;
  clientId?: number | null;
  stockMisAJour?: boolean;
}

export interface NonConformite {
  id?: number;
  description: string;
  gravite: 'FAIBLE' | 'MOYENNE' | 'ELEVEE' | 'CRITIQUE' | 'MINEURE';
  date: string;
  produitRetour?: string;
  retourId?: number | null;
}

export interface Utilisateur {
  id?: number;
  nom: string;
  email: string;
  role: 'ADMIN' | 'QUALITE' | 'EMPLOYE' | 'CLIENT';
}

export interface HistoriqueRetour {
  id?: number;
  action: string;
  date: string;
  produitRetour?: string;
  nomEmploye?: string;
  retourId?: number | null;
  employeId?: number | null;
}

export interface ProduitStock {
  id?: number;
  nomProduit: string;
  quantiteDisponible: number;
  quantiteRetournee: number;
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

  updateRetour(id: number, retour: RetourProduit): Observable<string> {
    return this.http.put(`${this.apiUrl}/retours/update/${id}`, retour, { responseType: 'text' });
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

  deleteNonConformite(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/nonconformites/delete/${id}`, { responseType: 'text' });
  }

  getUtilisateurs(): Observable<Utilisateur[]> {
    return this.http.get<Utilisateur[]>(`${this.apiUrl}/utilisateurs`);
  }

  addUtilisateur(utilisateur: Utilisateur): Observable<Utilisateur> {
    return this.http.post<Utilisateur>(`${this.apiUrl}/utilisateurs/add`, utilisateur);
  }

  deleteUtilisateur(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/utilisateurs/delete/${id}`, { responseType: 'text' });
  }

  getHistoriques(): Observable<HistoriqueRetour[]> {
    return this.http.get<HistoriqueRetour[]>(`${this.apiUrl}/historiques`);
  }

  addHistorique(historique: HistoriqueRetour): Observable<HistoriqueRetour> {
    return this.http.post<HistoriqueRetour>(`${this.apiUrl}/historiques/add`, historique);
  }

  getStocks(): Observable<ProduitStock[]> {
    return this.http.get<ProduitStock[]>(`${this.apiUrl}/stocks`);
  }

  addStock(stock: ProduitStock): Observable<ProduitStock> {
    return this.http.post<ProduitStock>(`${this.apiUrl}/stocks/add`, stock);
  }

  updateStock(id: number, stock: ProduitStock): Observable<string> {
    return this.http.put(`${this.apiUrl}/stocks/update/${id}`, stock, { responseType: 'text' });
  }

  deleteStock(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/stocks/delete/${id}`, { responseType: 'text' });
  }
}
