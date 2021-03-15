package com.generali.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = Company.TABLE_NAME)
public class Company extends BaseDomain {

    protected final static String TABLE_NAME = BaseDomain.PREFIX + "COMPANY";

}
