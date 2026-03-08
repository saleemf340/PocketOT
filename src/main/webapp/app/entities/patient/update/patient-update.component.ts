import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IdDocument } from 'app/entities/enumerations/id-document.model';
import { IPatient } from '../patient.model';
import { PatientService } from '../service/patient.service';
import { PatientFormGroup, PatientFormService } from './patient-form.service';

@Component({
  selector: 'jhi-patient-update',
  templateUrl: './patient-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PatientUpdateComponent implements OnInit {
  isSaving = false;
  patient: IPatient | null = null;
  idDocumentValues = Object.keys(IdDocument);

  protected patientService = inject(PatientService);
  protected patientFormService = inject(PatientFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PatientFormGroup = this.patientFormService.createPatientFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ patient }) => {
      this.patient = patient;
      if (patient) {
        this.updateForm(patient);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const patient = this.patientFormService.getPatient(this.editForm);
    if (patient.id !== null) {
      this.subscribeToSaveResponse(this.patientService.update(patient));
    } else {
      this.subscribeToSaveResponse(this.patientService.create(patient));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPatient>>): void {
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

  protected updateForm(patient: IPatient): void {
    this.patient = patient;
    this.patientFormService.resetForm(this.editForm, patient);
  }
}
