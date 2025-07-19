package org.dbt.sda.smart_devops_assistant.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class GitFileMetaDataDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String path;
    private String type;
    private String sha;
    private int size;
    private String url;

    public GitFileMetaDataDTO() {}

    public GitFileMetaDataDTO(String path, String type, String sha, int size, String url) {
        this.path = path;
        this.type = type;
        this.sha = sha;
        this.size = size;
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "GitFileMetaDataDTO{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", type='" + type + '\'' +
                ", sha='" + sha + '\'' +
                ", size=" + size +
                ", url='" + url + '\'' +
                '}';
    }
}
