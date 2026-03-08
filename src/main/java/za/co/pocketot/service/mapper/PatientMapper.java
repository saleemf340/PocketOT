package za.co.pocketot.service.mapper;

import org.mapstruct.*;
import za.co.pocketot.domain.Patient;
import za.co.pocketot.service.dto.PatientDTO;

/**
 * Mapper for the entity {@link Patient} and its DTO {@link PatientDTO}.
 */
@Mapper(componentModel = "spring")
public interface PatientMapper extends EntityMapper<PatientDTO, Patient> {}
