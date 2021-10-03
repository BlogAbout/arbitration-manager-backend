package ru.blogabout.arbitrationmanager.controller;

import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import ru.blogabout.arbitrationmanager.dto.ERole;
import ru.blogabout.arbitrationmanager.entity.DocumentInfo;
import ru.blogabout.arbitrationmanager.entity.DocumentPassport;
import ru.blogabout.arbitrationmanager.entity.User;
import ru.blogabout.arbitrationmanager.service.DocumentInfoService;
import ru.blogabout.arbitrationmanager.service.UserService;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/documents/info")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DocumentInfoController {
    private final UserService userService;
    private final DocumentInfoService documentService;

    public DocumentInfoController(UserService userService, DocumentInfoService documentService) {
        this.userService = userService;
        this.documentService = documentService;
    }

    @GetMapping("{username}/{type}")
    public ResponseEntity<?> info(
            @PathVariable("username") String username,
            @PathVariable("type") String type,
            @AuthenticationPrincipal User user
    ) {
        try {
            User userFromDb = (User) userService.loadUserByUsername(username);
            if (user.getId().equals(userFromDb.getId()) || user.getRole().contains(ERole.ROLE_ADMIN)) {
                DocumentInfo document = documentService.findByUserIdAndType(userFromDb.getId(), type);
                return new ResponseEntity<DocumentInfo>(document, HttpStatus.OK);
            } else {
                return new ResponseEntity<String>("Ошибка доступа.", HttpStatus.ACCEPTED);
            }
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<String>("Ошибка доступа.", HttpStatus.ACCEPTED);
        } catch (NotFoundException e) {
            return new ResponseEntity<String>("Документ не найден.", HttpStatus.ACCEPTED);
        }
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody DocumentInfo document,
            @AuthenticationPrincipal User user
    ) {
        try {
            try {
                DocumentInfo documentFromDb = documentService.findByUserIdAndType(user.getId(), document.getType());
                DocumentInfo updated  = documentService.update(documentFromDb, document);
                return new ResponseEntity<DocumentInfo>(updated, HttpStatus.CREATED);
            } catch (NotFoundException e) {
                document.setUserId(user.getId());
                DocumentInfo updated = documentService.create(document);
                return new ResponseEntity<DocumentInfo>(updated, HttpStatus.CREATED);
            }
        } catch (IOException e) {
            return new ResponseEntity<String>("Ошибка создания записи.", HttpStatus.ACCEPTED);
        }
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<?> update(
            @PathVariable("id") DocumentInfo documentFromDb,
            @RequestBody DocumentInfo document,
            @AuthenticationPrincipal User user
    ) {
        if (!user.getRole().contains(ERole.ROLE_ADMIN))
            return new ResponseEntity<String>("Ошибка доступа.", HttpStatus.ACCEPTED);

        try {
            DocumentInfo updated  = documentService.update(documentFromDb, document);
            return new ResponseEntity<DocumentInfo>(updated, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<String>("Ошибка обновления записи.", HttpStatus.ACCEPTED);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable("id") DocumentInfo question) {
        documentService.delete(question);
        return new ResponseEntity<String>("Запись удалена.", HttpStatus.NO_CONTENT);
    }
}