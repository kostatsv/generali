package com.generali.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = User.TABLE_NAME,
uniqueConstraints = {@UniqueConstraint(columnNames = "USERNAME")})
public class User extends BaseDomain implements UserDetails {

  protected final static String TABLE_NAME = BaseDomain.PREFIX + "USERS";

  private String username;
  private String password;
  private Boolean enabled;
  private LocalDateTime expiryDate;
  private Role role;

  @Column(name = "ENABLED")
  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  // @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
  @Column(name = "EXPIRY_DATE")
  public LocalDateTime getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(LocalDateTime expiryDate) {
    this.expiryDate = expiryDate;
  }

  @Nationalized
  @Override
  @Column(name = "USERNAME", nullable = false)
  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Nationalized
  @Override
  @Column(name = "PASSWORD", nullable = false)
  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "ROLE_FK", foreignKey = @ForeignKey(name = "FK_USER_TO_ROLE"), nullable = false)
  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  @Transient
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return new ArrayList<>();
  }

  @Transient
  @Override
  public boolean isAccountNonExpired() {
    return this.getExpiryDate() == null || LocalDateTime.now().isBefore(this.getExpiryDate());
  }

  @Transient
  @Override
  public boolean isAccountNonLocked() {
    return this.getEnabled();
  }

  @Transient
  @Override
  public boolean isCredentialsNonExpired() {
    return this.getExpiryDate() == null || LocalDateTime.now().isBefore(this.getExpiryDate());
  }

  @Transient
  @Override
  public boolean isEnabled() {
    return this.getEnabled();
  }

}
