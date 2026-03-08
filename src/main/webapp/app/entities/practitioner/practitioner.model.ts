import dayjs from 'dayjs/esm';
import { IOrganization } from 'app/entities/organization/organization.model';
import { IdDocument } from 'app/entities/enumerations/id-document.model';
import { PractitionerType } from 'app/entities/enumerations/practitioner-type.model';

export interface IPractitioner {
  id: number;
  uuid?: string | null;
  idDocument?: keyof typeof IdDocument | null;
  idNumber?: string | null;
  dateOfBirth?: dayjs.Dayjs | null;
  firstName?: string | null;
  lastName?: string | null;
  registrationNumber?: string | null;
  verified?: boolean | null;
  practitionerType?: keyof typeof PractitionerType | null;
  organization?: Pick<IOrganization, 'id'> | null;
}

export type NewPractitioner = Omit<IPractitioner, 'id'> & { id: null };
