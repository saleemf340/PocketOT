package za.co.pocketot.domain;

import java.io.Serializable;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Organization.
 */
@Table("organization")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Organization implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("uuid")
    private UUID uuid;

    @Column("name")
    private String name;

    @Column("registration_number")
    private String registrationNumber;

    @Column("verified")
    private Boolean verified;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Organization id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public Organization uuid(UUID uuid) {
        this.setUuid(uuid);
        return this;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return this.name;
    }

    public Organization name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegistrationNumber() {
        return this.registrationNumber;
    }

    public Organization registrationNumber(String registrationNumber) {
        this.setRegistrationNumber(registrationNumber);
        return this;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public Boolean getVerified() {
        return this.verified;
    }

    public Organization verified(Boolean verified) {
        this.setVerified(verified);
        return this;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Organization)) {
            return false;
        }
        return getId() != null && getId().equals(((Organization) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Organization{" +
            "id=" + getId() +
            ", uuid='" + getUuid() + "'" +
            ", name='" + getName() + "'" +
            ", registrationNumber='" + getRegistrationNumber() + "'" +
            ", verified='" + getVerified() + "'" +
            "}";
    }
}
