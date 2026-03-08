package za.co.pocketot.service.mapper;

import org.mapstruct.*;
import za.co.pocketot.domain.Exercise;
import za.co.pocketot.service.dto.ExerciseDTO;

/**
 * Mapper for the entity {@link Exercise} and its DTO {@link ExerciseDTO}.
 */
@Mapper(componentModel = "spring")
public interface ExerciseMapper extends EntityMapper<ExerciseDTO, Exercise> {}
