package com.sparta.jwt.domain.model;


import com.sparta.jwt.domain.RoleEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleEnum authorities;

    @Column(unique = true, nullable = false)
    private String nickname;

    // 기본 권한이 없을 때 USER 권한을 자동 추가하는 메서드
    @PrePersist
    private void addDefaultRole() {
        if (this.authorities == null) {
            this.authorities = RoleEnum.USER; // 기본 권한 설정
        }
    }
}

