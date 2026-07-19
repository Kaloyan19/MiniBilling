package com.example.minibilling.importer;

import com.example.minibilling.exception.ImportException;
import org.springframework.web.multipart.MultipartFile;

public interface FileImporter {
    boolean supports(String filename);
    void importFile(MultipartFile file) throws ImportException;
}
