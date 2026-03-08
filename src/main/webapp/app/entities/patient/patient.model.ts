import dayjs from 'dayjs/esm';
import { IdDocument } from 'app/entities/enumerations/id-document.model';

export interface IPatient {
  id: number;
  uuid?: string | null;
  idDocument?: keyof typeof IdDocument | null;
  idNumber?: string | null;
  dateOfBirth?: dayjs.Dayjs | null;
  firstName?: string | null;
  lastName?: string | null;
}

export type NewPatient = Omit<IPatient, 'id'> & { id: null };
