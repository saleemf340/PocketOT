export interface IOrganization {
  id: number;
  uuid?: string | null;
  name?: string | null;
  registrationNumber?: string | null;
  verified?: boolean | null;
}

export type NewOrganization = Omit<IOrganization, 'id'> & { id: null };
