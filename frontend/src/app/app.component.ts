import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  ApiService,
  HistoriqueRetour,
  NonConformite,
  Notification,
  ProduitStock,
  RetourProduit,
  Role,
  Utilisateur
} from './api.service';

type View = 'dashboard' | 'retours' | 'qualite' | 'nonconformites' | 'stock' | 'users' | 'history' | 'notifications' | 'compte';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit, OnDestroy {
  retours: RetourProduit[] = [];
  nonConformites: NonConformite[] = [];
  utilisateurs: Utilisateur[] = [];
  historiques: HistoriqueRetour[] = [];
  stocks: ProduitStock[] = [];
  notifications: Notification[] = [];
  currentUser: Utilisateur | null = null;
  activeView: View = 'dashboard';
  authMode: 'login' | 'signup' = 'login';
  loading = false;
  message = '';
  showLoginPassword = false;
  showSignupPassword = false;
  showAccountPassword = false;
  private refreshTimer?: number;

  loginForm = { email: 'admin@gestion-retours.com', motDePasse: 'admin123' };
  signupForm: Utilisateur = { nom: '', email: '', motDePasse: '', role: 'CLIENT' };
  accountForm: Utilisateur = this.newUtilisateur();
  retourForm: RetourProduit = this.newRetour();
  nonConformiteForm: NonConformite = this.newNonConformite();
  utilisateurForm: Utilisateur = this.newUtilisateur();
  historiqueForm: HistoriqueRetour = this.newHistorique();
  stockForm: ProduitStock = this.newStock();
  refusCommentaire = '';
  etatFilter: 'TOUS' | RetourProduit['etatTraitement'] = 'TOUS';
  graviteFilter: 'TOUTES' | NonConformite['gravite'] = 'TOUTES';
  roleFilter: 'TOUS' | Role = 'TOUS';
  retourSearch = '';

  constructor(private readonly api: ApiService) {}

  ngOnInit(): void {
    const storedUser = localStorage.getItem('gestionRetoursUser');
    if (storedUser) {
      const user = JSON.parse(storedUser) as Utilisateur;
      this.currentUser = user;
      this.accountForm = this.createAccountForm(user);
      this.refresh();
    }
    this.refreshTimer = window.setInterval(() => {
      if (this.currentUser) this.refresh(false);
    }, 30000);
  }

  ngOnDestroy(): void {
    if (this.refreshTimer) window.clearInterval(this.refreshTimer);
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
    const retours = this.isClient && this.currentUser?.id
      ? this.retours.filter((retour) => retour.clientId === this.currentUser?.id)
      : this.retours;
    return retours.filter((retour) => {
      const matchEtat = this.etatFilter === 'TOUS' || retour.etatTraitement === this.etatFilter;
      const search = this.retourSearch.trim().toLowerCase();
      const matchSearch = !search
        || retour.produit.toLowerCase().includes(search)
        || retour.raison.toLowerCase().includes(search)
        || (retour.nomClient || '').toLowerCase().includes(search);
      return matchEtat && matchSearch;
    });
  }

  get pendingRetours(): RetourProduit[] {
    return this.retours.filter((retour) => retour.etatTraitement === 'EN_ATTENTE');
  }

  get retoursEnCours(): RetourProduit[] {
    return this.retours.filter((retour) => retour.etatTraitement === 'EN_COURS');
  }

  get acceptedRetours(): RetourProduit[] {
    return this.retours.filter((retour) => retour.etatTraitement === 'ACCEPTE');
  }

  get refusedRetours(): RetourProduit[] {
    return this.retours.filter((retour) => retour.etatTraitement === 'REFUSE');
  }

  get criticalNonConformites(): NonConformite[] {
    return this.nonConformites.filter((item) => item.gravite === 'CRITIQUE' && !item.cloturee);
  }

  get visibleNonConformites(): NonConformite[] {
    return this.nonConformites.filter((item) => {
      const matchGravite = this.graviteFilter === 'TOUTES' || item.gravite === this.graviteFilter;
      return matchGravite;
    });
  }

  get visibleUsers(): Utilisateur[] {
    return this.utilisateurs.filter((user) => {
      const matchRole = this.roleFilter === 'TOUS' || user.role === this.roleFilter;
      return matchRole;
    });
  }

  get visibleHistoriques(): HistoriqueRetour[] {
    const source = !this.isClient || !this.currentUser?.id ? this.historiques : this.historiques.filter((item) => {
      const retourIds = this.retours
        .filter((retour) => retour.clientId === this.currentUser?.id)
        .map((retour) => retour.id);
      return item.retourId && retourIds.includes(item.retourId);
    });
    return source;
  }

  get visibleStocks(): ProduitStock[] {
    return this.stocks;
  }

  get filteredNotifications(): Notification[] {
    return this.visibleNotifications;
  }

  get unreadNotifications(): number {
    return this.visibleNotifications.filter((notification) => !notification.lue).length;
  }

  get visibleNotifications(): Notification[] {
    if (this.isAdmin) return this.notifications;
    if (!this.currentUser?.id) return [];
    return this.notifications.filter((notification) => notification.destinataireId === this.currentUser?.id);
  }

  get canManageStock(): boolean {
    return this.isAdmin;
  }

  canOpen(view: View): boolean {
    if (view === 'users') return this.isAdmin;
    if (view === 'stock') return this.canManageStock;
    if (view === 'qualite') return this.isQualite;
    if (view === 'nonconformites') return !this.isClient;
    if (view === 'history') return true;
    return true;
  }

  roleLabel(): string {
    if (this.isAdmin) return 'Administration globale';
    if (this.currentUser?.role === 'QUALITE') return 'Service qualite';
    if (this.currentUser?.role === 'EMPLOYE') return 'Traitement des retours';
    return 'Espace client';
  }

  viewTitle(): string {
    const titles: Record<View, string> = {
      dashboard: 'Tableau de bord',
      retours: this.isClient ? 'Mes retours' : 'Retours produits',
      qualite: 'Validation qualite',
      nonconformites: 'Non-conformites',
      stock: 'Stock produits',
      users: 'Utilisateurs',
      history: this.isClient ? 'Historique de mes retours' : 'Historique',
      notifications: 'Notifications',
      compte: 'Mon compte'
    };
    return titles[this.activeView];
  }

  roleDescription(): string {
    if (this.isAdmin) return 'Vue complete sur les utilisateurs, retours, stock et alertes systeme.';
    if (this.currentUser?.role === 'QUALITE') return 'Validation des retours en cours et supervision des non-conformites.';
    if (this.currentUser?.role === 'EMPLOYE') return 'Prise en charge des retours et signalement des non-conformites.';
    return 'Declaration, suivi et notifications de vos retours produits.';
  }

  statusClass(etat: RetourProduit['etatTraitement']): string {
    if (etat === 'ACCEPTE') return 'success';
    if (etat === 'REFUSE') return 'danger';
    if (etat === 'EN_COURS') return 'warning';
    return 'info';
  }

  graviteClass(gravite: NonConformite['gravite']): string {
    if (gravite === 'CRITIQUE') return 'danger';
    if (gravite === 'MOYENNE') return 'warning';
    return 'success';
  }

  avatarUrl(user?: Utilisateur | null): string {
    if (user?.photo) return user.photo;
    const name = encodeURIComponent(user?.nom || 'Utilisateur');
    return `https://ui-avatars.com/api/?name=${name}&background=DBEAFE&color=2563EB&bold=true&size=96`;
  }

  productImage(produit?: string): string {
    const key = (produit || '').toLowerCase();
    if (key.includes('phone') || key.includes('tel')) return 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=160&q=70';
    if (key.includes('laptop') || key.includes('pc') || key.includes('ordinateur')) return 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=160&q=70';
    if (key.includes('casque') || key.includes('audio')) return 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=160&q=70';
    return 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=160&q=70';
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

  forgotPassword(): void {
    if (!this.loginForm.email) {
      this.showMessage("Saisis ton email pour recevoir le lien de reinitialisation.");
      return;
    }
    this.showMessage("Si ce compte existe, un lien de reinitialisation sera envoye par email apres configuration SMTP.");
  }

  logout(): void {
    localStorage.removeItem('gestionRetoursUser');
    this.currentUser = null;
    this.activeView = 'dashboard';
    this.message = '';
  }

  refresh(showLoader = true): void {
    if (showLoader) {
      this.loading = true;
      this.message = '';
    }
    let pending = 6;
    const finish = () => {
      pending -= 1;
      if (pending === 0 && showLoader) this.loading = false;
    };

    this.api.getRetours().subscribe({ next: (data) => (this.retours = data), error: () => this.showMessage('Chargement des retours impossible.'), complete: finish });
    this.api.getNonConformites().subscribe({ next: (data) => (this.nonConformites = data), error: () => this.showMessage('Chargement des non-conformites impossible.'), complete: finish });
    this.api.getUtilisateurs().subscribe({ next: (data) => (this.utilisateurs = data), error: () => this.showMessage('Chargement des utilisateurs impossible.'), complete: finish });
    this.api.getHistoriques().subscribe({ next: (data) => (this.historiques = data), error: () => this.showMessage("Chargement de l'historique impossible."), complete: finish });
    this.api.getStocks().subscribe({ next: (data) => (this.stocks = data), error: () => this.showMessage('Chargement du stock impossible.'), complete: finish });
    this.api.getNotifications().subscribe({ next: (data) => (this.notifications = data), error: () => this.showMessage('Chargement des notifications impossible.'), complete: finish });
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
    this.api.validateRetour(retour.id, this.currentUser?.id).subscribe({
      next: () => {
        this.showMessage('Retour accepte, stock mis a jour.');
        this.refresh();
      },
      error: () => this.showMessage('Validation impossible.')
    });
  }

  prendreEnCharge(retour: RetourProduit): void {
    if (!retour.id) return;
    this.api.prendreEnChargeRetour(retour.id, this.currentUser?.id).subscribe({
      next: () => {
        this.showMessage('Retour pris en charge.');
        this.refresh();
      },
      error: () => this.showMessage('Prise en charge impossible.')
    });
  }

  rejectRetour(retour: RetourProduit): void {
    if (!retour.id) return;
    const commentaire = this.refusCommentaire.trim();
    if (!commentaire) {
      this.showMessage('Commentaire obligatoire pour refuser un retour.');
      return;
    }
    this.api.rejectRetour(retour.id, commentaire, this.currentUser?.id).subscribe({
      next: () => {
        this.refusCommentaire = '';
        this.showMessage('Retour refuse.');
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

  updateAccount(): void {
    if (!this.currentUser?.id) return;
    const payload: Utilisateur = {
      ...this.currentUser,
      nom: this.accountForm.nom,
      email: this.currentUser.email,
      role: this.currentUser.role,
      motDePasse: this.accountForm.motDePasse || this.currentUser.motDePasse,
      photo: this.accountForm.photo || this.currentUser.photo
    };
    this.api.updateUtilisateur(this.currentUser.id, payload).subscribe({
      next: () => {
        this.currentUser = { ...payload, motDePasse: undefined };
        this.accountForm = { ...this.currentUser, motDePasse: '' };
        localStorage.setItem('gestionRetoursUser', JSON.stringify(this.currentUser));
        this.showMessage('Compte mis a jour.');
      },
      error: () => this.showMessage('Modification du compte impossible.')
    });
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

  markNotificationRead(notification: Notification): void {
    if (!notification.id) return;
    this.api.markNotificationRead(notification.id).subscribe({ next: () => this.refresh(), error: () => this.showMessage('Lecture notification impossible.') });
  }

  markAllNotificationsRead(): void {
    if (!this.currentUser?.id) return;
    this.api.markAllNotificationsRead(this.currentUser.id).subscribe({ next: () => this.refresh(), error: () => this.showMessage('Operation notifications impossible.') });
  }

  deleteNotification(notification: Notification): void {
    if (!notification.id) return;
    this.api.deleteNotification(notification.id).subscribe({ next: () => this.refresh(), error: () => this.showMessage('Suppression notification impossible.') });
  }

  onPhotoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = () => {
      this.accountForm.photo = String(reader.result);
    };
    reader.readAsDataURL(file);
  }

  private setSession(user: Utilisateur, message: string): void {
    this.currentUser = user;
    this.accountForm = this.createAccountForm(user);
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

  private createAccountForm(user: Utilisateur): Utilisateur {
    return {
      id: user.id,
      nom: user.nom,
      email: user.email,
      role: user.role,
      photo: user.photo,
      motDePasse: ''
    };
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
