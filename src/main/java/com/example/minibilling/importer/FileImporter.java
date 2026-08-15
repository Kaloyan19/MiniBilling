package com.example.minibilling.importer;

import com.example.minibilling.exception.ImportException;
import com.example.minibilling.model.domain.ImportResult;
import org.springframework.web.multipart.MultipartFile;

public interface FileImporter {
    boolean supports(String filename);
    ImportResult importFile(MultipartFile file) throws ImportException;
}
