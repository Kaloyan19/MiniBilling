package com.example.minibilling.repository;

import com.example.minibilling.model.User;
import com.example.minibilling.reader.UserCsvReader;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Repository
public class UserRepository {

    private final UserCsvReader userCsvReader;
    private List<User> users;

    @Value("${billing.input.dir}")
    private String inputDir;

    public UserRepository(UserCsvReader userCsvReader) {
        this.userCsvReader = userCsvReader;
    }

    @PostConstruct
    public void load() throws IOException {
        if (!Files.exists(Path.of(inputDir))) {
            throw new RuntimeException("Директорията не съществува: " + inputDir);
        }

        Path filePath = Path.of(inputDir + "users.csv");
        if (!Files.exists(filePath)) {
            throw new RuntimeException("Файлът не съществува: " + filePath);
        }

        users = userCsvReader.read(filePath);

        if (users.isEmpty()) {
            throw new RuntimeException("users.csv е празен!");
        }
    }

    public List<User> findAll() {
        return users;
    }
}