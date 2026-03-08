package za.co.pocketot.service.mapper;

import static za.co.pocketot.domain.PractitionerAsserts.*;
import static za.co.pocketot.domain.PractitionerTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PractitionerMapperTest {

    private PractitionerMapper practitionerMapper;

    @BeforeEach
    void setUp() {
        practitionerMapper = new PractitionerMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPractitionerSample1();
        var actual = practitionerMapper.toEntity(practitionerMapper.toDto(expected));
        assertPractitionerAllPropertiesEquals(expected, actual);
    }
}
