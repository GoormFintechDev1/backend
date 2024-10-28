package com.example.backend.model;
import com.example.backend.model.enumSet.MemberRoleEnum;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account", nullable = false, unique = true)
    private String account; // 아이디

    @Column(name = "password")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "nickname",  unique = true)
    private String nickname; // 추후 랜덤 생성

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "role", nullable = true, length = 30)
    @Enumerated(value = EnumType.STRING)
    private MemberRoleEnum role;


    // Builder를 이용하면 Service에서 체인 형태로 나타낼 수 있어 가독성이 높아짐
    @Builder
    public Member(String account, String password, String name, String nickname, String phoneNumber, String address) {
        this.account = account;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = MemberRoleEnum.USER;
    }

}

