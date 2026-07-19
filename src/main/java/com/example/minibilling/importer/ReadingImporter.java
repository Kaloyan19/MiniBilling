package com.example.minibilling.importer;

import com.example.minibilling.exception.ImportException;
import com.example.minibilling.model.domain.ProductType;
import com.example.minibilling.model.entity.ReadingEntity;
import com.example.minibilling.model.entity.UserEntity;
import com.example.minibilling.repository.jpa.ReadingEntityRepository;
import com.example.minibilling.repository.jpa.UserEntityRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class ReadingImporter implements FileImporter{

    private final ReadingEntityRepository readingEntityRepository;
    private final UserEntityRepository userEntityRepository;

    public ReadingImporter(ReadingEntityRepository readingEntityRepository, UserEntityRepository userEntityRepository) {
        this.readingEntityRepository = readingEntityRepository;
        this.userEntityRepository = userEntityRepository;
    }

    @Override
    public boolean supports(String filename) {
        return "readings.csv".equals(filename);
    }

    @Override
    public void importFile(MultipartFile file) throws ImportException {
        try {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.trim().split("\\s*,\\s*");
                    if (parts.length != 4) {
                        throw new ImportException("Невалиден ред в readings.csv: " + line);
                    }

                    String reference = parts[0];
                    UserEntity user = userEntityRepository.findByReference(reference);
                    if (user == null) continue;

                    ReadingEntity entity = new ReadingEntity();
                    entity.setId(UUID.randomUUID().toString().replace("-", ""));
                    entity.setUser(user);
                    entity.setProduct(ProductType.valueOf(parts[1].toUpperCase()));
                    entity.setDateTime(OffsetDateTime.parse(parts[2]));
                    entity.setLastReading(new BigDecimal(parts[3]));
                    entity.setInvoiced(false);
                    entity.setSelfReported(false);

                    readingEntityRepository.save(entity);
                }
            }
        } catch (Exception e) {
            throw new ImportException("Грешка при импорт: " + e.getMessage());
        }
    }
}
