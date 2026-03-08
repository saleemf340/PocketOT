package za.co.pocketot.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import za.co.pocketot.domain.enumeration.IdDocument;

/**
 * A DTO for the {@link za.co.pocketot.domain.Patient} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PatientDTO implements Serializable {

    private Long id;

    private UUID uuid;

    private IdDocument idDocument;

    private String idNumber;

    private LocalDate dateOfBirth;

    private String firstName;

    private String lastName;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PatientDTO)) {
            return false;
        }

        PatientDTO patientDTO = (PatientDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, patientDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PatientDTO{" +
            "id=" + getId() +
            ", uuid='" + getUuid() + "'" +
            ", idDocument='" + getIdDocument() + "'" +
            ", idNumber='" + getIdNumber() + "'" +
            ", dateOfBirth='" + getDateOfBirth() + "'" +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            "}";
    }
}
