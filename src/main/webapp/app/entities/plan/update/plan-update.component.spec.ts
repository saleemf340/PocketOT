import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IPatient } from 'app/entities/patient/patient.model';
import { PatientService } from 'app/entities/patient/service/patient.service';
import { IPractitioner } from 'app/entities/practitioner/practitioner.model';
import { PractitionerService } from 'app/entities/practitioner/service/practitioner.service';
import { IExercise } from 'app/entities/exercise/exercise.model';
import { ExerciseService } from 'app/entities/exercise/service/exercise.service';
import { IPlan } from '../plan.model';
import { PlanService } from '../service/plan.service';
import { PlanFormService } from './plan-form.service';

import { PlanUpdateComponent } from './plan-update.component';

describe('Plan Management Update Component', () => {
  let comp: PlanUpdateComponent;
  let fixture: ComponentFixture<PlanUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let planFormService: PlanFormService;
  let planService: PlanService;
  let patientService: PatientService;
  let practitionerService: PractitionerService;
  let exerciseService: ExerciseService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [PlanUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(PlanUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PlanUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    planFormService = TestBed.inject(PlanFormService);
    planService = TestBed.inject(PlanService);
    patientService = TestBed.inject(PatientService);
    practitionerService = TestBed.inject(PractitionerService);
    exerciseService = TestBed.inject(ExerciseService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Patient query and add missing value', () => {
      const plan: IPlan = { id: 5247 };
      const patient: IPatient = { id: 16668 };
      plan.patient = patient;

      const patientCollection: IPatient[] = [{ id: 16668 }];
      jest.spyOn(patientService, 'query').mockReturnValue(of(new HttpResponse({ body: patientCollection })));
      const additionalPatients = [patient];
      const expectedCollection: IPatient[] = [...additionalPatients, ...patientCollection];
      jest.spyOn(patientService, 'addPatientToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ plan });
      comp.ngOnInit();

      expect(patientService.query).toHaveBeenCalled();
      expect(patientService.addPatientToCollectionIfMissing).toHaveBeenCalledWith(
        patientCollection,
        ...additionalPatients.map(expect.objectContaining),
      );
      expect(comp.patientsSharedCollection).toEqual(expectedCollection);
    });

    it('should call Practitioner query and add missing value', () => {
      const plan: IPlan = { id: 5247 };
      const practitioner: IPractitioner = { id: 27164 };
      plan.practitioner = practitioner;

      const practitionerCollection: IPractitioner[] = [{ id: 27164 }];
      jest.spyOn(practitionerService, 'query').mockReturnValue(of(new HttpResponse({ body: practitionerCollection })));
      const additionalPractitioners = [practitioner];
      const expectedCollection: IPractitioner[] = [...additionalPractitioners, ...practitionerCollection];
      jest.spyOn(practitionerService, 'addPractitionerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ plan });
      comp.ngOnInit();

      expect(practitionerService.query).toHaveBeenCalled();
      expect(practitionerService.addPractitionerToCollectionIfMissing).toHaveBeenCalledWith(
        practitionerCollection,
        ...additionalPractitioners.map(expect.objectContaining),
      );
      expect(comp.practitionersSharedCollection).toEqual(expectedCollection);
    });

    it('should call Exercise query and add missing value', () => {
      const plan: IPlan = { id: 5247 };
      const exercise: IExercise = { id: 19704 };
      plan.exercise = exercise;

      const exerciseCollection: IExercise[] = [{ id: 19704 }];
      jest.spyOn(exerciseService, 'query').mockReturnValue(of(new HttpResponse({ body: exerciseCollection })));
      const additionalExercises = [exercise];
      const expectedCollection: IExercise[] = [...additionalExercises, ...exerciseCollection];
      jest.spyOn(exerciseService, 'addExerciseToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ plan });
      comp.ngOnInit();

      expect(exerciseService.query).toHaveBeenCalled();
      expect(exerciseService.addExerciseToCollectionIfMissing).toHaveBeenCalledWith(
        exerciseCollection,
        ...additionalExercises.map(expect.objectContaining),
      );
      expect(comp.exercisesSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const plan: IPlan = { id: 5247 };
      const patient: IPatient = { id: 16668 };
      plan.patient = patient;
      const practitioner: IPractitioner = { id: 27164 };
      plan.practitioner = practitioner;
      const exercise: IExercise = { id: 19704 };
      plan.exercise = exercise;

      activatedRoute.data = of({ plan });
      comp.ngOnInit();

      expect(comp.patientsSharedCollection).toContainEqual(patient);
      expect(comp.practitionersSharedCollection).toContainEqual(practitioner);
      expect(comp.exercisesSharedCollection).toContainEqual(exercise);
      expect(comp.plan).toEqual(plan);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPlan>>();
      const plan = { id: 13856 };
      jest.spyOn(planFormService, 'getPlan').mockReturnValue(plan);
      jest.spyOn(planService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ plan });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: plan }));
      saveSubject.complete();

      // THEN
      expect(planFormService.getPlan).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(planService.update).toHaveBeenCalledWith(expect.objectContaining(plan));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPlan>>();
      const plan = { id: 13856 };
      jest.spyOn(planFormService, 'getPlan').mockReturnValue({ id: null });
      jest.spyOn(planService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ plan: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: plan }));
      saveSubject.complete();

      // THEN
      expect(planFormService.getPlan).toHaveBeenCalled();
      expect(planService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPlan>>();
      const plan = { id: 13856 };
      jest.spyOn(planService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ plan });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(planService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePatient', () => {
      it('should forward to patientService', () => {
        const entity = { id: 16668 };
        const entity2 = { id: 16914 };
        jest.spyOn(patientService, 'comparePatient');
        comp.comparePatient(entity, entity2);
        expect(patientService.comparePatient).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('comparePractitioner', () => {
      it('should forward to practitionerService', () => {
        const entity = { id: 27164 };
        const entity2 = { id: 7855 };
        jest.spyOn(practitionerService, 'comparePractitioner');
        comp.comparePractitioner(entity, entity2);
        expect(practitionerService.comparePractitioner).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareExercise', () => {
      it('should forward to exerciseService', () => {
        const entity = { id: 19704 };
        const entity2 = { id: 9508 };
        jest.spyOn(exerciseService, 'compareExercise');
        comp.compareExercise(entity, entity2);
        expect(exerciseService.compareExercise).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
