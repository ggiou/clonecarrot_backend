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

  @Column(nullable = true)
  private String profileImgUrl;
//image 별로 뺄려다가 생각해 보니까 id 값 하나 나오는데 각 다른 부분에서 사용하니
//그냥 멤버에 넣는게 맞는거 같아서 넣었습니다~ 마이 페이지에서 어처피 프로필 사진, 닉네임, 비밀 번호 반환해
//줘야 하기도 하고요~

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
