package za.co.pocketot.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PractitionerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Practitioner getPractitionerSample1() {
        return new Practitioner()
            .id(1L)
            .uuid(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .idNumber("idNumber1")
            .firstName("firstName1")
            .lastName("lastName1")
            .registrationNumber("registrationNumber1");
    }

    public static Practitioner getPractitionerSample2() {
        return new Practitioner()
            .id(2L)
            .uuid(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .idNumber("idNumber2")
            .firstName("firstName2")
            .lastName("lastName2")
            .registrationNumber("registrationNumber2");
    }

    public static Practitioner getPractitionerRandomSampleGenerator() {
        return new Practitioner()
            .id(longCount.incrementAndGet())
            .uuid(UUID.randomUUID())
            .idNumber(UUID.randomUUID().toString())
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .registrationNumber(UUID.randomUUID().toString());
    }
}
