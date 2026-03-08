import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPlan, NewPlan } from '../plan.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPlan for edit and NewPlanFormGroupInput for create.
 */
type PlanFormGroupInput = IPlan | PartialWithRequiredKeyOf<NewPlan>;

type PlanFormDefaults = Pick<NewPlan, 'id'>;

type PlanFormGroupContent = {
  id: FormControl<IPlan['id'] | NewPlan['id']>;
  uuid: FormControl<IPlan['uuid']>;
  exerciseRepitition: FormControl<IPlan['exerciseRepitition']>;
  planRepitition: FormControl<IPlan['planRepitition']>;
  effectiveFrom: FormControl<IPlan['effectiveFrom']>;
  effectiveTo: FormControl<IPlan['effectiveTo']>;
  patient: FormControl<IPlan['patient']>;
  practitioner: FormControl<IPlan['practitioner']>;
  exercise: FormControl<IPlan['exercise']>;
};

export type PlanFormGroup = FormGroup<PlanFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PlanFormService {
  createPlanFormGroup(plan: PlanFormGroupInput = { id: null }): PlanFormGroup {
    const planRawValue = {
      ...this.getFormDefaults(),
      ...plan,
    };
    return new FormGroup<PlanFormGroupContent>({
      id: new FormControl(
        { value: planRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      uuid: new FormControl(planRawValue.uuid),
      exerciseRepitition: new FormControl(planRawValue.exerciseRepitition),
      planRepitition: new FormControl(planRawValue.planRepitition),
      effectiveFrom: new FormControl(planRawValue.effectiveFrom),
      effectiveTo: new FormControl(planRawValue.effectiveTo),
      patient: new FormControl(planRawValue.patient),
      practitioner: new FormControl(planRawValue.practitioner),
      exercise: new FormControl(planRawValue.exercise),
    });
  }

  getPlan(form: PlanFormGroup): IPlan | NewPlan {
    return form.getRawValue() as IPlan | NewPlan;
  }

  resetForm(form: PlanFormGroup, plan: PlanFormGroupInput): void {
    const planRawValue = { ...this.getFormDefaults(), ...plan };
    form.reset(
      {
        ...planRawValue,
        id: { value: planRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PlanFormDefaults {
    return {
      id: null,
    };
  }
}
