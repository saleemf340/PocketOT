package za.co.pocketot.service.dto;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link za.co.pocketot.domain.Exercise} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExerciseDTO implements Serializable {

    private Long id;

    private UUID uuid;

    private String name;

    private String videoLink;

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

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExerciseDTO)) {
            return false;
        }

        ExerciseDTO exerciseDTO = (ExerciseDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, exerciseDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExerciseDTO{" +
            "id=" + getId() +
            ", uuid='" + getUuid() + "'" +
            ", name='" + getName() + "'" +
            ", videoLink='" + getVideoLink() + "'" +
            "}";
    }
}
