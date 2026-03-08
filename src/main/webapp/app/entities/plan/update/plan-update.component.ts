import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IPatient } from 'app/entities/patient/patient.model';
import { PatientService } from 'app/entities/patient/service/patient.service';
import { IPractitioner } from 'app/entities/practitioner/practitioner.model';
import { PractitionerService } from 'app/entities/practitioner/service/practitioner.service';
import { IExercise } from 'app/entities/exercise/exercise.model';
import { ExerciseService } from 'app/entities/exercise/service/exercise.service';
import { PlanService } from '../service/plan.service';
import { IPlan } from '../plan.model';
import { PlanFormGroup, PlanFormService } from './plan-form.service';

@Component({
  selector: 'jhi-plan-update',
  templateUrl: './plan-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PlanUpdateComponent implements OnInit {
  isSaving = false;
  plan: IPlan | null = null;

  patientsSharedCollection: IPatient[] = [];
  practitionersSharedCollection: IPractitioner[] = [];
  exercisesSharedCollection: IExercise[] = [];

  protected planService = inject(PlanService);
  protected planFormService = inject(PlanFormService);
  protected patientService = inject(PatientService);
  protected practitionerService = inject(PractitionerService);
  protected exerciseService = inject(ExerciseService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PlanFormGroup = this.planFormService.createPlanFormGroup();

  comparePatient = (o1: IPatient | null, o2: IPatient | null): boolean => this.patientService.comparePatient(o1, o2);

  comparePractitioner = (o1: IPractitioner | null, o2: IPractitioner | null): boolean =>
    this.practitionerService.comparePractitioner(o1, o2);

  compareExercise = (o1: IExercise | null, o2: IExercise | null): boolean => this.exerciseService.compareExercise(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ plan }) => {
      this.plan = plan;
      if (plan) {
        this.updateForm(plan);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const plan = this.planFormService.getPlan(this.editForm);
    if (plan.id !== null) {
      this.subscribeToSaveResponse(this.planService.update(plan));
    } else {
      this.subscribeToSaveResponse(this.planService.create(plan));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPlan>>): void {
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

  protected updateForm(plan: IPlan): void {
    this.plan = plan;
    this.planFormService.resetForm(this.editForm, plan);

    this.patientsSharedCollection = this.patientService.addPatientToCollectionIfMissing<IPatient>(
      this.patientsSharedCollection,
      plan.patient,
    );
    this.practitionersSharedCollection = this.practitionerService.addPractitionerToCollectionIfMissing<IPractitioner>(
      this.practitionersSharedCollection,
      plan.practitioner,
    );
    this.exercisesSharedCollection = this.exerciseService.addExerciseToCollectionIfMissing<IExercise>(
      this.exercisesSharedCollection,
      plan.exercise,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.patientService
      .query()
      .pipe(map((res: HttpResponse<IPatient[]>) => res.body ?? []))
      .pipe(map((patients: IPatient[]) => this.patientService.addPatientToCollectionIfMissing<IPatient>(patients, this.plan?.patient)))
      .subscribe((patients: IPatient[]) => (this.patientsSharedCollection = patients));

    this.practitionerService
      .query()
      .pipe(map((res: HttpResponse<IPractitioner[]>) => res.body ?? []))
      .pipe(
        map((practitioners: IPractitioner[]) =>
          this.practitionerService.addPractitionerToCollectionIfMissing<IPractitioner>(practitioners, this.plan?.practitioner),
        ),
      )
      .subscribe((practitioners: IPractitioner[]) => (this.practitionersSharedCollection = practitioners));

    this.exerciseService
      .query()
      .pipe(map((res: HttpResponse<IExercise[]>) => res.body ?? []))
      .pipe(
        map((exercises: IExercise[]) => this.exerciseService.addExerciseToCollectionIfMissing<IExercise>(exercises, this.plan?.exercise)),
      )
      .subscribe((exercises: IExercise[]) => (this.exercisesSharedCollection = exercises));
  }
}
