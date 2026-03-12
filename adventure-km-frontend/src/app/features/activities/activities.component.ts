import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-activities',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './activities.component.html',
  styleUrl: './activities.component.css'
})
export class ActivitiesComponent {

  activities = [
    {
      title: "GR54",
      date: "2023-09-01",
      description: "Marcher et courir en montagne sur 4,5 jours pour découvrir et parcourir le tour du massif de l'Oisans (200KM, 12000m de dénivelé)."
    },
    {
      title: "UTOBI",
      date: "2025-07-20",
      description: "Réaliser l'ultra trail de l'obiou, correspondant à une boucle de 72km pour 4200m de dénivelé autour du massif du Dévoluy. Chrono réalisé : 12H00"
    }, 
	{
      title: "UT4M Challenge",
      date: "2023-07-20",
      description: "Réaliser le tour des 4 massifs autour de Grenoble (Vercors, Taillefer, Belledonne, et la Chartreuse) en 4 étapes/4 jours. DNF (did not finish)."
    }
  ];

}