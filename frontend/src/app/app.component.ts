import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  ApiService,
  HistoriqueRetour,
  NonConformite,
  ProduitStock,
  RetourProduit,
  Role,
  Utilisateur
} from './api.service';

type View = 'dashboard' | 'retours' | 'qualite' | 'stock' | 'users' | 'history';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  retours: RetourProduit[] = [];
  nonConformites: NonConformite[] = [];
  utilisateurs: Utilisateur[] = [];
  historiques: HistoriqueRetour[] = [];
  stocks: ProduitStock[] = [];
  currentUser: Utilisateur | null = null;
  activeView: View = 'dashboard';
  authMode: 'login' | 'signup' = 'login';
  loading = false;
  message = '';

  loginForm = { email: 'admin@gestion-retours.com', motDePasse: 'admin123' };
  signupForm: Utilisateur = { nom: '', email: '', motDePasse: '', role: 'CLIENT' };
  retourForm: RetourProduit = this.newRetour();
  nonConformiteForm: NonConformite = this.newNonConformite();
  utilisateurForm: Utilisateur = this.newUtilisateur();
  historiqueForm: HistoriqueRetour = this.newHistorique();
  stockForm: ProduitStock = this.newStock();

  constructor(private readonly api: ApiService) {}

  ngOnInit(): void {
    const storedUser = localStorage.getItem('gestionRetoursUser');
    if (storedUser) {
      this.currentUser = JSON.parse(storedUser);
      this.refresh();
    }
  }

  get isAdmin(): boolean {
    return this.currentUser?.role === 'ADMIN';
  }

  get isQualite(): boolean {
    return this.currentUser?.role === 'QUALITE' || this.isAdmin;
  }

  get isEmploye(): boolean {
    return this.currentUser?.role === 'EMPLOYE' || this.isAdmin;
  }

  get isClient(): boolean {
    return this.currentUser?.role === 'CLIENT';
  }

  get clients(): Utilisateur[] {
    return this.utilisateurs.filter((user) => user.role === 'CLIENT');
  }

  get employesQualite(): Utilisateur[] {
    return this.utilisateurs.filter((user) => user.role === 'EMPLOYE' || user.role === 'QUALITE' || user.role === 'ADMIN');
  }

  get visibleRetours(): RetourProduit[] {
    if (!this.isClient || !this.currentUser?.id) return this.retours;
    return this.retours.filter((retour) => retour.clientId === this.currentUser?.id);
  }

  get pendingRetours(): RetourProduit[] {
    return this.retours.filter((retour) => retour.etatTraitement === 'EN_ATTENTE');
  }

  get canManageStock(): boolean {
    return this.isAdmin || this.isEmploye;
  }

  canOpen(view: View): boolean {
    if (view === 'users') return this.isAdmin;
    if (view === 'stock') return this.canManageStock;
    if (view === 'qualite') return this.isQualite;
    if (view === 'history') return !this.isClient;
    return true;
  }

  login(): void {
    this.api.login(this.loginForm).subscribe({
      next: (user) => this.setSession(user, 'Connexion reussie.'),
      error: () => this.showMessage('Email ou mot de passe incorrect.')
    });
  }

  signup(): void {
    this.api.signup({ ...this.signupForm, role: 'CLIENT' }).subscribe({
      next: (user) => this.setSession(user, 'Compte client cree.'),
      error: () => this.showMessage("Impossible de creer le compte. Verifie l'email et le mot de passe.")
    });
  }

  logout(): void {
    localStorage.removeItem('gestionRetoursUser');
    this.currentUser = null;
    this.activeView = 'dashboard';
    this.message = '';
  }

  refresh(): void {
    this.loading = true;
    this.message = '';
    let pending = 5;
    const finish = () => {
      pending -= 1;
      if (pending === 0) this.loading = false;
    };

    this.api.getRetours().subscribe({ next: (data) => (this.retours = data), error: () => this.showMessage('Chargement des retours impossible.'), complete: finish });
    this.api.getNonConformites().subscribe({ next: (data) => (this.nonConformites = data), error: () => this.showMessage('Chargement des non-conformites impossible.'), complete: finish });
    this.api.getUtilisateurs().subscribe({ next: (data) => (this.utilisateurs = data), error: () => this.showMessage('Chargement des utilisateurs impossible.'), complete: finish });
    this.api.getHistoriques().subscribe({ next: (data) => (this.historiques = data), error: () => this.showMessage("Chargement de l'historique impossible."), complete: finish });
    this.api.getStocks().subscribe({ next: (data) => (this.stocks = data), error: () => this.showMessage('Chargement du stock impossible.'), complete: finish });
  }

  setView(view: View): void {
    if (this.canOpen(view)) this.activeView = view;
  }

  saveRetour(): void {
    const retour = this.normalizeRetour(this.retourForm);
    const onSuccess = () => {
      this.retourForm = this.newRetour();
      this.showMessage(retour.id ? 'Retour mis a jour.' : 'Retour enregistre.');
      this.refresh();
    };
    const onError = () => this.showMessage("Operation impossible sur le retour.");
    if (retour.id) {
      this.api.updateRetour(retour.id, retour).subscribe({ next: onSuccess, error: onError });
      return;
    }
    this.api.addRetour(retour).subscribe({
      next: () => {
        this.retourForm = this.newRetour();
        this.showMessage('Retour enregistre.');
        this.refresh();
      },
      error: onError
    });
  }

  editRetour(retour: RetourProduit): void {
    this.retourForm = { ...retour, clientId: retour.clientId ?? null };
    this.activeView = 'retours';
  }

  validateRetour(retour: RetourProduit): void {
    if (!retour.id) return;
    this.api.validateRetour(retour.id).subscribe({
      next: () => {
        this.showMessage('Retour valide, stock mis a jour.');
        this.refresh();
      },
      error: () => this.showMessage('Validation impossible.')
    });
  }

  rejectRetour(retour: RetourProduit): void {
    if (!retour.id) return;
    this.api.rejectRetour(retour.id).subscribe({
      next: () => {
        this.showMessage('Retour rejete.');
        this.refresh();
      },
      error: () => this.showMessage('Rejet impossible.')
    });
  }

  deleteRetour(retour: RetourProduit): void {
    if (!retour.id) return;
    this.api.deleteRetour(retour.id).subscribe({ next: () => this.refresh(), error: () => this.showMessage('Suppression impossible.') });
  }

  createNonConformite(): void {
    this.api.addNonConformite(this.nonConformiteForm).subscribe({
      next: () => {
        this.nonConformiteForm = this.newNonConformite();
        this.showMessage('Non-conformite signalee.');
        this.refresh();
      },
      error: () => this.showMessage('Signalement impossible.')
    });
  }

  deleteNonConformite(item: NonConformite): void {
    if (!item.id) return;
    this.api.deleteNonConformite(item.id).subscribe({ next: () => this.refresh(), error: () => this.showMessage('Suppression impossible.') });
  }

  createUtilisateur(): void {
    this.api.addUtilisateur(this.utilisateurForm).subscribe({
      next: () => {
        this.utilisateurForm = this.newUtilisateur();
        this.showMessage('Utilisateur cree.');
        this.refresh();
      },
      error: () => this.showMessage("Creation impossible. Un seul admin est autorise.")
    });
  }

  deleteUtilisateur(user: Utilisateur): void {
    if (!user.id) return;
    this.api.deleteUtilisateur(user.id).subscribe({ next: () => this.refresh(), error: () => this.showMessage('Suppression impossible.') });
  }

  createHistorique(): void {
    this.api.addHistorique(this.historiqueForm).subscribe({
      next: () => {
        this.historiqueForm = this.newHistorique();
        this.showMessage('Action historisee.');
        this.refresh();
      },
      error: () => this.showMessage('Historisation impossible.')
    });
  }

  saveStock(): void {
    const onSuccess = () => {
      this.stockForm = this.newStock();
      this.showMessage('Stock enregistre.');
      this.refresh();
    };
    const onError = () => this.showMessage('Operation stock impossible.');
    if (this.stockForm.id) {
      this.api.updateStock(this.stockForm.id, this.stockForm).subscribe({ next: onSuccess, error: onError });
      return;
    }
    this.api.addStock(this.stockForm).subscribe({
      next: () => {
        this.stockForm = this.newStock();
        this.showMessage('Stock enregistre.');
        this.refresh();
      },
      error: onError
    });
  }

  editStock(stock: ProduitStock): void {
    this.stockForm = { ...stock };
  }

  deleteStock(stock: ProduitStock): void {
    if (!stock.id) return;
    this.api.deleteStock(stock.id).subscribe({ next: () => this.refresh(), error: () => this.showMessage('Suppression stock impossible.') });
  }

  private setSession(user: Utilisateur, message: string): void {
    this.currentUser = user;
    localStorage.setItem('gestionRetoursUser', JSON.stringify(user));
    this.showMessage(message);
    this.refresh();
  }

  private normalizeRetour(retour: RetourProduit): RetourProduit {
    return {
      ...retour,
      quantite: Number(retour.quantite || 1),
      clientId: this.isClient ? this.currentUser?.id ?? null : retour.clientId ? Number(retour.clientId) : null
    };
  }

  private newRetour(): RetourProduit {
    return {
      produit: '',
      raison: '',
      quantite: 1,
      etatTraitement: 'EN_ATTENTE',
      date: new Date().toISOString().slice(0, 10),
      clientId: this.currentUser?.role === 'CLIENT' ? this.currentUser.id ?? null : null
    };
  }

  private newNonConformite(): NonConformite {
    return { description: '', gravite: 'MOYENNE', date: new Date().toISOString().slice(0, 10), retourId: null };
  }

  private newUtilisateur(): Utilisateur {
    return { nom: '', email: '', motDePasse: 'password', role: 'EMPLOYE' };
  }

  private newHistorique(): HistoriqueRetour {
    return { action: '', date: new Date().toISOString().slice(0, 10), retourId: null, employeId: this.currentUser?.id ?? null };
  }

  private newStock(): ProduitStock {
    return { nomProduit: '', quantiteDisponible: 0, quantiteRetournee: 0 };
  }

  private showMessage(message: string): void {
    this.message = message;
  }
}
