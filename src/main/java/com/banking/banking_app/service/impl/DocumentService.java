package com.banking.banking_app.service.impl;

import com.banking.banking_app.entity.Document;
import com.banking.banking_app.entity.User;
import com.banking.banking_app.repository.DocumentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class DocumentService {

    private static final String DOCUMENT_UPLOAD_DIR = "user_documents/";
    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Transactional
    public Document saveDocument(MultipartFile file, User user) {
        try {
            // Ensure the directory exists
            Path resourceDirectory = Paths.get(DOCUMENT_UPLOAD_DIR).toAbsolutePath().normalize();
            if (Files.notExists(resourceDirectory)) {
                Files.createDirectories(resourceDirectory);
            }

            // Save the file
            String documentName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path documentPath = resourceDirectory.resolve(documentName);
            Files.copy(file.getInputStream(), documentPath);

            // Create and save the Document entity
            Document document = Document.builder()
                    .documentType("Uploaded Document") // Adjust type as needed
                    .filePath(documentPath.toString())
                    .user(user)
                    .build();

            return documentRepository.save(document);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}
