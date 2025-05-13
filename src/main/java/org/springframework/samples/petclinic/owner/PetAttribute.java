package org.springframework.samples.petclinic.owner;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.samples.petclinic.model.BaseEntity;

@Entity
@Table(name = "pet_attributes")
public class PetAttribute extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @Column(name = "temperament")
    private String temperament;

    @Column(name = "length_cm")
    @DecimalMin(value = "0.0", message = "Length must be positive")
    private Double lengthCm;

    @Column(name = "weight_kg")
    @DecimalMin(value = "0.0", message = "Weight must be positive")
    private Double weightKg;

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public String getTemperament() {
        return temperament;
    }

    public void setTemperament(String temperament) {
        this.temperament = temperament;
    }

    public Double getLengthCm() {
        return lengthCm;
    }

    public void setLengthCm(Double lengthCm) {
        this.lengthCm = lengthCm;
    }

    public Double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(Double weightKg) {
        this.weightKg = weightKg;
    }

    @Override
    public String toString() {
        return "PetAttribute{" + "temperament='" + temperament + '\'' + ", lengthCm=" + lengthCm +
                ", weightKg=" + weightKg +
                '}';
    }
}
