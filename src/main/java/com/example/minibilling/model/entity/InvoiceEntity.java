package com.example.minibilling.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
public class InvoiceEntity {

    @Id
    private String id;

    @Column(name = "date_time")
    private OffsetDateTime dateTime;

    private String number;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<LineEntity> lines = new ArrayList<>();

    private boolean paid;

    public InvoiceEntity() {}

    public String getId() { return id; }
    public OffsetDateTime getDateTime() { return dateTime; }
    public String getNumber() { return number; }
    public UserEntity getUser() { return user; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public List<LineEntity> getLines() { return lines; }
    public boolean isPaid() { return paid; }
}
