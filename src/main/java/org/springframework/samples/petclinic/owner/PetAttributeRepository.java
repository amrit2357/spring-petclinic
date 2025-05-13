package org.springframework.samples.petclinic.owner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PetAttributeRepository extends JpaRepository<PetAttribute, Integer> {

    Optional<PetAttribute> findByPetId(Integer petId);
}