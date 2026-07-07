package com.example.minibilling.reader;

import com.example.minibilling.model.User;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserCsvReader implements FileReader<User> {

    @Override
    public List<User> read(Path path) throws IOException {
        List<User> users = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("\\s*,\\s*");
                users.add(new User(parts[0], parts[1], Integer.parseInt(parts[2])));
            }
        }
        return users;
    }
}