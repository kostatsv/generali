package com.generali.domain;

import org.hibernate.annotations.Nationalized;

import javax.persistence.*;

@Entity
@Table(name = Vehicle.TABLE_NAME)
public class Vehicle extends BaseDomain {

    protected static final String TABLE_NAME = BaseDomain.PREFIX + "VEHICLE";

    private String name;
    private String registration;

    private Client client;

    @Nationalized
    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nationalized
    @Column(name = "REGISTRATION")
    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    @ManyToOne
    @JoinColumn(name = "CLIENT_FK", foreignKey = @ForeignKey(name = "FK_CLIENT_TO_VEHICLE"), nullable = false)
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
