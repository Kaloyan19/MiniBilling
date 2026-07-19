package com.example.minibilling.model.entity;

import com.example.minibilling.model.domain.ProductType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "readings", indexes = {
        @Index(name = "idx_readings_user_id", columnList = "user_id"),
        @Index(name = "idx_readings_date_time", columnList = "date_time")
})
public class ReadingEntity {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    private ProductType product;

    @Column(name = "date_time")
    private OffsetDateTime dateTime;

    @Column(name = "last_reading")
    private BigDecimal lastReading;

    private boolean invoiced;

    @Column(name = "self_reported")
    private boolean selfReported;

    public ReadingEntity() {}

    public String getId() { return id; }
    public UserEntity getUser() { return user; }
    public ProductType getProduct() { return product; }
    public OffsetDateTime getDateTime() { return dateTime; }
    public BigDecimal getLastReading() { return lastReading; }
    public boolean isInvoiced() { return invoiced; }
    public boolean isSelfReported() { return selfReported; }

    public void setId(String id) {
        this.id = id;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void setProduct(ProductType product) {
        this.product = product;
    }

    public void setDateTime(OffsetDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setLastReading(BigDecimal lastReading) {
        this.lastReading = lastReading;
    }

    public void setInvoiced(boolean invoiced) {
        this.invoiced = invoiced;
    }

    public void setSelfReported(boolean selfReported) {
        this.selfReported = selfReported;
    }
}
