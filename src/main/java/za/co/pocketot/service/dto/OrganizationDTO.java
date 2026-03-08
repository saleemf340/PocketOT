package za.co.pocketot.service.dto;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link za.co.pocketot.domain.Organization} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrganizationDTO implements Serializable {

    private Long id;

    private UUID uuid;

    private String name;

    private String registrationNumber;

    private Boolean verified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrganizationDTO)) {
            return false;
        }

        OrganizationDTO organizationDTO = (OrganizationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, organizationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrganizationDTO{" +
            "id=" + getId() +
            ", uuid='" + getUuid() + "'" +
            ", name='" + getName() + "'" +
            ", registrationNumber='" + getRegistrationNumber() + "'" +
            ", verified='" + getVerified() + "'" +
            "}";
    }
}
