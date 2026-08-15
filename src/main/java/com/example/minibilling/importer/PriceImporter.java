package com.example.minibilling.importer;

import com.example.minibilling.exception.ImportException;
import com.example.minibilling.model.domain.ImportError;
import com.example.minibilling.model.domain.ImportResult;
import com.example.minibilling.model.domain.ProductType;
import com.example.minibilling.model.entity.PriceEntity;
import com.example.minibilling.repository.jpa.PriceEntityRepository;
import com.example.minibilling.validator.ImportValidator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PriceImporter extends BaseImporter {

    private final PriceEntityRepository priceEntityRepository;
    private final ImportValidator importValidator;

    public PriceImporter(PriceEntityRepository priceEntityRepository, ImportValidator importValidator) {
        this.priceEntityRepository = priceEntityRepository;
        this.importValidator = importValidator;
    }

    @Override
    public boolean supports(String filename) {
        return filename != null && filename.matches("prices-\\d+\\.csv");
    }

    @Override
    protected Optional<ImportError> processLine(String line, int lineNumber) throws ImportException {
        throw new ImportException("Използвай importFile() директно за PriceImporter");
    }

    @Transactional
    @Override
    public ImportResult importFile(MultipartFile file) throws ImportException {
        List<ImportError> errors = new ArrayList<>();
        int success = 0;
        int failed = 0;
        int lineNumber = 0;

        String filename = file.getOriginalFilename();
        if (filename == null) throw new ImportException("Името на файла не може да е null");
        int priceListNumber = extractPriceListNumber(filename);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                Optional<ImportError> error = processPriceLine(line, lineNumber, filename, priceListNumber);
                if (error.isPresent()) {
                    failed++;
                    errors.add(error.get());
                } else {
                    success++;
                }
            }
        } catch (IOException e) {
            throw new ImportException("Грешка при четене на файла: " + e.getMessage());
        }

        return new ImportResult(success, failed, errors);
    }

    private Optional<ImportError> processPriceLine(String line, int lineNumber,
                                                   String filename, int priceListNumber) throws ImportException {
        String[] parts = line.trim().split("\\s*,\\s*");
        if (parts.length != 4) {
            throw new ImportException("Невалиден брой колони на ред " + lineNumber + ": " + line);
        }

        try {
            ProductType product = ProductType.valueOf(parts[0].toUpperCase());
            LocalDate startDate = LocalDate.parse(parts[1]);

            if (priceEntityRepository.existsByProductAndStartDateAndPriceList(
                    product, startDate, priceListNumber)) {
                return Optional.of(new ImportError(lineNumber, line,
                        "Цена за " + parts[0] + " от " + parts[1] + " вече съществува!", false));
            }

            PriceEntity entity = buildEntity(parts, priceListNumber);
            importValidator.validatePrice(entity.getStartDate(), entity.getEndDate(), entity.getPrice());
            priceEntityRepository.save(entity);
            return Optional.empty();
        } catch (ImportException e) {
            return Optional.of(new ImportError(lineNumber, line, e.getMessage(), true));
        } catch (Exception e) {
            return Optional.of(new ImportError(lineNumber, line, "Невалидни данни: " + e.getMessage(), true));
        }
    }

    private PriceEntity buildEntity(String[] parts, int priceListNumber) {
        PriceEntity entity = new PriceEntity();
        entity.setId(UUID.randomUUID().toString().replace("-", ""));
        entity.setProduct(ProductType.valueOf(parts[0].toUpperCase()));
        entity.setStartDate(LocalDate.parse(parts[1]));
        entity.setEndDate(LocalDate.parse(parts[2]));
        entity.setPrice(Double.parseDouble(parts[3]));
        entity.setPriceList(priceListNumber);
        return entity;
    }

    private int extractPriceListNumber(String filename) {
        return Integer.parseInt(filename.replace("prices-", "").replace(".csv", ""));
    }
}