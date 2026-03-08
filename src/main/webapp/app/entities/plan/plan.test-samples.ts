import dayjs from 'dayjs/esm';

import { IPlan, NewPlan } from './plan.model';

export const sampleWithRequiredData: IPlan = {
  id: 10097,
};

export const sampleWithPartialData: IPlan = {
  id: 30458,
  uuid: '70d25d54-05b2-49f6-9fb1-5dea1b258b25',
  effectiveFrom: dayjs('2026-03-07'),
  effectiveTo: dayjs('2026-03-07'),
};

export const sampleWithFullData: IPlan = {
  id: 11506,
  uuid: '71f1bbe2-2144-4ef2-b6f9-c6fcddaee58e',
  exerciseRepitition: 22416,
  planRepitition: 20211,
  effectiveFrom: dayjs('2026-03-07'),
  effectiveTo: dayjs('2026-03-07'),
};

export const sampleWithNewData: NewPlan = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
