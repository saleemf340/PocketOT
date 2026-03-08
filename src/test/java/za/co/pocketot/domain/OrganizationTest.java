package za.co.pocketot.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static za.co.pocketot.domain.OrganizationTestSamples.*;

import org.junit.jupiter.api.Test;
import za.co.pocketot.web.rest.TestUtil;

class OrganizationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Organization.class);
        Organization organization1 = getOrganizationSample1();
        Organization organization2 = new Organization();
        assertThat(organization1).isNotEqualTo(organization2);

        organization2.setId(organization1.getId());
        assertThat(organization1).isEqualTo(organization2);

        organization2 = getOrganizationSample2();
        assertThat(organization1).isNotEqualTo(organization2);
    }
}
