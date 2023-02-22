package com.leonidov.cloud.model;

import com.leonidov.cloud.enums.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users",
uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id", "email"})
})
public class User {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
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

    public User(String name, String surname, String email, String password, Role role) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
