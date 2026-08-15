package com.example.minibilling.controllers;

import com.example.minibilling.exception.ImportException;
import com.example.minibilling.importer.FileImporter;
import com.example.minibilling.model.domain.ImportResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/import")
@CrossOrigin(origins = "http://localhost:3000")
public class ImportController {

    private final List<FileImporter> importers;

    public ImportController(List<FileImporter> importers){
        this.importers = importers;
    }

    @PostMapping
    public ResponseEntity<ImportResult> importFile(@RequestParam("file") MultipartFile file) {
        String filename = file.getOriginalFilename();

        if (file.isEmpty()) {
            throw new ImportException("Файлът е празен!");
        }

        FileImporter importer = importers.stream()
                .filter(i -> i.supports(filename))
                .findFirst()
                .orElseThrow(() -> new ImportException("Неподдържан файл: " + filename));

        ImportResult result = importer.importFile(file);
        return ResponseEntity.ok(result);
    }
}
