package com.example.minibilling.model.entity;

import com.example.minibilling.model.domain.ProductType;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "prices")
public class PriceEntity {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private ProductType product;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    private double price;

    @Column(name="price_list")
    private int priceList;

    @ManyToOne
    @JoinColumn(name = "file_import_id")
    private FileImportEntity fileImport;

    public PriceEntity() {}

    public String getId() { return id; }
    public ProductType getProduct() { return product; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public double getPrice() { return price; }
    public int getPriceList() { return priceList; }
    public FileImportEntity getFileImport() { return fileImport; }

}
