package za.co.pocketot.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static za.co.pocketot.domain.OrganizationTestSamples.*;
import static za.co.pocketot.domain.PractitionerTestSamples.*;

import org.junit.jupiter.api.Test;
import za.co.pocketot.web.rest.TestUtil;

class PractitionerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Practitioner.class);
        Practitioner practitioner1 = getPractitionerSample1();
        Practitioner practitioner2 = new Practitioner();
        assertThat(practitioner1).isNotEqualTo(practitioner2);

        practitioner2.setId(practitioner1.getId());
        assertThat(practitioner1).isEqualTo(practitioner2);

        practitioner2 = getPractitionerSample2();
        assertThat(practitioner1).isNotEqualTo(practitioner2);
    }

    @Test
    void organizationTest() {
        Practitioner practitioner = getPractitionerRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        practitioner.setOrganization(organizationBack);
        assertThat(practitioner.getOrganization()).isEqualTo(organizationBack);

        practitioner.organization(null);
        assertThat(practitioner.getOrganization()).isNull();
    }
}
