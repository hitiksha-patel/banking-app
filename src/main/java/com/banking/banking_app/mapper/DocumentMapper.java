package com.banking.banking_app.mapper;

import com.banking.banking_app.dto.DocumentDto;
import com.banking.banking_app.entity.Document;

public class DocumentMapper {

    public static DocumentDto mapToDocumentDto(Document document) {
        if (document == null) {
            return null;
        }

        return DocumentDto.builder()
                .id(document.getId())
                .documentType(document.getDocumentType())
                .filePath(document.getFilePath())
                .build();
    }

    public static Document mapToDocument(DocumentDto documentDto) {
        if (documentDto == null) {
            return null;
        }

        return Document.builder()
                .id(documentDto.getId())
                .documentType(documentDto.getDocumentType())
                .filePath(documentDto.getFilePath())
                .build();
    }
}
