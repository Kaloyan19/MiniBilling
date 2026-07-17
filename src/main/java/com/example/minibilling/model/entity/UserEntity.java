package com.example.minibilling.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    private String id;

    private String name;
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
}
