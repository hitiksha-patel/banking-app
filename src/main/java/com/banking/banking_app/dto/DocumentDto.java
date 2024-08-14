package com.banking.banking_app.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentDto {
    private Long id;
    private String documentType;
    private String filePath;
}
