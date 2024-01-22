package org.example.service;

import org.example.model.Contact;

import java.util.List;

public interface ContactService {
    List<Contact> getAllContacts();
    Contact getContactById(Long id);
    Contact saveContact(Contact contact);
    Contact updateContact(Contact contact, Long id);
    void deleteContactById(Long Id);
}
