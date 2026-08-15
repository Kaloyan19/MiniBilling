package com.example.minibilling.importer;

import com.example.minibilling.exception.ImportException;
import com.example.minibilling.model.domain.ImportError;
import com.example.minibilling.model.domain.ImportResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BaseImporter implements FileImporter {

    @Override
    public ImportResult importFile(MultipartFile file) throws ImportException {
        List<ImportError> errors = new ArrayList<>();
        int success = 0;
        int failed = 0;
        int lineNumber = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                Optional<ImportError> error = processLine(line, lineNumber);
                if (error.isPresent()) {
                    failed++;
                    errors.add(error.get());
                } else {
                    success++;
                }
            }
        } catch (ImportException e) {
            throw e;
        } catch (Exception e) {
            throw new ImportException("Грешка при импорт: " + e.getMessage());
        }

        return new ImportResult(success, failed, errors);
    }

    protected abstract Optional<ImportError> processLine(String line, int lineNumber)
            throws ImportException;
}