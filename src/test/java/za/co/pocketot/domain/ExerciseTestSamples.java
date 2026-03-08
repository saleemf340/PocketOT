package za.co.pocketot.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ExerciseTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Exercise getExerciseSample1() {
        return new Exercise().id(1L).uuid(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa")).name("name1").videoLink("videoLink1");
    }

    public static Exercise getExerciseSample2() {
        return new Exercise().id(2L).uuid(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367")).name("name2").videoLink("videoLink2");
    }

    public static Exercise getExerciseRandomSampleGenerator() {
        return new Exercise()
            .id(longCount.incrementAndGet())
            .uuid(UUID.randomUUID())
            .name(UUID.randomUUID().toString())
            .videoLink(UUID.randomUUID().toString());
    }
}
