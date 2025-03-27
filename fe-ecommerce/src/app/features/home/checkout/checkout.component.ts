import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CartGroup } from 'app/models/cart.model';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [],
  templateUrl: './checkout.component.html',
  styleUrl: './checkout.component.scss'
})
export class CheckoutComponent implements OnInit {

  constructor(){}

  selectedItems: CartGroup[] = []

  ngOnInit(): void {

  }

}
