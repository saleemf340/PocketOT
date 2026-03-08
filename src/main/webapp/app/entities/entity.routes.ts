import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'pocketOtApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'patient',
    data: { pageTitle: 'pocketOtApp.patient.home.title' },
    loadChildren: () => import('./patient/patient.routes'),
  },
  {
    path: 'practitioner',
    data: { pageTitle: 'pocketOtApp.practitioner.home.title' },
    loadChildren: () => import('./practitioner/practitioner.routes'),
  },
  {
    path: 'organization',
    data: { pageTitle: 'pocketOtApp.organization.home.title' },
    loadChildren: () => import('./organization/organization.routes'),
  },
  {
    path: 'exercise',
    data: { pageTitle: 'pocketOtApp.exercise.home.title' },
    loadChildren: () => import('./exercise/exercise.routes'),
  },
  {
    path: 'plan',
    data: { pageTitle: 'pocketOtApp.plan.home.title' },
    loadChildren: () => import('./plan/plan.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
