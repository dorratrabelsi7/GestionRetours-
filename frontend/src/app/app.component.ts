import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService, NonConformite, RetourProduit, Utilisateur } from './api.service';

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
  loading = false;
  message = '';

  retourForm: RetourProduit = {
    produit: '',
    raison: '',
    etatTraitement: 'EN_ATTENTE',
    date: new Date().toISOString().slice(0, 10)
  };

  nonConformiteForm: NonConformite = {
    description: '',
    gravite: 'MOYENNE',
    date: new Date().toISOString().slice(0, 10)
  };

  utilisateurForm: Utilisateur = {
    nom: '',
    email: '',
    role: 'CLIENT'
  };

  constructor(private readonly api: ApiService) {}

  ngOnInit(): void {
    this.refresh();
  }

  refresh(): void {
    this.loading = true;
    this.message = '';

    this.api.getRetours().subscribe({
      next: (data) => (this.retours = data),
      error: () => this.showMessage('Impossible de charger les retours.')
    });

    this.api.getNonConformites().subscribe({
      next: (data) => (this.nonConformites = data),
      error: () => this.showMessage('Impossible de charger les non-conformites.')
    });

    this.api.getUtilisateurs().subscribe({
      next: (data) => {
        this.utilisateurs = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.showMessage('Impossible de charger les utilisateurs.');
      }
    });
  }

  createRetour(): void {
    this.api.addRetour(this.retourForm).subscribe({
      next: () => {
        this.retourForm = {
          produit: '',
          raison: '',
          etatTraitement: 'EN_ATTENTE',
          date: new Date().toISOString().slice(0, 10)
        };
        this.showMessage('Retour ajoute.');
        this.refresh();
      },
      error: () => this.showMessage('Erreur pendant la creation du retour.')
    });
  }

  validateRetour(retour: RetourProduit): void {
    if (!retour.id) return;
    this.api.validateRetour(retour.id).subscribe({
      next: () => this.refresh(),
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
        this.nonConformiteForm = {
          description: '',
          gravite: 'MOYENNE',
          date: new Date().toISOString().slice(0, 10)
        };
        this.showMessage('Non-conformite ajoutee.');
        this.refresh();
      },
      error: () => this.showMessage('Erreur pendant la creation de la non-conformite.')
    });
  }

  createUtilisateur(): void {
    this.api.addUtilisateur(this.utilisateurForm).subscribe({
      next: () => {
        this.utilisateurForm = { nom: '', email: '', role: 'CLIENT' };
        this.showMessage('Utilisateur ajoute.');
        this.refresh();
      },
      error: () => this.showMessage("Erreur pendant la creation de l'utilisateur.")
    });
  }

  private showMessage(message: string): void {
    this.message = message;
  }
}
