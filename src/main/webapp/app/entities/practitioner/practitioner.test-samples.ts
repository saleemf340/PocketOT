import dayjs from 'dayjs/esm';

import { IPractitioner, NewPractitioner } from './practitioner.model';

export const sampleWithRequiredData: IPractitioner = {
  id: 30305,
};

export const sampleWithPartialData: IPractitioner = {
  id: 8293,
  uuid: '0a391fb5-9864-4c9d-846f-af9da36acd1b',
  dateOfBirth: dayjs('2026-03-08'),
  lastName: 'Von',
  practitionerType: 'PHYSIOTHERAPRIST',
};

export const sampleWithFullData: IPractitioner = {
  id: 17896,
  uuid: 'd1feea97-2adb-4d12-b6b1-b5745b6904f4',
  idDocument: 'SOUTH_AFRICAN_ID',
  idNumber: 'drug',
  dateOfBirth: dayjs('2026-03-07'),
  firstName: 'Joel',
  lastName: 'Hahn',
  registrationNumber: 'accomplished pinstripe earth',
  verified: false,
  practitionerType: 'PHYSIOTHERAPRIST',
};

export const sampleWithNewData: NewPractitioner = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
