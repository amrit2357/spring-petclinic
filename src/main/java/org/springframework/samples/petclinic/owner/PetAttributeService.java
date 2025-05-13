package org.springframework.samples.petclinic.owner;

import org.springframework.samples.petclinic.system.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PetAttributeService {

    private final PetAttributeRepository petAttributeRepository;
    private final OwnerRepository ownerRepository;

    public PetAttributeService(PetAttributeRepository petAttributeRepository, OwnerRepository ownerRepository) {
        this.petAttributeRepository = petAttributeRepository;
        this.ownerRepository = ownerRepository;
    }

    @Transactional(readOnly = true)
    public PetAttribute findPetAttributesByPetId(Integer petId) {
        return petAttributeRepository.findByPetId(petId).orElse(null);
    }


    @Transactional
    public PetAttribute savePetAttributesToDb(Pet pet, PetAttribute attributesToSave) {
    if (pet == null || attributesToSave == null) {
            throw new IllegalArgumentException("attributes must no null");
        }
        PetAttribute existing = petAttributeRepository.findByPetId(pet.getId()).orElse(null);
        if (existing != null) {
            existing.setTemperament(attributesToSave.getTemperament());
            existing.setLengthCm(attributesToSave.getLengthCm());
            existing.setWeightKg(attributesToSave.getWeightKg());
            return petAttributeRepository.save(existing);
        }

        attributesToSave.setPet(pet);

        return petAttributeRepository.save(attributesToSave); // Save to DB
    }

    @Transactional
    public PetAttribute savePetAttributes(Integer ownerId, Integer petId, PetAttribute attributesToSave) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));

        Pet pet = owner.getPet(petId);
        // Raise excetion if not found
        if (pet == null) {
            throw new ResourceNotFoundException("Pet not found with id: " + petId + " for owner " + ownerId);
        }

        return savePetAttributesToDb(pet, attributesToSave); // call
    }

}
