package com.leonidov.cloud.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class File {
    private String name;
    private String isFile;
    private long size;
    private String path;
    private String pathAndFile;
}
