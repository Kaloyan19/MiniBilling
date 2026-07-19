package com.example.minibilling.importer;

import com.example.minibilling.exception.ImportException;
import com.example.minibilling.model.entity.UserEntity;
import com.example.minibilling.repository.jpa.UserEntityRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class UserImporter implements FileImporter {

    private final UserEntityRepository userEntityRepository;

    public UserImporter(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }

    @Override
    public boolean supports(String filename){
        return "users.csv".equals(filename);
    }

    @Override
    public void importFile(MultipartFile file) throws ImportException {
        try {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.trim().split("\\s*,\\s*");
                    if (parts.length != 3) {
                        throw new ImportException("Невалиден ред в users.csv: " + line);
                    }

                    UserEntity entity = new UserEntity();
                    entity.setId(UUID.randomUUID().toString().replace("-", ""));
                    entity.setName(parts[0]);
                    entity.setReference(parts[1]);
                    entity.setPriceList(Integer.parseInt(parts[2]));

                    userEntityRepository.save(entity);
                }
            }
        } catch (Exception e) {
            throw new ImportException("Грешка при импорт: " + e.getMessage());
        }
    }
}
