package com.leonidov.cloud.model;

import com.leonidov.cloud.model.enums.Role;
import com.leonidov.cloud.model.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users",
uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id", "email"})
})
public class User {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    @NotBlank(message = "Поле не заполнено")
    private String name;

    @Column(name = "surname")
    @NotBlank(message = "Поле не заполнено")
    private String surname;

    @Column(name = "email", unique = true)
    @NotBlank(message = "Поле не заполнено")
    @Email(message = "Не верный формат электронной почты")
    private String email;

    @Column(name = "password")
    @NotBlank(message = "Поле не должно быть пустым")
    @Size(min = 8, max = 128, message = "Длина пароля должна быть не менее 8 символов")
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SharedFile> sharedFile;

    public User(String name, String surname, String email, String password) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.role = Role.ROLE_USER;
        this.status = UserStatus.DEFAULT;
        this.sharedFile = new ArrayList<>();
    }
}
