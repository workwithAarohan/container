package org.example.service;

import org.example.abstraction.annotation.Service;
import org.example.model.Document;
import org.example.repository.DocumentRepository;

@Service
public class DocumentService extends CrudServiceImpl<Document, Long> {
    public DocumentService(DocumentRepository repository) {
        super(repository);
    }
}