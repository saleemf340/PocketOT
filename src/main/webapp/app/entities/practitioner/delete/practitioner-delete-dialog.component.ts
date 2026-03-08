import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IPractitioner } from '../practitioner.model';
import { PractitionerService } from '../service/practitioner.service';

@Component({
  templateUrl: './practitioner-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class PractitionerDeleteDialogComponent {
  practitioner?: IPractitioner;

  protected practitionerService = inject(PractitionerService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.practitionerService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
