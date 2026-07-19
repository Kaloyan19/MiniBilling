package com.example.minibilling.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_reference", columnList = "reference")
})
public class UserEntity {

    @Id
    private String id;

    private String name;

    @Column(unique = true)
    private String reference;

    @Column(name = "price_list")
    private int priceList;

    public UserEntity() {}

    public UserEntity(String id, String name, String reference, int priceList) {
        this.id = id;
        this.name = name;
        this.reference = reference;
        this.priceList = priceList;
    }

    public String getId() {return id;}
    public String getName() {return name;}
    public String getReference() {return reference;}
    public int getPriceList() {return priceList;}

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setReference(String reference) { this.reference = reference; }
    public void setPriceList(int priceList) { this.priceList = priceList; }
}
