package org.example.repository;

import lombok.extern.slf4j.Slf4j;
import org.example.abstraction.annotation.Repository;
import org.example.model.Contact;
import org.example.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository
public class ContactRepository {
    List<Contact> contacts = new ArrayList<>();

    public ContactRepository() {
        contacts.add(Contact.builder().id(1L).title("St. Louis").address("Imadol").build());
        contacts.add(Contact.builder().id(2L).title("Cotiviti").address("Hattisar").build());
        contacts.add(Contact.builder().id(3L).title("Project X").address("USA").build());
    }

    public List<Contact> findAllContacts() {
        log.info("Finding all contacts.");
        return contacts;
    }

    public Contact findContactById(Long id) {
        log.info("Finding contact by ID: {}", id);
        return contacts.stream().filter(user -> Objects.equals(user.getId(), id)).findFirst().orElse(null);
    }

    public Contact saveContact(Contact contact) {
        log.info("Adding new contact");
        contacts.add(contact);
        return findContactById(contact.getId());
    }

    public Contact updateContact(Contact updatedContact, Long id) {
        log.info("Updating contact");
        Optional<Contact> contact = contacts.stream()
                .filter(usr -> usr.getId().equals(id))
                .findFirst();

        contact.ifPresent(existingDocument -> {
            existingDocument.setTitle(updatedContact.getTitle());
            existingDocument.setAddress(updatedContact.getAddress());
        });

        if (contact.isEmpty()) {
            log.error("Contact with ID {} not found for update.", id);
        }

        return contact.orElse(null);
    }

    public void deleteContactById(Long id) {
        contacts.removeIf(contact -> contact.getId().equals(id));
    }
}
