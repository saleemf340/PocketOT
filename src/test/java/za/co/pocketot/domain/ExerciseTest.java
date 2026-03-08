package za.co.pocketot.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static za.co.pocketot.domain.ExerciseTestSamples.*;

import org.junit.jupiter.api.Test;
import za.co.pocketot.web.rest.TestUtil;

class ExerciseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Exercise.class);
        Exercise exercise1 = getExerciseSample1();
        Exercise exercise2 = new Exercise();
        assertThat(exercise1).isNotEqualTo(exercise2);

        exercise2.setId(exercise1.getId());
        assertThat(exercise1).isEqualTo(exercise2);

        exercise2 = getExerciseSample2();
        assertThat(exercise1).isNotEqualTo(exercise2);
    }
}
