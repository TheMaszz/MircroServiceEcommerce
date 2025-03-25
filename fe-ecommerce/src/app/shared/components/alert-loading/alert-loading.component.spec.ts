import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AlertLoadingComponent } from './alert-loading.component';

describe('AlertLoadingComponent', () => {
  let component: AlertLoadingComponent;
  let fixture: ComponentFixture<AlertLoadingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlertLoadingComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AlertLoadingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
