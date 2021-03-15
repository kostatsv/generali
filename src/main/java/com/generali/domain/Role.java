package com.generali.domain;

import org.hibernate.annotations.Nationalized;

import javax.persistence.*;

@Entity
@Table(name = Role.TABLE_NAME, uniqueConstraints = @UniqueConstraint(columnNames = "NAME"))
public class Role extends BaseDomain {

    protected final static String TABLE_NAME = BaseDomain.PREFIX + "ROLE";

    private String name;

    public Role() {

    }

    public Role(String name) {
        this.name = name;
    }

    @Nationalized
    @Column(name = "NAME", length = 50, nullable = false, columnDefinition = "nvarchar(50)")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
