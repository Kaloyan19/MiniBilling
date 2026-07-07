package com.example.minibilling.repository;

import com.example.minibilling.model.Reading;
import com.example.minibilling.reader.ReadingCsvReader;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Repository
public class ReadingRepository {

    private final ReadingCsvReader readingCsvReader;
    private List<Reading> readings;

    @Value("${billing.input.dir}")
    private String inputDir;

    public ReadingRepository(ReadingCsvReader readingCsvReader){
        this.readingCsvReader = readingCsvReader;
    }

    @PostConstruct
    public void load() throws IOException{
        readings = readingCsvReader.read(Path.of(inputDir + "readings.csv"));
    }

    public List<Reading> findAll(){
        return readings;
    }

    public List<Reading> findByCustomerReference(String reference) {
        return readings.stream()
                .filter(r -> r.customerReference().equals(reference))
                .toList();
    }
}
