import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPractitioner, NewPractitioner } from '../practitioner.model';

export type PartialUpdatePractitioner = Partial<IPractitioner> & Pick<IPractitioner, 'id'>;

type RestOf<T extends IPractitioner | NewPractitioner> = Omit<T, 'dateOfBirth'> & {
  dateOfBirth?: string | null;
};

export type RestPractitioner = RestOf<IPractitioner>;

export type NewRestPractitioner = RestOf<NewPractitioner>;

export type PartialUpdateRestPractitioner = RestOf<PartialUpdatePractitioner>;

export type EntityResponseType = HttpResponse<IPractitioner>;
export type EntityArrayResponseType = HttpResponse<IPractitioner[]>;

@Injectable({ providedIn: 'root' })
export class PractitionerService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/practitioners');

  create(practitioner: NewPractitioner): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(practitioner);
    return this.http
      .post<RestPractitioner>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(practitioner: IPractitioner): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(practitioner);
    return this.http
      .put<RestPractitioner>(`${this.resourceUrl}/${this.getPractitionerIdentifier(practitioner)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(practitioner: PartialUpdatePractitioner): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(practitioner);
    return this.http
      .patch<RestPractitioner>(`${this.resourceUrl}/${this.getPractitionerIdentifier(practitioner)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestPractitioner>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPractitioner[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPractitionerIdentifier(practitioner: Pick<IPractitioner, 'id'>): number {
    return practitioner.id;
  }

  comparePractitioner(o1: Pick<IPractitioner, 'id'> | null, o2: Pick<IPractitioner, 'id'> | null): boolean {
    return o1 && o2 ? this.getPractitionerIdentifier(o1) === this.getPractitionerIdentifier(o2) : o1 === o2;
  }

  addPractitionerToCollectionIfMissing<Type extends Pick<IPractitioner, 'id'>>(
    practitionerCollection: Type[],
    ...practitionersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const practitioners: Type[] = practitionersToCheck.filter(isPresent);
    if (practitioners.length > 0) {
      const practitionerCollectionIdentifiers = practitionerCollection.map(practitionerItem =>
        this.getPractitionerIdentifier(practitionerItem),
      );
      const practitionersToAdd = practitioners.filter(practitionerItem => {
        const practitionerIdentifier = this.getPractitionerIdentifier(practitionerItem);
        if (practitionerCollectionIdentifiers.includes(practitionerIdentifier)) {
          return false;
        }
        practitionerCollectionIdentifiers.push(practitionerIdentifier);
        return true;
      });
      return [...practitionersToAdd, ...practitionerCollection];
    }
    return practitionerCollection;
  }

  protected convertDateFromClient<T extends IPractitioner | NewPractitioner | PartialUpdatePractitioner>(practitioner: T): RestOf<T> {
    return {
      ...practitioner,
      dateOfBirth: practitioner.dateOfBirth?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restPractitioner: RestPractitioner): IPractitioner {
    return {
      ...restPractitioner,
      dateOfBirth: restPractitioner.dateOfBirth ? dayjs(restPractitioner.dateOfBirth) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestPractitioner>): HttpResponse<IPractitioner> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestPractitioner[]>): HttpResponse<IPractitioner[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
