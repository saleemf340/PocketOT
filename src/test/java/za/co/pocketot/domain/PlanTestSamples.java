package za.co.pocketot.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PlanTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Plan getPlanSample1() {
        return new Plan().id(1L).uuid(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa")).exerciseRepitition(1).planRepitition(1);
    }

    public static Plan getPlanSample2() {
        return new Plan().id(2L).uuid(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367")).exerciseRepitition(2).planRepitition(2);
    }

    public static Plan getPlanRandomSampleGenerator() {
        return new Plan()
            .id(longCount.incrementAndGet())
            .uuid(UUID.randomUUID())
            .exerciseRepitition(intCount.incrementAndGet())
            .planRepitition(intCount.incrementAndGet());
    }
}
