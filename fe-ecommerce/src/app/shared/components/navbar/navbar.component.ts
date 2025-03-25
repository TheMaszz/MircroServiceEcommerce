import { CommonModule } from '@angular/common';
import { Component, ViewChild } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MaterialModules } from 'app/core/modules/material.module';
import { HomeService } from 'app/features/home/home.service';
import {
  debounceTime,
  distinctUntilChanged,
  map,
  Observable,
  startWith,
  Subscription,
  switchMap,
} from 'rxjs';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [MaterialModules, ReactiveFormsModule, CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss',
})
export class NavbarComponent {
  constructor(
    private homeService: HomeService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  private paramsSubscription: Subscription | null = null;

  @ViewChild(MatAutocompleteTrigger) autocomplete!: MatAutocompleteTrigger;

  searchProduct = new FormControl('');
  filteredOptions$!: Observable<any[]>;

  ngOnInit() {
    this.paramsSubscription = this.route.queryParams.subscribe((params) => {
      this.searchProduct.setValue(params['search'] || '');
    });

    this.filteredOptions$ = this.searchProduct.valueChanges.pipe(
      startWith(''),
      debounceTime(300),
      distinctUntilChanged(),
      switchMap((value) => this.getAutoCompleteOptions(value || ''))
    );
  }

  ngOnDestroy(): void {
    if (this.paramsSubscription) {
      this.paramsSubscription.unsubscribe();
    }
  }

  getAutoCompleteOptions(search: string): Observable<any[]> {
    return this.homeService
      .getAutoCompleteProduct({ search: search, limit: 10 })
      .pipe(map((response) => response.data));
  }

  onSearchSelected(event: any) {
    const selectedValue = event.option.value;
    this.router.navigateByUrl(
      `/products?search=${encodeURIComponent(selectedValue)}&page=1`
    );
  }

  onSearchEnter(event: any): void {
    if (event.code === 'Enter' || event.code === 'NumpadEnter') {
      event.preventDefault();
      let keyword = this.searchProduct.value?.trim().toLowerCase();
      this.autocomplete.closePanel();
      this.searchProduct.setValue(keyword || '');
      this.router.navigateByUrl(`/products?search=${keyword}&page=1`);
    }
  }
}
