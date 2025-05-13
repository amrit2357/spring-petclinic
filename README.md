# 🐶 Pet Attributes Feature - Assignment Update

## Summary of Implemented Changes

### 1. Database Enhancements
- **New Table**: Introduced a new table called `pet_attributes` to store additional pet details.
- **Relationship**: One-to-one mapping between `pet_attributes` and `pets`.
- **Cascade Delete**: Deleting a pet also removes its associated attributes automatically.

### 2. Backend Changes

#### 📦 Model Layer
- **`PetAttribute.java`**: New entity to hold pet-specific details such as:
  - temperament
  - length
  - weight
- **`Pet.java`**: Updated to include a reference to `PetAttribute` with appropriate JPA annotations for cascading and orphan removal.

#### 💾 Repository Layer
- **`PetAttributeRepository.java`**: Provides methods for querying and persisting pet attribute data, including fetching by pet ID.

#### ⚙️ Service Layer
- **`PetAttributeService.java`**: Handles logic related to:
  - Retrieving attributes by pet ID
  - Creating or updating attributes
  - Ensuring data integrity with null checks and update logic

#### 🌐 REST API
- **`PetAttributeController.java`**: Exposes endpoints to manage pet attributes via HTTP:
  - `GET /api/owners/{ownerId}/pets/{petId}/attributes` — Retrieve attributes
  - `POST /api/owners/{ownerId}/pets/{petId}/attributes` — Create or update attributes

### 3. Frontend Integration

#### 🐾 Pet Forms (Add/Edit)
- Added fields for:
  - Temperament
  - Length
  - Weigh
- These fields appear in **Add Pet** and **Edit Pet** forms.
- Data is persisted along with the pet entity.
- If these fields are left empty during editing, existing attributes will be removed from the database.


## 🔧 Notes

- **Frontend changes** were implemented with the help of **ChatGPT** to integrate and reflect the new attributes properly.
-  **Function formatting and logic cleanup** utilized **Grok** to ensure maintainable and readable code.
-  **README documentation** was formatted using an online **README formatter** for clarity and presentation.

---