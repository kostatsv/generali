package com.generali.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@MappedSuperclass
public class BaseDomain implements Serializable {

  private Long pk;
  private Integer version;
  private LocalDateTime lastUpdated;

  public static final String PREFIX = "GNRLY_";

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "PK")
  public Long getPk() {
    return pk;
  }

  public void setPk(Long pk) {
    this.pk = pk;
  }

  @Version
  @Column(name = "VERSION", nullable = false)
  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  @Column(name = "LAST_UPDATED")
  public LocalDateTime getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(LocalDateTime lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  @Transient
  public String byteToString(byte[] content) {
    return new String(content, StandardCharsets.UTF_8);
  }

  @Transient
  public byte[] stringToByte(String content) {
    return content.getBytes(StandardCharsets.UTF_8);
  }
}
