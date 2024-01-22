package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.abstraction.annotation.Service;
import org.example.model.Contact;
import org.example.repository.ContactRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {
    private final ContactRepository repository;

    @Override
    public List<Contact> getAllContacts() {
        return repository.findAllContacts();
    }

    @Override
    public Contact getContactById(Long id) {
        return repository.findContactById(id);
    }

    @Override
    public Contact saveContact(Contact contact) {
        return repository.saveContact(contact);
    }

    @Override
    public Contact updateContact(Contact contact, Long id) {
        return repository.updateContact(contact, id);
    }

    @Override
    public void deleteContactById(Long id) {
        repository.deleteContactById(id);
    }
}