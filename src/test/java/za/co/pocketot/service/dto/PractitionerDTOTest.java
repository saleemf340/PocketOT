package za.co.pocketot.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import za.co.pocketot.web.rest.TestUtil;

class PractitionerDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PractitionerDTO.class);
        PractitionerDTO practitionerDTO1 = new PractitionerDTO();
        practitionerDTO1.setId(1L);
        PractitionerDTO practitionerDTO2 = new PractitionerDTO();
        assertThat(practitionerDTO1).isNotEqualTo(practitionerDTO2);
        practitionerDTO2.setId(practitionerDTO1.getId());
        assertThat(practitionerDTO1).isEqualTo(practitionerDTO2);
        practitionerDTO2.setId(2L);
        assertThat(practitionerDTO1).isNotEqualTo(practitionerDTO2);
        practitionerDTO1.setId(null);
        assertThat(practitionerDTO1).isNotEqualTo(practitionerDTO2);
    }
}
