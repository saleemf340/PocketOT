package za.co.pocketot.domain;

import java.io.Serializable;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Exercise.
 */
@Table("exercise")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Exercise implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("uuid")
    private UUID uuid;

    @Column("name")
    private String name;

    @Column("video_link")
    private String videoLink;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Exercise id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public Exercise uuid(UUID uuid) {
        this.setUuid(uuid);
        return this;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return this.name;
    }

    public Exercise name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVideoLink() {
        return this.videoLink;
    }

    public Exercise videoLink(String videoLink) {
        this.setVideoLink(videoLink);
        return this;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Exercise)) {
            return false;
        }
        return getId() != null && getId().equals(((Exercise) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Exercise{" +
            "id=" + getId() +
            ", uuid='" + getUuid() + "'" +
            ", name='" + getName() + "'" +
            ", videoLink='" + getVideoLink() + "'" +
            "}";
    }
}
