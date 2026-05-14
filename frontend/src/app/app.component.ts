import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  ApiService,
  HistoriqueRetour,
  NonConformite,
  ProduitStock,
  RetourProduit,
  Utilisateur
} from './api.service';

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
  loading = false;
  message = '';

  retourForm: RetourProduit = this.newRetour();
  nonConformiteForm: NonConformite = this.newNonConformite();
  utilisateurForm: Utilisateur = this.newUtilisateur();
  historiqueForm: HistoriqueRetour = this.newHistorique();
  stockForm: ProduitStock = this.newStock();

  constructor(private readonly api: ApiService) {}

  ngOnInit(): void {
    this.refresh();
  }

  get clients(): Utilisateur[] {
    return this.utilisateurs.filter((user) => user.role === 'CLIENT');
  }

  get employesQualite(): Utilisateur[] {
    return this.utilisateurs.filter((user) => user.role === 'EMPLOYE' || user.role === 'QUALITE' || user.role === 'ADMIN');
  }

  refresh(): void {
    this.loading = true;
    this.message = '';

    let pending = 5;
    const finish = () => {
      pending -= 1;
      if (pending === 0) this.loading = false;
    };

    this.api.getRetours().subscribe({
      next: (data) => (this.retours = data),
      error: () => this.showMessage('Impossible de charger les retours.'),
      complete: finish
    });

    this.api.getNonConformites().subscribe({
      next: (data) => (this.nonConformites = data),
      error: () => this.showMessage('Impossible de charger les non-conformites.'),
      complete: finish
    });

    this.api.getUtilisateurs().subscribe({
      next: (data) => (this.utilisateurs = data),
      error: () => this.showMessage('Impossible de charger les utilisateurs.'),
      complete: finish
    });

    this.api.getHistoriques().subscribe({
      next: (data) => (this.historiques = data),
      error: () => this.showMessage("Impossible de charger l'historique."),
      complete: finish
    });

    this.api.getStocks().subscribe({
      next: (data) => (this.stocks = data),
      error: () => this.showMessage('Impossible de charger le stock.'),
      complete: finish
    });
  }

  createRetour(): void {
    this.api.addRetour(this.normalizeRetour(this.retourForm)).subscribe({
      next: () => {
        this.retourForm = this.newRetour();
        this.showMessage('Retour ajoute.');
        this.refresh();
      },
      error: () => this.showMessage('Erreur pendant la creation du retour.')
    });
  }

  editRetour(retour: RetourProduit): void {
    this.retourForm = { ...retour, clientId: retour.clientId ?? null };
  }

  saveRetour(): void {
    if (!this.retourForm.id) {
      this.createRetour();
      return;
    }
    this.api.updateRetour(this.retourForm.id, this.normalizeRetour(this.retourForm)).subscribe({
      next: () => {
        this.retourForm = this.newRetour();
        this.showMessage('Retour mis a jour.');
        this.refresh();
      },
      error: () => this.showMessage('Erreur pendant la mise a jour du retour.')
    });
  }

  validateRetour(retour: RetourProduit): void {
    if (!retour.id) return;
    this.api.validateRetour(retour.id).subscribe({
      next: () => {
        this.showMessage('Retour valide et stock mis a jour.');
        this.refresh();
      },
      error: () => this.showMessage('Erreur pendant la validation.')
    });
  }

  rejectRetour(retour: RetourProduit): void {
    if (!retour.id) return;
    this.api.rejectRetour(retour.id).subscribe({
      next: () => this.refresh(),
      error: () => this.showMessage('Erreur pendant le rejet.')
    });
  }

  deleteRetour(retour: RetourProduit): void {
    if (!retour.id) return;
    this.api.deleteRetour(retour.id).subscribe({
      next: () => this.refresh(),
      error: () => this.showMessage('Erreur pendant la suppression.')
    });
  }

  createNonConformite(): void {
    this.api.addNonConformite(this.nonConformiteForm).subscribe({
      next: () => {
        this.nonConformiteForm = this.newNonConformite();
        this.showMessage('Non-conformite ajoutee.');
        this.refresh();
      },
      error: () => this.showMessage('Erreur pendant la creation de la non-conformite.')
    });
  }

  deleteNonConformite(item: NonConformite): void {
    if (!item.id) return;
    this.api.deleteNonConformite(item.id).subscribe({
      next: () => this.refresh(),
      error: () => this.showMessage('Erreur pendant la suppression de la non-conformite.')
    });
  }

  createUtilisateur(): void {
    this.api.addUtilisateur(this.utilisateurForm).subscribe({
      next: () => {
        this.utilisateurForm = this.newUtilisateur();
        this.showMessage('Utilisateur ajoute.');
        this.refresh();
      },
      error: () => this.showMessage("Erreur pendant la creation de l'utilisateur.")
    });
  }

  deleteUtilisateur(user: Utilisateur): void {
    if (!user.id) return;
    this.api.deleteUtilisateur(user.id).subscribe({
      next: () => this.refresh(),
      error: () => this.showMessage("Erreur pendant la suppression de l'utilisateur.")
    });
  }

  createHistorique(): void {
    this.api.addHistorique(this.historiqueForm).subscribe({
      next: () => {
        this.historiqueForm = this.newHistorique();
        this.showMessage('Action historisee.');
        this.refresh();
      },
      error: () => this.showMessage("Erreur pendant l'enregistrement de l'historique.")
    });
  }

  createStock(): void {
    this.api.addStock(this.stockForm).subscribe({
      next: () => {
        this.stockForm = this.newStock();
        this.showMessage('Produit ajoute au stock.');
        this.refresh();
      },
      error: () => this.showMessage('Erreur pendant la creation du stock.')
    });
  }

  editStock(stock: ProduitStock): void {
    this.stockForm = { ...stock };
  }

  saveStock(): void {
    if (!this.stockForm.id) {
      this.createStock();
      return;
    }
    this.api.updateStock(this.stockForm.id, this.stockForm).subscribe({
      next: () => {
        this.stockForm = this.newStock();
        this.showMessage('Stock mis a jour.');
        this.refresh();
      },
      error: () => this.showMessage('Erreur pendant la mise a jour du stock.')
    });
  }

  deleteStock(stock: ProduitStock): void {
    if (!stock.id) return;
    this.api.deleteStock(stock.id).subscribe({
      next: () => this.refresh(),
      error: () => this.showMessage('Erreur pendant la suppression du stock.')
    });
  }

  private normalizeRetour(retour: RetourProduit): RetourProduit {
    return {
      ...retour,
      quantite: Number(retour.quantite || 1),
      clientId: retour.clientId ? Number(retour.clientId) : null
    };
  }

  private newRetour(): RetourProduit {
    return {
      produit: '',
      raison: '',
      quantite: 1,
      etatTraitement: 'EN_ATTENTE',
      date: new Date().toISOString().slice(0, 10),
      clientId: null
    };
  }

  private newNonConformite(): NonConformite {
    return {
      description: '',
      gravite: 'MOYENNE',
      date: new Date().toISOString().slice(0, 10),
      retourId: null
    };
  }

  private newUtilisateur(): Utilisateur {
    return {
      nom: '',
      email: '',
      role: 'CLIENT'
    };
  }

  private newHistorique(): HistoriqueRetour {
    return {
      action: '',
      date: new Date().toISOString().slice(0, 10),
      retourId: null,
      employeId: null
    };
  }

  private newStock(): ProduitStock {
    return {
      nomProduit: '',
      quantiteDisponible: 0,
      quantiteRetournee: 0
    };
  }

  private showMessage(message: string): void {
    this.message = message;
  }
}
