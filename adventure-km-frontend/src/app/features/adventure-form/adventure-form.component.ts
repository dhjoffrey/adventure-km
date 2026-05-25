import { Component, OnInit, signal, ViewChild, ElementRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AdventureApiService } from '../../core/services/adventure.service';
import { EquipmentItemResponse } from '../../core/models/equipment.model';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-adventure-form',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './adventure-form.component.html',
  styleUrl: './adventure-form.component.css'
})
export class AdventureFormComponent implements OnInit {
  @ViewChild('contentArea') contentArea!: ElementRef<HTMLTextAreaElement>;

  step = signal(1);
  editId: number | null = null;

  title = '';
  date = '';
  type = '';
  difficulty = 3;
  content = '';
  selectedEquipmentIds: number[] = [];
  distanceKm: number | null = null;
  elevationGainM: number | null = null;
  durationHours: number | null = null;
  durationMins: number | null = null;

  equipment = signal<EquipmentItemResponse[]>([]);
  savedId = signal<number | null>(null);
  error = signal<string | null>(null);
  deleteModal = signal(false);
  fieldErrors = signal<Record<string, string>>({});
  gpxFile = signal<File | null>(null);
  extractStats = false;

  constructor(
    private api: AdventureApiService,
    private http: HttpClient,
    public router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.http.get<EquipmentItemResponse[]>('/api/equipment').subscribe(
      items => this.equipment.set(items)
    );

    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      this.resetForm();
      if (id) {
        this.editId = +id;
        this.api.getById(this.editId).subscribe(adv => {
          this.title = adv.title;
          this.date = adv.date;
          this.type = adv.type ?? '';
          this.difficulty = adv.difficulty ?? 3;
          this.content = adv.content;
          this.selectedEquipmentIds = adv.equipment.map(e => e.id);
          this.distanceKm = adv.stats?.distanceKm ?? null;
          this.elevationGainM = adv.stats?.elevationGainM ?? null;
          const totalMins = adv.stats?.durationMinutes ?? null;
          if (totalMins !== null) {
            this.durationHours = Math.floor(totalMins / 60);
            this.durationMins = totalMins % 60;
          }
          this.savedId.set(adv.id);
        });
      }
    });
  }

  private resetForm(): void {
    this.editId = null;
    this.step.set(1);
    this.title = '';
    this.date = '';
    this.type = '';
    this.difficulty = 3;
    this.content = '';
    this.selectedEquipmentIds = [];
    this.distanceKm = null;
    this.elevationGainM = null;
    this.durationHours = null;
    this.durationMins = null;
    this.savedId.set(null);
    this.error.set(null);
    this.deleteModal.set(false);
    this.fieldErrors.set({});
    this.gpxFile.set(null);
    this.extractStats = false;
  }

  private computeDurationMinutes(): number | undefined {
    const h = this.durationHours ?? 0;
    const m = this.durationMins ?? 0;
    const total = h * 60 + m;
    return total > 0 ? total : undefined;
  }

  insertMarkdown(before: string, after = ''): void {
    const ta = this.contentArea?.nativeElement;
    if (!ta) return;
    const start = ta.selectionStart;
    const end = ta.selectionEnd;
    const selected = this.content.slice(start, end);
    this.content = this.content.slice(0, start) + before + selected + after + this.content.slice(end);
    requestAnimationFrame(() => {
      ta.selectionStart = start + before.length;
      ta.selectionEnd = start + before.length + selected.length;
      ta.focus();
    });
  }

  saveMetadata(): void {
    const errors: Record<string, string> = {};
    if (!this.title.trim()) errors['title'] = 'Le titre est obligatoire';
    if (!this.date) errors['date'] = 'La date est obligatoire';
    if (!this.content.trim()) errors['content'] = 'Le contenu est obligatoire';
    this.fieldErrors.set(errors);
    if (Object.keys(errors).length > 0) {
      this.error.set('Certains champs obligatoires ne sont pas remplis.');
      return;
    }
    this.error.set(null);

    const request = {
      title: this.title,
      date: this.date,
      content: this.content,
      type: this.type || undefined,
      difficulty: this.difficulty,
      equipmentIds: this.selectedEquipmentIds,
      distanceKm: this.distanceKm ?? undefined,
      elevationGainM: this.elevationGainM ?? undefined,
      durationMinutes: this.computeDurationMinutes()
    };

    const obs = this.editId
      ? this.api.update(this.editId, request)
      : this.api.create(request);

    obs.subscribe({
      next: res => {
        this.savedId.set(res.id);
        this.step.set(2);
      },
      error: () => this.error.set('Erreur serveur lors de la sauvegarde. Vérifiez votre connexion.')
    });
  }

  deleteAdventure(): void {
    if (!this.editId) return;
    this.api.delete(this.editId).subscribe({
      next: () => this.router.navigate(['/adventures']),
      error: () => {
        this.deleteModal.set(false);
        this.error.set('Erreur lors de la suppression');
      }
    });
  }

  onGpxSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    this.gpxFile.set(file ?? null);
    this.extractStats = false;
  }

  uploadGpxAndContinue(): void {
    const file = this.gpxFile();
    if (!file || !this.savedId()) return;
    this.api.uploadGpx(this.savedId()!, file, this.extractStats).subscribe({
      next: () => this.step.set(3),
      error: () => this.error.set('Erreur lors de l\'upload GPX')
    });
  }

  onPhotoSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file || !this.savedId()) return;
    this.api.uploadPhoto(this.savedId()!, file, '').subscribe({
      error: () => this.error.set('Erreur lors de l\'upload photo')
    });
  }

  publish(): void {
    if (!this.savedId()) return;
    this.api.publish(this.savedId()!).subscribe({
      next: () => this.router.navigate(['/adventures', this.savedId()]),
      error: () => this.error.set('Erreur lors de la publication')
    });
  }

  toggleEquipment(id: number): void {
    const idx = this.selectedEquipmentIds.indexOf(id);
    if (idx >= 0) this.selectedEquipmentIds.splice(idx, 1);
    else this.selectedEquipmentIds.push(id);
  }

  isSelected(id: number): boolean {
    return this.selectedEquipmentIds.includes(id);
  }
}
