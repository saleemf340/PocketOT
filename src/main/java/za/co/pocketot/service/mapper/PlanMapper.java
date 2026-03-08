package za.co.pocketot.service.mapper;

import org.mapstruct.*;
import za.co.pocketot.domain.Exercise;
import za.co.pocketot.domain.Patient;
import za.co.pocketot.domain.Plan;
import za.co.pocketot.domain.Practitioner;
import za.co.pocketot.service.dto.ExerciseDTO;
import za.co.pocketot.service.dto.PatientDTO;
import za.co.pocketot.service.dto.PlanDTO;
import za.co.pocketot.service.dto.PractitionerDTO;

/**
 * Mapper for the entity {@link Plan} and its DTO {@link PlanDTO}.
 */
@Mapper(componentModel = "spring")
public interface PlanMapper extends EntityMapper<PlanDTO, Plan> {
    @Mapping(target = "patient", source = "patient", qualifiedByName = "patientId")
    @Mapping(target = "practitioner", source = "practitioner", qualifiedByName = "practitionerId")
    @Mapping(target = "exercise", source = "exercise", qualifiedByName = "exerciseId")
    PlanDTO toDto(Plan s);

    @Named("patientId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PatientDTO toDtoPatientId(Patient patient);

    @Named("practitionerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PractitionerDTO toDtoPractitionerId(Practitioner practitioner);

    @Named("exerciseId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ExerciseDTO toDtoExerciseId(Exercise exercise);
}
