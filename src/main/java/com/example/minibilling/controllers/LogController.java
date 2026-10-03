package com.example.minibilling.controllers;

import com.example.minibilling.model.domain.LogEntry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/logs")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class LogController {

    @GetMapping
    public ResponseEntity<List<LogEntry>> getLogs() throws IOException {
        Path logFile = Path.of("logs/minibilling.log");

        try (var lines = Files.lines(logFile, StandardCharsets.UTF_8)) {
            List<LogEntry> entries = lines
                    .filter(line -> (line.contains("[WARN]") || line.contains("[ERROR]"))
                            && line.contains("importer"))
                    .map(line -> {
                        String level = line.contains("[WARN]") ? "WARN" : "ERROR";
                        return new LogEntry(line, level);
                    })
                    .toList();
            return ResponseEntity.ok(entries);
        }
    }
}
