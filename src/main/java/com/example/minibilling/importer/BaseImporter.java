package com.example.minibilling.importer;

import com.example.minibilling.exception.ImportException;
import com.example.minibilling.model.domain.ImportError;
import com.example.minibilling.model.domain.ImportResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseImporter implements FileImporter {

    private static final Logger log = LoggerFactory.getLogger(BaseImporter.class);

    protected void logStart(String filename) {
        log.info("Започва импорт на файл: {}", filename);
    }

    protected void logError(ImportError error) {
        if(error.canFix()) {
            log.warn("Ред {}: {} -> {}", error.line(), error.data(), error.error());
        } else {
            log.error("Ред {}: {} -> {}", error.line(), error.data(), error.error());
        }
    }

    protected void logEnd(int success, int failed, String filename) {
        log.info("Импортът завърши: {} успешни, {} неуспешни за файл: {}",
                success, failed, filename);
    }

    @Override
    public ImportResult importFile(MultipartFile file) throws ImportException {
        logStart(file.getOriginalFilename());

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
                    logError(error.get());
                } else {
                    success++;
                }
            }
        } catch (ImportException e) {
            throw e;
        } catch (Exception e) {
            throw new ImportException("Грешка при импорт: " + e.getMessage());
        }

        logEnd(success, failed, file.getOriginalFilename());

        return new ImportResult(success, failed, errors);
    }

    protected abstract Optional<ImportError> processLine(String line, int lineNumber)
            throws ImportException;
}