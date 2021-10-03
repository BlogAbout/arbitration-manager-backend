package ru.blogabout.arbitrationmanager.service;

import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.blogabout.arbitrationmanager.entity.DocumentInfo;
import ru.blogabout.arbitrationmanager.repository.DocumentInfoRepository;

import java.io.IOException;

@Service
public class DocumentInfoService {
    private final DocumentInfoRepository documentInfoRepository;

    @Autowired
    public DocumentInfoService(DocumentInfoRepository documentInfoRepository) {
        this.documentInfoRepository = documentInfoRepository;
    }

    public DocumentInfo create(DocumentInfo document) throws IOException {
        return documentInfoRepository.save(document);
    }

    public DocumentInfo update(DocumentInfo documentFromDb, DocumentInfo document) throws IOException {
        documentFromDb.setType(document.getType());
        documentFromDb.setDocumentInfo(document.getDocumentInfo());

        return documentInfoRepository.save(documentFromDb);
    }

    public void delete(DocumentInfo document) {
        documentInfoRepository.delete(document);
    }

    public DocumentInfo findById(Long id) throws NotFoundException {
        return documentInfoRepository.findById(id).orElseThrow(() -> new NotFoundException("Document not found."));
    }

    public DocumentInfo findByUserId(Long userId) throws NotFoundException {
        return documentInfoRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException("Document not found."));
    }

    public DocumentInfo findByUserIdAndType(Long userId, String type) throws NotFoundException {
        return documentInfoRepository.findByUserIdAndType(userId, type).orElseThrow(() -> new NotFoundException("Document not found."));
    }
}