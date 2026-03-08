import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPractitioner, NewPractitioner } from '../practitioner.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPractitioner for edit and NewPractitionerFormGroupInput for create.
 */
type PractitionerFormGroupInput = IPractitioner | PartialWithRequiredKeyOf<NewPractitioner>;

type PractitionerFormDefaults = Pick<NewPractitioner, 'id' | 'verified'>;

type PractitionerFormGroupContent = {
  id: FormControl<IPractitioner['id'] | NewPractitioner['id']>;
  uuid: FormControl<IPractitioner['uuid']>;
  idDocument: FormControl<IPractitioner['idDocument']>;
  idNumber: FormControl<IPractitioner['idNumber']>;
  dateOfBirth: FormControl<IPractitioner['dateOfBirth']>;
  firstName: FormControl<IPractitioner['firstName']>;
  lastName: FormControl<IPractitioner['lastName']>;
  registrationNumber: FormControl<IPractitioner['registrationNumber']>;
  verified: FormControl<IPractitioner['verified']>;
  practitionerType: FormControl<IPractitioner['practitionerType']>;
  organization: FormControl<IPractitioner['organization']>;
};

export type PractitionerFormGroup = FormGroup<PractitionerFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PractitionerFormService {
  createPractitionerFormGroup(practitioner: PractitionerFormGroupInput = { id: null }): PractitionerFormGroup {
    const practitionerRawValue = {
      ...this.getFormDefaults(),
      ...practitioner,
    };
    return new FormGroup<PractitionerFormGroupContent>({
      id: new FormControl(
        { value: practitionerRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      uuid: new FormControl(practitionerRawValue.uuid),
      idDocument: new FormControl(practitionerRawValue.idDocument),
      idNumber: new FormControl(practitionerRawValue.idNumber),
      dateOfBirth: new FormControl(practitionerRawValue.dateOfBirth),
      firstName: new FormControl(practitionerRawValue.firstName),
      lastName: new FormControl(practitionerRawValue.lastName),
      registrationNumber: new FormControl(practitionerRawValue.registrationNumber),
      verified: new FormControl(practitionerRawValue.verified),
      practitionerType: new FormControl(practitionerRawValue.practitionerType),
      organization: new FormControl(practitionerRawValue.organization),
    });
  }

  getPractitioner(form: PractitionerFormGroup): IPractitioner | NewPractitioner {
    return form.getRawValue() as IPractitioner | NewPractitioner;
  }

  resetForm(form: PractitionerFormGroup, practitioner: PractitionerFormGroupInput): void {
    const practitionerRawValue = { ...this.getFormDefaults(), ...practitioner };
    form.reset(
      {
        ...practitionerRawValue,
        id: { value: practitionerRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PractitionerFormDefaults {
    return {
      id: null,
      verified: false,
    };
  }
}
