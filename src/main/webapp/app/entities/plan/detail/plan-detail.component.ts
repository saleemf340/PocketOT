import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatePipe } from 'app/shared/date';
import { IPlan } from '../plan.model';

@Component({
  selector: 'jhi-plan-detail',
  templateUrl: './plan-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatePipe],
})
export class PlanDetailComponent {
  plan = input<IPlan | null>(null);

  previousState(): void {
    window.history.back();
  }
}
