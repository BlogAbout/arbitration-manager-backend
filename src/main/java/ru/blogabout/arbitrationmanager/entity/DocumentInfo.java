package ru.blogabout.arbitrationmanager.entity;

import javax.persistence.*;

@Entity
@Table(name = "document_info")
public class DocumentInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String type;

    @Lob
    private String documentInfo;

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDocumentInfo() {
        return documentInfo;
    }

    public void setDocumentInfo(String documentInfo) {
        this.documentInfo = documentInfo;
    }
}