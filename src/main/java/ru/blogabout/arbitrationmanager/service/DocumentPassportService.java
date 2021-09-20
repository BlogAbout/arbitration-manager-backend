package ru.blogabout.arbitrationmanager.service;

import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.blogabout.arbitrationmanager.entity.DocumentPassport;
import ru.blogabout.arbitrationmanager.entity.User;
import ru.blogabout.arbitrationmanager.repository.DocumentPassportRepository;

import java.io.IOException;

@Service
public class DocumentPassportService {
    private final DocumentPassportRepository documentPassportRepository;

    @Autowired
    public DocumentPassportService(DocumentPassportRepository documentPassportRepository) {
        this.documentPassportRepository = documentPassportRepository;
    }

    public DocumentPassport create(DocumentPassport document) throws IOException {
        return documentPassportRepository.save(document);
    }

    public DocumentPassport update(DocumentPassport documentFromDb, DocumentPassport document) throws IOException {
        documentFromDb.setLastName(document.getLastName());
        documentFromDb.setFirstName(document.getFirstName());
        documentFromDb.setMiddleName(document.getMiddleName());
        documentFromDb.setPrevFio(document.getPrevFio());
        //documentFromDb.setBirthDate(document.getBirthDate());
        documentFromDb.setBirthPlace(document.getBirthPlace());
        documentFromDb.setSnils(document.getSnils());
        documentFromDb.setInn(document.getInn());
        documentFromDb.setDocumentType(document.getDocumentType());
        documentFromDb.setDocumentSeries(document.getDocumentSeries());
        documentFromDb.setSubject(document.getSubject());
        documentFromDb.setArea(document.getArea());
        documentFromDb.setCity(document.getCity());
        documentFromDb.setLocality(document.getLocality());
        documentFromDb.setStreet(document.getStreet());
        documentFromDb.setHouse(document.getHouse());
        documentFromDb.setHull(document.getHull());
        documentFromDb.setApartment(document.getApartment());

        return documentPassportRepository.save(documentFromDb);
    }

    public void delete(DocumentPassport document) {
        documentPassportRepository.delete(document);
    }

    public DocumentPassport findById(Long id) throws NotFoundException {
        return documentPassportRepository.findById(id).orElseThrow(() -> new NotFoundException("Document not found."));
    }

    public DocumentPassport findByUserId(Long userId) throws NotFoundException {
        return documentPassportRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException("Document not found."));
    }
}