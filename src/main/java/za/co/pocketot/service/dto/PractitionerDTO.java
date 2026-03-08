package za.co.pocketot.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import za.co.pocketot.domain.enumeration.IdDocument;
import za.co.pocketot.domain.enumeration.PractitionerType;

/**
 * A DTO for the {@link za.co.pocketot.domain.Practitioner} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PractitionerDTO implements Serializable {

    private Long id;

    private UUID uuid;

    private IdDocument idDocument;

    private String idNumber;

    private LocalDate dateOfBirth;

    private String firstName;

    private String lastName;

    private String registrationNumber;

    private Boolean verified;

    private PractitionerType practitionerType;

    private OrganizationDTO organization;

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

    public IdDocument getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(IdDocument idDocument) {
        this.idDocument = idDocument;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public PractitionerType getPractitionerType() {
        return practitionerType;
    }

    public void setPractitionerType(PractitionerType practitionerType) {
        this.practitionerType = practitionerType;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PractitionerDTO)) {
            return false;
        }

        PractitionerDTO practitionerDTO = (PractitionerDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, practitionerDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PractitionerDTO{" +
            "id=" + getId() +
            ", uuid='" + getUuid() + "'" +
            ", idDocument='" + getIdDocument() + "'" +
            ", idNumber='" + getIdNumber() + "'" +
            ", dateOfBirth='" + getDateOfBirth() + "'" +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", registrationNumber='" + getRegistrationNumber() + "'" +
            ", verified='" + getVerified() + "'" +
            ", practitionerType='" + getPractitionerType() + "'" +
            ", organization=" + getOrganization() +
            "}";
    }
}
