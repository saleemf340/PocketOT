import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatePipe } from 'app/shared/date';
import { IPractitioner } from '../practitioner.model';

@Component({
  selector: 'jhi-practitioner-detail',
  templateUrl: './practitioner-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatePipe],
})
export class PractitionerDetailComponent {
  practitioner = input<IPractitioner | null>(null);

  previousState(): void {
    window.history.back();
  }
}
