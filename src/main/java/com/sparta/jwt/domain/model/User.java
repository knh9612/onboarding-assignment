package com.sparta.jwt.domain.model;


import com.sparta.jwt.domain.RoleEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

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

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ElementCollection(targetClass = RoleEnum.class, fetch = FetchType.EAGER) // 연관관계 매핑 없이 enum 타입 등을 엔티티에 포함할 수 있도록 함.
    @CollectionTable(name = "user_authorities", joinColumns = @JoinColumn(name = "user_id")) // 해당 컬렉션을 저장할 테이블 지정
    @Enumerated(EnumType.STRING)
    private Set<RoleEnum> authorities = new HashSet<>();

    @Column(nullable = false)
    private String nickname;
}
