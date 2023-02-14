package com.leonidov.cloud.model;

public class File {

    private String name;

    private String isFile;

    private long size;

    private String path;

    private String pathAndFile;

    public File() {
    }

    public File(String name, String isFile, long size, String path, String pathAndFile) {
        this.name = name;
        this.isFile = isFile;
        this.size = size;
        this.path = path;
        this.pathAndFile = pathAndFile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String isFile() {
        return isFile;
    }

    public void setFile(String file) {
        isFile = file;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPathAndFile() {
        return pathAndFile;
    }

    public void setPathAndFile(String pathAndFile) {
        this.pathAndFile = pathAndFile;
    }
}
