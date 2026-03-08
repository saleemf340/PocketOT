package za.co.pocketot.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import za.co.pocketot.domain.enumeration.IdDocument;

/**
 * A Patient.
 */
@Table("patient")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Patient implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("uuid")
    private UUID uuid;

    @Column("id_document")
    private IdDocument idDocument;

    @Column("id_number")
    private String idNumber;

    @Column("date_of_birth")
    private LocalDate dateOfBirth;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Patient id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public Patient uuid(UUID uuid) {
        this.setUuid(uuid);
        return this;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public IdDocument getIdDocument() {
        return this.idDocument;
    }

    public Patient idDocument(IdDocument idDocument) {
        this.setIdDocument(idDocument);
        return this;
    }

    public void setIdDocument(IdDocument idDocument) {
        this.idDocument = idDocument;
    }

    public String getIdNumber() {
        return this.idNumber;
    }

    public Patient idNumber(String idNumber) {
        this.setIdNumber(idNumber);
        return this;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public LocalDate getDateOfBirth() {
        return this.dateOfBirth;
    }

    public Patient dateOfBirth(LocalDate dateOfBirth) {
        this.setDateOfBirth(dateOfBirth);
        return this;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Patient firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Patient lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Patient)) {
            return false;
        }
        return getId() != null && getId().equals(((Patient) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Patient{" +
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
