package za.co.pocketot.service.mapper;

import org.mapstruct.*;
import za.co.pocketot.domain.Organization;
import za.co.pocketot.service.dto.OrganizationDTO;

/**
 * Mapper for the entity {@link Organization} and its DTO {@link OrganizationDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrganizationMapper extends EntityMapper<OrganizationDTO, Organization> {}
