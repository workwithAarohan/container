package org.example.repository;

import lombok.extern.slf4j.Slf4j;
import org.example.abstraction.annotation.Repository;
import org.example.model.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository
public class DocumentRepository implements CrudRepository<Document, Long> {
    List<Document> documents = new ArrayList<>();

    public DocumentRepository() {
        documents.add(Document.builder().id(1L).title("Claim Review").file("claim.csv").build());
        documents.add(Document.builder().id(2L).title("Medical Dictionary").file("medical.csv").build());
        documents.add(Document.builder().id(3L).title("Project Cover").file("cover.txt").build());
    }

    @Override
    public List<Document> findAll() {
        log.info("Finding all documents.");
        return documents;
    }

    @Override
    public Document findById(Long id) {
        log.info("Finding document by ID: {}", id);
        return documents.stream().filter(user -> Objects.equals(user.getId(), id)).findFirst().orElse(null);
    }

    @Override
    public Document save(Document document) {
        log.info("Adding new document");
        documents.add(document);
        return findById(document.getId());
    }

    @Override
    public Document update(Document updatedDocument, Long id) {
        log.info("Updating document");
        Optional<Document> document = documents.stream()
                .filter(doc -> doc.getId().equals(id))
                .findFirst();

        document.ifPresent(existingDocument -> {
            existingDocument.setTitle(updatedDocument.getTitle());
            existingDocument.setFile(updatedDocument.getFile());
        });

        if (document.isEmpty()) {
            log.error("Document with ID {} not found for update.", id);
        }

        return document.orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        documents.removeIf(document -> document.getId().equals(id));
    }
}
