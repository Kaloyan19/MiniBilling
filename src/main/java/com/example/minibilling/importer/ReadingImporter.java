package com.example.minibilling.importer;

import com.example.minibilling.exception.ImportException;
import com.example.minibilling.model.domain.ImportError;
import com.example.minibilling.model.domain.ProductType;
import com.example.minibilling.model.entity.ReadingEntity;
import com.example.minibilling.model.entity.UserEntity;
import com.example.minibilling.repository.jpa.ReadingEntityRepository;
import com.example.minibilling.repository.jpa.UserEntityRepository;
import com.example.minibilling.validator.ImportValidator;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
public class ReadingImporter extends BaseImporter {

    private final ReadingEntityRepository readingEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final ImportValidator importValidator;

    public ReadingImporter(ReadingEntityRepository readingEntityRepository,
                           UserEntityRepository userEntityRepository,
                           ImportValidator importValidator) {
        this.readingEntityRepository = readingEntityRepository;
        this.userEntityRepository = userEntityRepository;
        this.importValidator = importValidator;
    }

    @Override
    public boolean supports(String filename) {
        return filename.matches("readings.*\\.csv");
    }

    @Override
    protected Optional<ImportError> processLine(String line, int lineNumber) throws ImportException {
        String[] parts = line.trim().split("\\s*,\\s*");
        if (parts.length != 4) {
            throw new ImportException("Невалиден брой колони на ред " + lineNumber + ": " + line);
        }

        try {
            OffsetDateTime dateTime = OffsetDateTime.parse(parts[2]);

            if (readingEntityRepository.existsByUserReferenceAndDateTime(parts[0], dateTime)) {
                return Optional.of(new ImportError(lineNumber, line,
                        "Показание за " + parts[0] + " на " + parts[2] + " вече съществува!", false));
            }

            importValidator.validateReadingUser(parts[0]);
            ReadingEntity entity = buildEntity(parts, dateTime);
            importValidator.validateReading(parts[0], entity.getLastReading());
            readingEntityRepository.save(entity);
            return Optional.empty();
        } catch (ImportException e) {
            return Optional.of(new ImportError(lineNumber, line, e.getMessage(), true));
        } catch (Exception e) {
            return Optional.of(new ImportError(lineNumber, line, "Невалидни данни: " + e.getMessage(), true));
        }
    }

    private ReadingEntity buildEntity(String[] parts, OffsetDateTime dateTime) throws ImportException {
        UserEntity user = userEntityRepository.findByReference(parts[0]);
        ReadingEntity entity = new ReadingEntity();
        entity.setId(UUID.randomUUID().toString().replace("-", ""));
        entity.setUser(user);
        try {
            entity.setProduct(ProductType.valueOf(parts[1].toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new ImportException("Невалиден продукт: " + parts[1]);
        }
        entity.setDateTime(dateTime);
        try {
            entity.setLastReading(new BigDecimal(parts[3]));
        } catch (Exception e) {
            throw new ImportException("Невалидно показание: " + parts[3]);
        }
        entity.setInvoiced(false);
        entity.setSelfReported(false);
        return entity;
    }
}
