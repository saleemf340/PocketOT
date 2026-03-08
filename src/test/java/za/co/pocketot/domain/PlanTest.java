package za.co.pocketot.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static za.co.pocketot.domain.ExerciseTestSamples.*;
import static za.co.pocketot.domain.PatientTestSamples.*;
import static za.co.pocketot.domain.PlanTestSamples.*;
import static za.co.pocketot.domain.PractitionerTestSamples.*;

import org.junit.jupiter.api.Test;
import za.co.pocketot.web.rest.TestUtil;

class PlanTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Plan.class);
        Plan plan1 = getPlanSample1();
        Plan plan2 = new Plan();
        assertThat(plan1).isNotEqualTo(plan2);

        plan2.setId(plan1.getId());
        assertThat(plan1).isEqualTo(plan2);

        plan2 = getPlanSample2();
        assertThat(plan1).isNotEqualTo(plan2);
    }

    @Test
    void patientTest() {
        Plan plan = getPlanRandomSampleGenerator();
        Patient patientBack = getPatientRandomSampleGenerator();

        plan.setPatient(patientBack);
        assertThat(plan.getPatient()).isEqualTo(patientBack);

        plan.patient(null);
        assertThat(plan.getPatient()).isNull();
    }

    @Test
    void practitionerTest() {
        Plan plan = getPlanRandomSampleGenerator();
        Practitioner practitionerBack = getPractitionerRandomSampleGenerator();

        plan.setPractitioner(practitionerBack);
        assertThat(plan.getPractitioner()).isEqualTo(practitionerBack);

        plan.practitioner(null);
        assertThat(plan.getPractitioner()).isNull();
    }

    @Test
    void exerciseTest() {
        Plan plan = getPlanRandomSampleGenerator();
        Exercise exerciseBack = getExerciseRandomSampleGenerator();

        plan.setExercise(exerciseBack);
        assertThat(plan.getExercise()).isEqualTo(exerciseBack);

        plan.exercise(null);
        assertThat(plan.getExercise()).isNull();
    }
}
