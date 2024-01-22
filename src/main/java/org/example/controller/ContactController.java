package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.abstraction.annotation.*;
import org.example.model.Contact;
import org.example.service.ContactService;

import java.util.List;

@RequestMapping("/contacts")
@RequiredArgsConstructor
@RestController
public class ContactController {
    private final ContactService service;

    @GetMapping
    public List<Contact> getAllContact() {
        return service.getAllContacts();
    }

    @GetMapping("/{id}")
    public Contact getContactById(@PathVariable("id") Long id) {
        return service.getContactById(id);
    }

    @PostMapping
    public Contact saveContact(@RequestBody Contact contact) {
        return service.saveContact(contact);
    }

    @PutMapping("/{id}")
    public Contact updateContact(@RequestBody Contact contact, @PathVariable("id") Long id) {
        return service.updateContact(contact, id);
    }

    @DeleteMapping("/{id}")
    public void deleteContactById(@PathVariable("id") Long id) {
        service.deleteContactById(id);
    }
}