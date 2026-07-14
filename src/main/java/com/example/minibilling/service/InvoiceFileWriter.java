package com.example.minibilling.service;

import com.example.minibilling.model.Invoice;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

@Service
public class InvoiceFileWriter {

    private final ObjectMapper objectMapper;

    @Value("${billing.output.dir}")
    private String outputDir;

    public InvoiceFileWriter(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    public void write(Invoice invoice, String reference, YearMonth period) throws IOException {
        Path folder = createFolder(invoice.consumer(), reference);
        Path filePath = buildFilePath(folder, period, invoice.documentNumber());
        objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(filePath.toFile(), invoice);
    }

    public Invoice read(String consumer, String reference, YearMonth period) throws IOException {
        Path folder = Path.of(outputDir, consumer + "-" + reference);
        if (!Files.exists(folder)) return null;

        String monthName = period.getMonth()
                .getDisplayName(TextStyle.FULL, new Locale("bg"));
        String year = String.format("%02d", period.getYear() % 100);

        try (var stream = Files.newDirectoryStream(folder, "*-" + monthName + "-" + year + ".json")) {
            for (Path file : stream) {
                return objectMapper.readValue(file.toFile(), Invoice.class);
            }
        }
        return null;
    }

    private Path createFolder(String consumer, String reference) throws IOException {
        Path folder = Path.of(outputDir, consumer + "-" + reference);
        Files.createDirectories(folder);
        return folder;
    }

    private Path buildFilePath(Path folder, YearMonth period, String documentNumber) {
        String monthName = period.getMonth()
                .getDisplayName(TextStyle.FULL, new Locale("bg"));
        String year = String.format("%02d", period.getYear() % 100);
        String fileName = documentNumber + "-" + monthName + "-" + year + ".json";
        return folder.resolve(fileName);
    }

}
