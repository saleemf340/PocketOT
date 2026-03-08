import dayjs from 'dayjs/esm';
import { IPatient } from 'app/entities/patient/patient.model';
import { IPractitioner } from 'app/entities/practitioner/practitioner.model';
import { IExercise } from 'app/entities/exercise/exercise.model';

export interface IPlan {
  id: number;
  uuid?: string | null;
  exerciseRepitition?: number | null;
  planRepitition?: number | null;
  effectiveFrom?: dayjs.Dayjs | null;
  effectiveTo?: dayjs.Dayjs | null;
  patient?: Pick<IPatient, 'id'> | null;
  practitioner?: Pick<IPractitioner, 'id'> | null;
  exercise?: Pick<IExercise, 'id'> | null;
}

export type NewPlan = Omit<IPlan, 'id'> & { id: null };
