package za.co.pocketot.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class OrganizationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Organization getOrganizationSample1() {
        return new Organization()
            .id(1L)
            .uuid(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .name("name1")
            .registrationNumber("registrationNumber1");
    }

    public static Organization getOrganizationSample2() {
        return new Organization()
            .id(2L)
            .uuid(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .name("name2")
            .registrationNumber("registrationNumber2");
    }

    public static Organization getOrganizationRandomSampleGenerator() {
        return new Organization()
            .id(longCount.incrementAndGet())
            .uuid(UUID.randomUUID())
            .name(UUID.randomUUID().toString())
            .registrationNumber(UUID.randomUUID().toString());
    }
}
