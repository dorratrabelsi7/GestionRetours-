import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

export type Role = 'ADMIN' | 'QUALITE' | 'EMPLOYE' | 'CLIENT';

export interface RetourProduit {
  id?: number;
  produit: string;
  raison: string;
  quantite: number;
  etatTraitement: 'EN_ATTENTE' | 'EN_COURS' | 'ACCEPTE' | 'REFUSE';
  date: string;
  nomClient?: string;
  clientId?: number | null;
  stockMisAJour?: boolean;
}

export interface NonConformite {
  id?: number;
  description: string;
  gravite: 'FAIBLE' | 'MOYENNE' | 'CRITIQUE';
  date: string;
  cloturee?: boolean;
  produitRetour?: string;
  retourId?: number | null;
}

export interface Utilisateur {
  id?: number;
  nom: string;
  email: string;
  motDePasse?: string;
  photo?: string;
  role: Role;
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

export interface Notification {
  id?: number;
  titre: string;
  message: string;
  date: string;
  lue: boolean;
  roleDestinataire?: Role;
  destinataireId?: number | null;
  nomDestinataire?: string;
}

export interface LoginRequest {
  email: string;
  motDePasse: string;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  token: string;
  motDePasse: string;
}

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly apiUrl = environment.apiUrl;

  constructor(private readonly http: HttpClient) {}

  login(payload: LoginRequest): Observable<Utilisateur> {
    return this.http.post<Utilisateur>(`${this.apiUrl}/auth/login`, payload);
  }

  signup(payload: Utilisateur): Observable<Utilisateur> {
    return this.http.post<Utilisateur>(`${this.apiUrl}/auth/signup`, payload);
  }

  forgotPassword(payload: ForgotPasswordRequest): Observable<string> {
    return this.http.post(`${this.apiUrl}/auth/forgot-password`, payload, { responseType: 'text' });
  }

  resetPassword(payload: ResetPasswordRequest): Observable<string> {
    return this.http.post(`${this.apiUrl}/auth/reset-password`, payload, { responseType: 'text' });
  }

  getRetours(): Observable<RetourProduit[]> {
    return this.http.get<RetourProduit[]>(`${this.apiUrl}/retours`);
  }

  addRetour(retour: RetourProduit): Observable<RetourProduit> {
    return this.http.post<RetourProduit>(`${this.apiUrl}/retours/add`, retour);
  }

  updateRetour(id: number, retour: RetourProduit): Observable<string> {
    return this.http.put(`${this.apiUrl}/retours/update/${id}`, retour, { responseType: 'text' });
  }

  prendreEnChargeRetour(id: number, employeId?: number | null): Observable<string> {
    const query = employeId ? `?employeId=${employeId}` : '';
    return this.http.put(`${this.apiUrl}/retours/prendre-en-charge/${id}${query}`, null, { responseType: 'text' });
  }

  validateRetour(id: number, employeId?: number | null): Observable<string> {
    const query = employeId ? `?employeId=${employeId}` : '';
    return this.http.put(`${this.apiUrl}/retours/valider/${id}${query}`, null, { responseType: 'text' });
  }

  rejectRetour(id: number, commentaire: string, employeId?: number | null): Observable<string> {
    const params = new URLSearchParams({ commentaire });
    if (employeId) params.set('employeId', String(employeId));
    return this.http.put(`${this.apiUrl}/retours/refuser/${id}?${params.toString()}`, null, { responseType: 'text' });
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

  updateUtilisateur(id: number, utilisateur: Utilisateur): Observable<string> {
    return this.http.put(`${this.apiUrl}/utilisateurs/update/${id}`, utilisateur, { responseType: 'text' });
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

  getNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.apiUrl}/notifications`);
  }

  getNotificationsUtilisateur(userId: number): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.apiUrl}/notifications/utilisateur/${userId}`);
  }

  markNotificationRead(id: number): Observable<string> {
    return this.http.put(`${this.apiUrl}/notifications/lire/${id}`, null, { responseType: 'text' });
  }

  markAllNotificationsRead(userId: number): Observable<string> {
    return this.http.put(`${this.apiUrl}/notifications/lire-toutes/${userId}`, null, { responseType: 'text' });
  }

  deleteNotification(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/notifications/delete/${id}`, { responseType: 'text' });
  }
}
