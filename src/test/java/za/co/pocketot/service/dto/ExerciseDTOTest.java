package za.co.pocketot.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import za.co.pocketot.web.rest.TestUtil;

class ExerciseDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ExerciseDTO.class);
        ExerciseDTO exerciseDTO1 = new ExerciseDTO();
        exerciseDTO1.setId(1L);
        ExerciseDTO exerciseDTO2 = new ExerciseDTO();
        assertThat(exerciseDTO1).isNotEqualTo(exerciseDTO2);
        exerciseDTO2.setId(exerciseDTO1.getId());
        assertThat(exerciseDTO1).isEqualTo(exerciseDTO2);
        exerciseDTO2.setId(2L);
        assertThat(exerciseDTO1).isNotEqualTo(exerciseDTO2);
        exerciseDTO1.setId(null);
        assertThat(exerciseDTO1).isNotEqualTo(exerciseDTO2);
    }
}
