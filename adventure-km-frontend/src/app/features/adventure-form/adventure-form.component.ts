import { Component, OnInit, signal } from '@angular/core';
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
  step = signal(1);
  editId: number | null = null;

  title = '';
  date = '';
  type = '';
  difficulty = 3;
  content = '';
  selectedEquipmentIds: number[] = [];

  equipment = signal<EquipmentItemResponse[]>([]);
  savedId = signal<number | null>(null);
  error = signal<string | null>(null);

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
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.editId = +id;
      this.api.getById(this.editId).subscribe(adv => {
        this.title = adv.title;
        this.date = adv.date;
        this.type = adv.type ?? '';
        this.difficulty = adv.difficulty ?? 3;
        this.content = adv.content;
        this.selectedEquipmentIds = adv.equipment.map(e => e.id);
        this.savedId.set(adv.id);
      });
    }
  }

  saveMetadata(): void {
    const request = {
      title: this.title,
      date: this.date,
      content: this.content,
      type: this.type || undefined,
      difficulty: this.difficulty,
      equipmentIds: this.selectedEquipmentIds
    };

    const obs = this.editId
      ? this.api.update(this.editId, request)
      : this.api.create(request);

    obs.subscribe({
      next: res => {
        this.savedId.set(res.id);
        this.step.set(2);
      },
      error: () => this.error.set('Erreur lors de la sauvegarde')
    });
  }

  onGpxSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file || !this.savedId()) return;
    this.api.uploadGpx(this.savedId()!, file).subscribe({
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
