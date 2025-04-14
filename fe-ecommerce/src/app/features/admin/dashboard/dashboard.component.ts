import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { MaterialModules } from 'app/core/modules/material.module';
import { UserService } from 'app/core/services/user.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [MaterialModules, CommonModule],
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent {
  constructor(private userService: UserService) {}

  dashbordData: any;

  ngOnInit(): void {
    window.alertLoading();
    this.userService.getDashboardData().subscribe({
      next: (res) => {
        console.log('Dashboard Data:', res.data);
        this.dashbordData = res.data;
        window.closeLoading();
      },
      error: (err) => {
        console.error('Error fetching dashboard data:', err);
        window.closeLoading();
      },
    });
  }

  checkRole(role: number): string {
    switch (role) {
      case 0:
        return 'ผู้ใช้งาน';
      case 1:
        return 'ผู้ดูแลระบบ';
      case 2:
        return 'ผู้ขาย';
      default:
        return 'Unknown Role';
    }
  }
}
