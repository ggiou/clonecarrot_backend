package com.example.intermediate.domain;

import com.example.intermediate.controller.request.ProfileRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;
import javax.persistence.*;

import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Member extends Timestamped {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String nickname;

  @Column(nullable = false)
  @JsonIgnore
  private String password;


  @Column(unique = true)
  private Long kakaoId;

  @PrePersist 
  public void prePersist(){
    this.location = this.location == null ? "현재 위치가 지정되어있지 않습니다." : this.location;
  }

  @Column(nullable = true)
  private String profileImgUrl;

  @Column
  private String location;

  @PrePersist
  public void prePersist(){
    this.location = this.location == null ? "현재 위치가 지정되어있지 않습니다." : this.location;
  }

  public void update(ProfileRequestDto profileRequestDto, String newPassword, String profileImgUrl){
    this.location = profileRequestDto.getLocation();
    this.password = newPassword;
    this.profileImgUrl = profileImgUrl;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    Member member = (Member) o;
    return id != null && Objects.equals(id, member.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  public boolean validatePassword(PasswordEncoder passwordEncoder, String password) {
    return passwordEncoder.matches(password, this.password);
  }
}
