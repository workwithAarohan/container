package org.example.controller;

import org.example.abstraction.annotation.*;
import org.example.model.Document;
import org.example.service.DocumentService;


@RestController
@RequestMapping("/documents")
public class DocumentController extends CrudController<Document, Long> {
    public DocumentController(DocumentService service) {
        super(service);
    }
}
