import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MaterialModules } from 'app/core/modules/material.module';
import { UserService } from 'app/core/services/user.service';
import { Address } from 'app/models/user.model';
import { ModalAddressComponent } from './components/modal-address/modal-address.component';

@Component({
  selector: 'app-address',
  standalone: true,
  imports: [MaterialModules, CommonModule],
  templateUrl: './address.component.html',
})
export class AddressComponent implements OnInit {
  constructor(
    private userService: UserService, 
    private dialog: MatDialog
  ) {}

  addressList: Address[] = [];

  ngOnInit(): void {
    this.userService.myAddress$.subscribe({
      next: (response) => {
        console.log('res: ', response);
        this.addressList = response!;
      },
      error: (error) => {
        console.log('res Error: ', error);
      },
    });
  }



  trackByAddressId(index: number, address: Address): number {
    return address.id;
  }

  createDialog() {
    this.dialog
      .open(ModalAddressComponent, {
        width: '400px',
        data: {
          condition: 'create',
        },
      })
      .afterClosed()
      .subscribe((data) => {
        this.userService.refreshAddress();
      });
  }

  updateDialog(id: number, address: Address) {
    this.dialog
      .open(ModalAddressComponent, {
        width: '400px',
        data: {
          condition: 'update',
          id: id,
          address: address,
        },
      })
      .afterClosed()
      .subscribe((data) => {
        this.userService.refreshAddress();
      });
  }

  deleteDialog(id: number) {
    this.dialog
      .open(ModalAddressComponent, {
        width: '400px',
        data: {
          condition: 'delete',
          id: id,
        },
      })
      .afterClosed()
      .subscribe((data) => {
        this.userService.refreshAddress();
      });
  }

  defaultAddress(id: number) {
    this.userService.defaultAddress(id).subscribe({
      next: (response) => {
        console.log('res: ', response);
        this.userService.refreshAddress();
      },
      error: (error) => {
        console.log('res Error: ', error);
      },
    });
  }
}
