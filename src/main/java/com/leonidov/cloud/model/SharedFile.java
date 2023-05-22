package com.leonidov.cloud.model;

import lombok.*;
import net.bytebuddy.utility.RandomString;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "files", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id"})
})
public class SharedFile {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "path")
    private String path;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public SharedFile(String path, String name, User user) {
        this.id = RandomString.make(9);
        this.path = path;
        this.name = name;
        this.user = user;
    }
}
