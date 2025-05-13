package org.springframework.samples.petclinic.owner;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.system.errors.ResourceNotFoundException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

// To get the Attributes of pet
@RestController
@RequestMapping("/api/owners/{ownerId}/pets/{petId}/attributes")
public class PetAttributeController {

    private final PetAttributeService petAttributeService;
    private final OwnerRepository ownerRepository;

    public PetAttributeController(PetAttributeService petAttributeService, OwnerRepository ownerRepository) {
        this.petAttributeService = petAttributeService;
        this.ownerRepository = ownerRepository;
    }

    private Pet getPetOrThrow(Integer ownerId, Integer petId) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found: " + ownerId)); // Added Exceotion
        Pet pet = owner.getPet(petId);
        if (pet == null) {
            throw new ResourceNotFoundException("Pet notfound: " + petId + " for owner" + ownerId);
        }
        return pet;
    }

    @GetMapping
    public ResponseEntity<PetAttribute> getPetAttributes(@PathVariable Integer ownerId, @PathVariable Integer petId) {
        getPetOrThrow(ownerId, petId);
        PetAttribute attributes = petAttributeService.findPetAttributesByPetId(petId);
        if (attributes == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(attributes);
    }

    @PostMapping
    public ResponseEntity<PetAttribute> savePetAttributes(@PathVariable Integer ownerId,
            @PathVariable Integer petId,
            @Valid @RequestBody PetAttribute petAttribute) {
        Pet pet = getPetOrThrow(ownerId, petId);
        boolean isNewPet = petAttributeService.findPetAttributesByPetId(petId) == null;

        petAttribute.setPet(pet);
        PetAttribute saved = petAttributeService.savePetAttributes(ownerId, petId, petAttribute);

        return isNewPet
                ? ResponseEntity.status(HttpStatus.CREATED).body(saved)
                : ResponseEntity.ok(saved);
    }
}
