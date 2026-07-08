package com.example.minibilling.reader;

import com.example.minibilling.model.ProductType;
import com.example.minibilling.model.Reading;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ReadingCsvReader implements FileReader<Reading> {

    @Override
    public List<Reading> read(Path path) throws IOException {
        List<Reading> readings = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("\\s*,\\s*");

                if (parts.length != 4) {
                    throw new IllegalArgumentException("Невалиден ред в users.csv: " + line);
                }

                readings.add(new Reading(
                        parts[0],
                        ProductType.valueOf(parts[1].toUpperCase()),
                        OffsetDateTime.parse(parts[2]),
                        Double.parseDouble(parts[3])
                ));
            }
        }
        return readings;
    }
}