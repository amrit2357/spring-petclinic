/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import org.springframework.samples.petclinic.system.errors.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Wick Dynex
 */
@Controller
@RequestMapping("/owners/{ownerId}")
class PetController {

	private static final String VIEWS_PETS_CREATE_OR_UPDATE_FORM = "pets/createOrUpdatePetForm";

	private final OwnerRepository owners;

	public PetController(OwnerRepository owners) {
		this.owners = owners;
	}

	@ModelAttribute("types")
	public Collection<PetType> populatePetTypes() {
		return this.owners.findPetTypes();
	}

	@ModelAttribute("owner")
	public Owner findOwner(@PathVariable("ownerId") int ownerId) {
		Optional<Owner> optionalOwner = this.owners.findById(ownerId);
		Owner owner = optionalOwner.orElseThrow(() -> new IllegalArgumentException(
				"Owner not found with id: " + ownerId + ". Please ensure the ID is correct "));
		return owner;
	}

	@ModelAttribute("pet")
	public Pet findPet(Owner owner, @PathVariable(name = "petId", required = false) Integer petId) {
		if (petId == null) {
			Pet newPet = new Pet();
			newPet.setAttributes(new PetAttribute()); 
			return newPet;
		} else {
			Pet pet = owner.getPet(petId);
			if (pet == null) {
				throw new ResourceNotFoundException("Pet not found with id: " + petId + " for owner " + owner.getId());
			}
			if (pet.getAttributes() == null) {
				pet.setAttributes(new PetAttribute());
			}
			return pet;
		}
	}

	@InitBinder("owner")
	public void initOwnerBinder(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@InitBinder("pet")
	public void initPetBinder(WebDataBinder dataBinder) {
		dataBinder.setValidator(new PetValidator());
	}

	@GetMapping("/pets/new")
	public String initCreationForm(Owner owner, @ModelAttribute Pet pet, ModelMap model) {
		owner.addPet(pet);
		return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/pets/new")
	public String processCreationForm(Owner owner, @Valid @ModelAttribute("pet") Pet pet, BindingResult result,
			RedirectAttributes redirectAttributes) {
		if (pet.isNew()) {
		    owner.addPet(pet);
		}
		if (StringUtils.hasText(pet.getName()) && pet.isNew() && owner.getPet(pet.getName(), true) != null) {
			Pet existingPetWithSameName = owner.getPet(pet.getName(), true);
			if (existingPetWithSameName != null && existingPetWithSameName.isNew() == pet.isNew()) {
			    result.rejectValue("name", "duplicate", "already exists");
			}
		}
		LocalDate currentDate = LocalDate.now();
		if (pet.getBirthDate() != null && pet.getBirthDate().isAfter(currentDate)) {
			result.rejectValue("birthDate", "typeMismatch.birthDate");
		}
		if (pet.getAttributes() != null) {
            PetAttribute attrs = pet.getAttributes();
            if (StringUtils.hasText(attrs.getTemperament()) || attrs.getLengthCm() != null || attrs.getWeightKg() != null) {
                attrs.setPet(pet);
            } else {
                pet.setAttributes(null);
            }
		}

		if (result.hasErrors()) {
			return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
		}
        
		this.owners.save(owner); 
		redirectAttributes.addFlashAttribute("message", "New Pet has been Added");
		return "redirect:/owners/" + owner.getId();
	}

	@GetMapping("/pets/{petId}/edit")
	public String initUpdateForm() {
		return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/pets/{petId}/edit")
	public String processUpdateForm(@Valid @ModelAttribute("pet") Pet pet, BindingResult result, Owner owner, 
			RedirectAttributes redirectAttributes) {

		String petName = pet.getName();
		if (StringUtils.hasText(petName)) {
			Pet existingPetWithSameName = owner.getPet(petName, false);
			if (existingPetWithSameName != null && !existingPetWithSameName.getId().equals(pet.getId())) {
				result.rejectValue("name", "duplicate", "already exists");
			}
		}
		LocalDate currentDate = LocalDate.now();
		if (pet.getBirthDate() != null && pet.getBirthDate().isAfter(currentDate)) {
			result.rejectValue("birthDate", "typeMismatch.birthDate");
		}

        if (pet.getAttributes() != null) {
            PetAttribute attrs = pet.getAttributes();
            if (StringUtils.hasText(attrs.getTemperament()) || attrs.getLengthCm() != null || attrs.getWeightKg() != null) {
                 attrs.setPet(pet);
            } else {
                pet.setAttributes(null);
            }
        }

		if (result.hasErrors()) {
			return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
		}

		this.owners.save(owner);
		redirectAttributes.addFlashAttribute("message", "Pet details has been edited");
		return "redirect:/owners/" + owner.getId();
	}
}
