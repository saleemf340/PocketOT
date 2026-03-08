import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../practitioner.test-samples';

import { PractitionerFormService } from './practitioner-form.service';

describe('Practitioner Form Service', () => {
  let service: PractitionerFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PractitionerFormService);
  });

  describe('Service methods', () => {
    describe('createPractitionerFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPractitionerFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uuid: expect.any(Object),
            idDocument: expect.any(Object),
            idNumber: expect.any(Object),
            dateOfBirth: expect.any(Object),
            firstName: expect.any(Object),
            lastName: expect.any(Object),
            registrationNumber: expect.any(Object),
            verified: expect.any(Object),
            practitionerType: expect.any(Object),
            organization: expect.any(Object),
          }),
        );
      });

      it('passing IPractitioner should create a new form with FormGroup', () => {
        const formGroup = service.createPractitionerFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            uuid: expect.any(Object),
            idDocument: expect.any(Object),
            idNumber: expect.any(Object),
            dateOfBirth: expect.any(Object),
            firstName: expect.any(Object),
            lastName: expect.any(Object),
            registrationNumber: expect.any(Object),
            verified: expect.any(Object),
            practitionerType: expect.any(Object),
            organization: expect.any(Object),
          }),
        );
      });
    });

    describe('getPractitioner', () => {
      it('should return NewPractitioner for default Practitioner initial value', () => {
        const formGroup = service.createPractitionerFormGroup(sampleWithNewData);

        const practitioner = service.getPractitioner(formGroup) as any;

        expect(practitioner).toMatchObject(sampleWithNewData);
      });

      it('should return NewPractitioner for empty Practitioner initial value', () => {
        const formGroup = service.createPractitionerFormGroup();

        const practitioner = service.getPractitioner(formGroup) as any;

        expect(practitioner).toMatchObject({});
      });

      it('should return IPractitioner', () => {
        const formGroup = service.createPractitionerFormGroup(sampleWithRequiredData);

        const practitioner = service.getPractitioner(formGroup) as any;

        expect(practitioner).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPractitioner should not enable id FormControl', () => {
        const formGroup = service.createPractitionerFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPractitioner should disable id FormControl', () => {
        const formGroup = service.createPractitionerFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
