import dayjs from 'dayjs/esm';

import { IPatient, NewPatient } from './patient.model';

export const sampleWithRequiredData: IPatient = {
  id: 2514,
};

export const sampleWithPartialData: IPatient = {
  id: 5584,
  uuid: '8957b6b8-6978-475d-b89b-002be374fc6a',
  idDocument: 'PASSPORT',
  dateOfBirth: dayjs('2026-03-07'),
  firstName: 'Kristoffer',
  lastName: 'Rolfson',
};

export const sampleWithFullData: IPatient = {
  id: 9819,
  uuid: '3abbb772-5990-47fe-896b-de90617b39b2',
  idDocument: 'SOUTH_AFRICAN_ID',
  idNumber: 'dependable pfft',
  dateOfBirth: dayjs('2026-03-07'),
  firstName: 'Constance',
  lastName: 'Heathcote',
};

export const sampleWithNewData: NewPatient = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
