package com.example.minibilling.importer;

import com.example.minibilling.exception.ImportException;
import com.example.minibilling.model.domain.ImportError;
import com.example.minibilling.model.entity.UserEntity;
import com.example.minibilling.repository.jpa.UserEntityRepository;
import com.example.minibilling.validator.ImportValidator;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserImporter extends BaseImporter {

    private final UserEntityRepository userEntityRepository;
    private final ImportValidator importValidator;

    public UserImporter(UserEntityRepository userEntityRepository, ImportValidator importValidator) {
        this.userEntityRepository = userEntityRepository;
        this.importValidator = importValidator;
    }

    @Override
    public boolean supports(String filename) {
        return filename.matches("users.*\\.csv");
    }

    @Override
    protected Optional<ImportError> processLine(String line, int lineNumber) throws ImportException {
        String[] parts = line.trim().split("\\s*,\\s*");
        if (parts.length != 3) {
            throw new ImportException("Невалиден брой колони на ред " + lineNumber + ": " + line);
        }

        if (userEntityRepository.findByReference(parts[1]) != null) {
            return Optional.of(new ImportError(lineNumber, line,
                    "Потребител с референтен номер " + parts[1] + " вече съществува!", false));
        }

        try {
            importValidator.validateUserPriceList(Integer.parseInt(parts[2]));
            UserEntity entity = buildEntity(parts);
            importValidator.validateUser(entity.getName(), entity.getReference(), entity.getPriceList());
            userEntityRepository.save(entity);
            return Optional.empty();
        } catch (ImportException e) {
            return Optional.of(new ImportError(lineNumber, line, e.getMessage(), true));
        }
    }

    private UserEntity buildEntity(String[] parts) {
        UserEntity entity = new UserEntity();
        entity.setId(UUID.randomUUID().toString().replace("-", ""));
        entity.setName(parts[0]);
        entity.setReference(parts[1]);
        entity.setPriceList(Integer.parseInt(parts[2]));
        return entity;
    }
}