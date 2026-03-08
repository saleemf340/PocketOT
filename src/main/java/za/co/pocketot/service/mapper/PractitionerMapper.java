package za.co.pocketot.service.mapper;

import org.mapstruct.*;
import za.co.pocketot.domain.Organization;
import za.co.pocketot.domain.Practitioner;
import za.co.pocketot.service.dto.OrganizationDTO;
import za.co.pocketot.service.dto.PractitionerDTO;

/**
 * Mapper for the entity {@link Practitioner} and its DTO {@link PractitionerDTO}.
 */
@Mapper(componentModel = "spring")
public interface PractitionerMapper extends EntityMapper<PractitionerDTO, Practitioner> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationId")
    PractitionerDTO toDto(Practitioner s);

    @Named("organizationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrganizationDTO toDtoOrganizationId(Organization organization);
}
