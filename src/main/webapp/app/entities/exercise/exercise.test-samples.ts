import { IExercise, NewExercise } from './exercise.model';

export const sampleWithRequiredData: IExercise = {
  id: 11343,
};

export const sampleWithPartialData: IExercise = {
  id: 8602,
  uuid: 'f4ba3556-78ab-41ff-aefb-8f20d065f124',
};

export const sampleWithFullData: IExercise = {
  id: 29477,
  uuid: '68a8f95e-6765-464a-b282-1c9296cc2e25',
  name: 'publication scientific',
  videoLink: 'boo',
};

export const sampleWithNewData: NewExercise = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
