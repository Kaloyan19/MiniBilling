package com.example.minibilling.controllers;

import com.example.minibilling.importer.FileImporter;
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
    public ResponseEntity<String> importFile(@RequestParam("file") MultipartFile file) {
        String filename = file.getOriginalFilename();

        FileImporter importer = importers.stream()
                .filter(i -> i.supports(filename))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Неподържан файл " + filename));

        importer.importFile(file);
        return ResponseEntity.ok("Файлът е импортиран успешно: " + filename);
    }
}
