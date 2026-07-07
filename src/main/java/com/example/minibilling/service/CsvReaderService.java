package com.example.minibilling.service;

import com.example.minibilling.model.PricePeriod;
import com.example.minibilling.model.Reading;
import com.example.minibilling.model.User;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvReaderService {

    public List<User> readUsers(String filePath) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(Path.of(filePath), StandardCharsets.UTF_8)){
            List<User> users = new ArrayList<>();
            String line;

            while((line = br.readLine()) != null){
                String[] parts = line.split(",");
                User user = new User(
                        parts[0],
                        parts[1],
                        Integer.parseInt(parts[2].trim())
                );
                users.add(user);
            }

            return users;
        }
    }

    public List<Reading> readReadings(String filePath) throws IOException{
        try (BufferedReader br = Files.newBufferedReader(Path.of(filePath), StandardCharsets.UTF_8)){
            List<Reading> readings = new ArrayList<>();
            String line;

            while((line = br.readLine()) != null){
                String[] parts = line.trim().split("\\s*,\\s*");
                Reading reading = new Reading(
                        parts[0],
                        parts[1],
                        OffsetDateTime.parse(parts[2]),
                        Double.parseDouble(parts[3]
                );
                readings.add(reading);
            }

            return readings;
        }
    }

    public List<PricePeriod> readPrices(String filePath, int priceListNumber) { ... }
}
