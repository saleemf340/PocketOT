import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { PractitionerDetailComponent } from './practitioner-detail.component';

describe('Practitioner Management Detail Component', () => {
  let comp: PractitionerDetailComponent;
  let fixture: ComponentFixture<PractitionerDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PractitionerDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./practitioner-detail.component').then(m => m.PractitionerDetailComponent),
              resolve: { practitioner: () => of({ id: 27164 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(PractitionerDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PractitionerDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load practitioner on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PractitionerDetailComponent);

      // THEN
      expect(instance.practitioner()).toEqual(expect.objectContaining({ id: 27164 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
