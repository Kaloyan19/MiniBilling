package com.example.minibilling.repository;

import com.example.minibilling.model.PricePeriod;
import com.example.minibilling.reader.PricePeriodCsvReader;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PriceRepository {

    private final PricePeriodCsvReader pricePeriodCsvReader;
    private List<PricePeriod> prices;

    @Value("${billing.input.dir}")
    private String inputDir;

    public PriceRepository(PricePeriodCsvReader pricePeriodCsvReader){
        this.pricePeriodCsvReader = pricePeriodCsvReader;
    }

    @PostConstruct
    public void load() throws IOException {
        prices = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(
                Path.of(inputDir), "prices-*.csv")) {
            for (Path path : stream) {
                loadPriceFile(path);
            }
        }
    }

    private void loadPriceFile(Path path) throws IOException {
        int priceListNumber = extractPriceListNumber(path);
        List<PricePeriod> periodList = pricePeriodCsvReader.read(path);
        periodList.forEach(p -> prices.add(
                new PricePeriod(p.product(), p.startDate(), p.endDate(), p.price(), priceListNumber)
        ));
    }

    private int extractPriceListNumber(Path path) {
        String fileName = path.getFileName().toString();
        return Integer.parseInt(fileName.replace("prices-", "").replace(".csv", ""));
    }

    public List<PricePeriod> findByPriceList(int priceListNumber) {
        return prices.stream()
                .filter(p -> p.priceListNumber() == priceListNumber)
                .toList();
    }
}
