package com.example.minibilling.model.entity;

import com.example.minibilling.model.domain.ProductType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "lines")
public class LineEntity {

    @Id
    private String id;

    @Column(name = "line_id")
    private int lineId;

    private BigDecimal quantity;

    @Column(name = "start_date_time")
    private OffsetDateTime startDateTime;

    @Column(name = "end_date_time")
    private OffsetDateTime endDateTime;

    @Enumerated(EnumType.STRING)
    private ProductType product;

    private BigDecimal price;

    @Column(name = "price_list")
    private int priceList;

    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private InvoiceEntity invoice;

    public LineEntity() {}

    public String getId() { return id; }
    public int getLineId() { return lineId; }
    public BigDecimal getQuantity() { return quantity; }
    public OffsetDateTime getStartDateTime() { return startDateTime; }
    public OffsetDateTime getEndDateTime() { return endDateTime; }
    public ProductType getProduct() { return product; }
    public BigDecimal getPrice() { return price; }
    public int getPriceList() { return priceList; }
    public BigDecimal getAmount() { return amount; }
    public InvoiceEntity getInvoice() { return invoice; }
}
