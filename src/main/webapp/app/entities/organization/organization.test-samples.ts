import { IOrganization, NewOrganization } from './organization.model';

export const sampleWithRequiredData: IOrganization = {
  id: 20534,
};

export const sampleWithPartialData: IOrganization = {
  id: 4752,
  registrationNumber: 'afore yippee toothpick',
  verified: true,
};

export const sampleWithFullData: IOrganization = {
  id: 12562,
  uuid: '0bf3ecd1-f4e1-487c-b12c-8f7290643be5',
  name: 'by why yuck',
  registrationNumber: 'independence upon',
  verified: true,
};

export const sampleWithNewData: NewOrganization = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
