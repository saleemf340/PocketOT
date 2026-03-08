import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IOrganization } from 'app/entities/organization/organization.model';
import { OrganizationService } from 'app/entities/organization/service/organization.service';
import { IdDocument } from 'app/entities/enumerations/id-document.model';
import { PractitionerType } from 'app/entities/enumerations/practitioner-type.model';
import { PractitionerService } from '../service/practitioner.service';
import { IPractitioner } from '../practitioner.model';
import { PractitionerFormGroup, PractitionerFormService } from './practitioner-form.service';

@Component({
  selector: 'jhi-practitioner-update',
  templateUrl: './practitioner-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PractitionerUpdateComponent implements OnInit {
  isSaving = false;
  practitioner: IPractitioner | null = null;
  idDocumentValues = Object.keys(IdDocument);
  practitionerTypeValues = Object.keys(PractitionerType);

  organizationsSharedCollection: IOrganization[] = [];

  protected practitionerService = inject(PractitionerService);
  protected practitionerFormService = inject(PractitionerFormService);
  protected organizationService = inject(OrganizationService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PractitionerFormGroup = this.practitionerFormService.createPractitionerFormGroup();

  compareOrganization = (o1: IOrganization | null, o2: IOrganization | null): boolean =>
    this.organizationService.compareOrganization(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ practitioner }) => {
      this.practitioner = practitioner;
      if (practitioner) {
        this.updateForm(practitioner);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const practitioner = this.practitionerFormService.getPractitioner(this.editForm);
    if (practitioner.id !== null) {
      this.subscribeToSaveResponse(this.practitionerService.update(practitioner));
    } else {
      this.subscribeToSaveResponse(this.practitionerService.create(practitioner));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPractitioner>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(practitioner: IPractitioner): void {
    this.practitioner = practitioner;
    this.practitionerFormService.resetForm(this.editForm, practitioner);

    this.organizationsSharedCollection = this.organizationService.addOrganizationToCollectionIfMissing<IOrganization>(
      this.organizationsSharedCollection,
      practitioner.organization,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.organizationService
      .query()
      .pipe(map((res: HttpResponse<IOrganization[]>) => res.body ?? []))
      .pipe(
        map((organizations: IOrganization[]) =>
          this.organizationService.addOrganizationToCollectionIfMissing<IOrganization>(organizations, this.practitioner?.organization),
        ),
      )
      .subscribe((organizations: IOrganization[]) => (this.organizationsSharedCollection = organizations));
  }
}
