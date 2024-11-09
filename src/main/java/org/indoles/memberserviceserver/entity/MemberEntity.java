package org.indoles.memberserviceserver.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "MEMBER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(unique = true)
    private String signInId;

    @NonNull
    private String password;

    @NonNull
    @Enumerated(value = EnumType.STRING)
    private Role role;

    @NonNull
    private Long point;

    @Builder
    private MemberEntity(
            Long id,
            String signInId,
            String password,
            Role role,
            Long point
    ) {
        this.id = id;
        this.signInId = signInId;
        this.password = password;
        this.role = role;
        this.point = point;
    }
}
