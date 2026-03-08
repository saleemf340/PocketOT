package za.co.pocketot.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import za.co.pocketot.domain.enumeration.IdDocument;
import za.co.pocketot.domain.enumeration.PractitionerType;

/**
 * A Practitioner.
 */
@Table("practitioner")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Practitioner implements Serializable {

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

    @Column("registration_number")
    private String registrationNumber;

    @Column("verified")
    private Boolean verified;

    @Column("practitioner_type")
    private PractitionerType practitionerType;

    @org.springframework.data.annotation.Transient
    private Organization organization;

    @Column("organization_id")
    private Long organizationId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Practitioner id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public Practitioner uuid(UUID uuid) {
        this.setUuid(uuid);
        return this;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public IdDocument getIdDocument() {
        return this.idDocument;
    }

    public Practitioner idDocument(IdDocument idDocument) {
        this.setIdDocument(idDocument);
        return this;
    }

    public void setIdDocument(IdDocument idDocument) {
        this.idDocument = idDocument;
    }

    public String getIdNumber() {
        return this.idNumber;
    }

    public Practitioner idNumber(String idNumber) {
        this.setIdNumber(idNumber);
        return this;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public LocalDate getDateOfBirth() {
        return this.dateOfBirth;
    }

    public Practitioner dateOfBirth(LocalDate dateOfBirth) {
        this.setDateOfBirth(dateOfBirth);
        return this;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Practitioner firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Practitioner lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRegistrationNumber() {
        return this.registrationNumber;
    }

    public Practitioner registrationNumber(String registrationNumber) {
        this.setRegistrationNumber(registrationNumber);
        return this;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public Boolean getVerified() {
        return this.verified;
    }

    public Practitioner verified(Boolean verified) {
        this.setVerified(verified);
        return this;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public PractitionerType getPractitionerType() {
        return this.practitionerType;
    }

    public Practitioner practitionerType(PractitionerType practitionerType) {
        this.setPractitionerType(practitionerType);
        return this;
    }

    public void setPractitionerType(PractitionerType practitionerType) {
        this.practitionerType = practitionerType;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        this.organizationId = organization != null ? organization.getId() : null;
    }

    public Practitioner organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organization) {
        this.organizationId = organization;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Practitioner)) {
            return false;
        }
        return getId() != null && getId().equals(((Practitioner) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Practitioner{" +
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
            "}";
    }
}
