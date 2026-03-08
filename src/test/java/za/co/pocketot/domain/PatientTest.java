package za.co.pocketot.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static za.co.pocketot.domain.PatientTestSamples.*;

import org.junit.jupiter.api.Test;
import za.co.pocketot.web.rest.TestUtil;

class PatientTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Patient.class);
        Patient patient1 = getPatientSample1();
        Patient patient2 = new Patient();
        assertThat(patient1).isNotEqualTo(patient2);

        patient2.setId(patient1.getId());
        assertThat(patient1).isEqualTo(patient2);

        patient2 = getPatientSample2();
        assertThat(patient1).isNotEqualTo(patient2);
    }
}
