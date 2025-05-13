package org.springframework.samples.petclinic.owner;

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
}
