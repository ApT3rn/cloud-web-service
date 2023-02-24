/*
package com.leonidov.cloud.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@Table(name = "files")
@Entity
public class SharedFile {

    @Id
    @Column(name = "id")
    private String id = UUID.randomUUID().toString().replaceAll("-", "");

    @Column(name = "path")
    private String path;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private User user_id;

    public SharedFile(String path, String name, User user) {
        this.path = path;
        this.name = name;
        this.user_id = user;
    }
}
*/
