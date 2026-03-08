package za.co.pocketot.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Plan.
 */
@Table("plan")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Plan implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("uuid")
    private UUID uuid;

    @Column("exercise_repitition")
    private Integer exerciseRepitition;

    @Column("plan_repitition")
    private Integer planRepitition;

    @Column("effective_from")
    private LocalDate effectiveFrom;

    @Column("effective_to")
    private LocalDate effectiveTo;

    @org.springframework.data.annotation.Transient
    private Patient patient;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization" }, allowSetters = true)
    private Practitioner practitioner;

    @org.springframework.data.annotation.Transient
    private Exercise exercise;

    @Column("patient_id")
    private Long patientId;

    @Column("practitioner_id")
    private Long practitionerId;

    @Column("exercise_id")
    private Long exerciseId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Plan id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public Plan uuid(UUID uuid) {
        this.setUuid(uuid);
        return this;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Integer getExerciseRepitition() {
        return this.exerciseRepitition;
    }

    public Plan exerciseRepitition(Integer exerciseRepitition) {
        this.setExerciseRepitition(exerciseRepitition);
        return this;
    }

    public void setExerciseRepitition(Integer exerciseRepitition) {
        this.exerciseRepitition = exerciseRepitition;
    }

    public Integer getPlanRepitition() {
        return this.planRepitition;
    }

    public Plan planRepitition(Integer planRepitition) {
        this.setPlanRepitition(planRepitition);
        return this;
    }

    public void setPlanRepitition(Integer planRepitition) {
        this.planRepitition = planRepitition;
    }

    public LocalDate getEffectiveFrom() {
        return this.effectiveFrom;
    }

    public Plan effectiveFrom(LocalDate effectiveFrom) {
        this.setEffectiveFrom(effectiveFrom);
        return this;
    }

    public void setEffectiveFrom(LocalDate effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public LocalDate getEffectiveTo() {
        return this.effectiveTo;
    }

    public Plan effectiveTo(LocalDate effectiveTo) {
        this.setEffectiveTo(effectiveTo);
        return this;
    }

    public void setEffectiveTo(LocalDate effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    public Patient getPatient() {
        return this.patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
        this.patientId = patient != null ? patient.getId() : null;
    }

    public Plan patient(Patient patient) {
        this.setPatient(patient);
        return this;
    }

    public Practitioner getPractitioner() {
        return this.practitioner;
    }

    public void setPractitioner(Practitioner practitioner) {
        this.practitioner = practitioner;
        this.practitionerId = practitioner != null ? practitioner.getId() : null;
    }

    public Plan practitioner(Practitioner practitioner) {
        this.setPractitioner(practitioner);
        return this;
    }

    public Exercise getExercise() {
        return this.exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
        this.exerciseId = exercise != null ? exercise.getId() : null;
    }

    public Plan exercise(Exercise exercise) {
        this.setExercise(exercise);
        return this;
    }

    public Long getPatientId() {
        return this.patientId;
    }

    public void setPatientId(Long patient) {
        this.patientId = patient;
    }

    public Long getPractitionerId() {
        return this.practitionerId;
    }

    public void setPractitionerId(Long practitioner) {
        this.practitionerId = practitioner;
    }

    public Long getExerciseId() {
        return this.exerciseId;
    }

    public void setExerciseId(Long exercise) {
        this.exerciseId = exercise;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Plan)) {
            return false;
        }
        return getId() != null && getId().equals(((Plan) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Plan{" +
            "id=" + getId() +
            ", uuid='" + getUuid() + "'" +
            ", exerciseRepitition=" + getExerciseRepitition() +
            ", planRepitition=" + getPlanRepitition() +
            ", effectiveFrom='" + getEffectiveFrom() + "'" +
            ", effectiveTo='" + getEffectiveTo() + "'" +
            "}";
    }
}
