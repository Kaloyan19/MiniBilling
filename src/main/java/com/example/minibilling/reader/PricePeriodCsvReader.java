package com.example.minibilling.reader;

import com.example.minibilling.model.PricePeriod;
import com.example.minibilling.model.ProductType;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class PricePeriodCsvReader implements FileReader<PricePeriod> {

    @Override
    public List<PricePeriod> read(Path path) throws IOException {
        List<PricePeriod> prices = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("\\s*,\\s*");

                if (parts.length != 4) {
                    throw new IllegalArgumentException("Невалиден ред в users.csv: " + line);
                }

                prices.add(new PricePeriod(
                        ProductType.valueOf(parts[0].toUpperCase()),
                        LocalDate.parse(parts[1]),
                        LocalDate.parse(parts[2]),
                        Double.parseDouble(parts[3]),
                        0
                ));
            }
        }
        return prices;
    }
}