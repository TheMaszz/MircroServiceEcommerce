import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ImageUrlPipe } from 'app/core/pipe/imageUrlPipe';

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CommonModule, ImageUrlPipe, RouterModule],
  templateUrl: './product-card.component.html',
  styleUrl: './product-card.component.scss'
})

export class ProductCardComponent {

  constructor(){}

  @Input() id!: number;
  @Input() name!: string;
  @Input() imgUrl!: string;
  @Input() price!: number;

}
