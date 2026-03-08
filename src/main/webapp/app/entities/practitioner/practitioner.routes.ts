import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import PractitionerResolve from './route/practitioner-routing-resolve.service';

const practitionerRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/practitioner.component').then(m => m.PractitionerComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/practitioner-detail.component').then(m => m.PractitionerDetailComponent),
    resolve: {
      practitioner: PractitionerResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/practitioner-update.component').then(m => m.PractitionerUpdateComponent),
    resolve: {
      practitioner: PractitionerResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/practitioner-update.component').then(m => m.PractitionerUpdateComponent),
    resolve: {
      practitioner: PractitionerResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default practitionerRoute;
