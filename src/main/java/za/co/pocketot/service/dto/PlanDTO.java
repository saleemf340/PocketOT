package za.co.pocketot.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link za.co.pocketot.domain.Plan} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PlanDTO implements Serializable {

    private Long id;

    private UUID uuid;

    private Integer exerciseRepitition;

    private Integer planRepitition;

    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    private PatientDTO patient;

    private PractitionerDTO practitioner;

    private ExerciseDTO exercise;

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

    public Integer getExerciseRepitition() {
        return exerciseRepitition;
    }

    public void setExerciseRepitition(Integer exerciseRepitition) {
        this.exerciseRepitition = exerciseRepitition;
    }

    public Integer getPlanRepitition() {
        return planRepitition;
    }

    public void setPlanRepitition(Integer planRepitition) {
        this.planRepitition = planRepitition;
    }

    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(LocalDate effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public LocalDate getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(LocalDate effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    public PatientDTO getPatient() {
        return patient;
    }

    public void setPatient(PatientDTO patient) {
        this.patient = patient;
    }

    public PractitionerDTO getPractitioner() {
        return practitioner;
    }

    public void setPractitioner(PractitionerDTO practitioner) {
        this.practitioner = practitioner;
    }

    public ExerciseDTO getExercise() {
        return exercise;
    }

    public void setExercise(ExerciseDTO exercise) {
        this.exercise = exercise;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlanDTO)) {
            return false;
        }

        PlanDTO planDTO = (PlanDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, planDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlanDTO{" +
            "id=" + getId() +
            ", uuid='" + getUuid() + "'" +
            ", exerciseRepitition=" + getExerciseRepitition() +
            ", planRepitition=" + getPlanRepitition() +
            ", effectiveFrom='" + getEffectiveFrom() + "'" +
            ", effectiveTo='" + getEffectiveTo() + "'" +
            ", patient=" + getPatient() +
            ", practitioner=" + getPractitioner() +
            ", exercise=" + getExercise() +
            "}";
    }
}
