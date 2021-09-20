package ru.blogabout.arbitrationmanager.controller;

import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import ru.blogabout.arbitrationmanager.dto.ERole;
import ru.blogabout.arbitrationmanager.entity.DocumentPassport;
import ru.blogabout.arbitrationmanager.entity.User;
import ru.blogabout.arbitrationmanager.service.DocumentPassportService;
import ru.blogabout.arbitrationmanager.service.UserService;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/documents/passport")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DocumentPassportController {
    private final UserService userService;
    private final DocumentPassportService documentService;

    public DocumentPassportController(UserService userService, DocumentPassportService documentService) {
        this.userService = userService;
        this.documentService = documentService;
    }

    @GetMapping("{username}")
    public ResponseEntity<?> info(@PathVariable("username") String username, @AuthenticationPrincipal User user) {
        try {
            User userFromDb = (User) userService.loadUserByUsername(username);
            if (user.getId().equals(userFromDb.getId()) || user.getRole().contains(ERole.ROLE_ADMIN)) {
                DocumentPassport document = documentService.findByUserId(userFromDb.getId());
                return new ResponseEntity<DocumentPassport>(document, HttpStatus.OK);
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
            @RequestBody DocumentPassport document,
            @AuthenticationPrincipal User user
    ) {
        try {
            try {
                DocumentPassport documentFromDb = documentService.findByUserId(user.getId());
                DocumentPassport updated  = documentService.update(documentFromDb, document);
                return new ResponseEntity<DocumentPassport>(updated, HttpStatus.CREATED);
            } catch (NotFoundException e) {
                document.setUserId(user.getId());
                DocumentPassport updated  = documentService.create(document);
                return new ResponseEntity<DocumentPassport>(updated, HttpStatus.CREATED);
            }
        } catch (IOException e) {
            return new ResponseEntity<String>("Ошибка создания записи.", HttpStatus.ACCEPTED);
        }
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<?> update(
            @PathVariable("id") DocumentPassport documentFromDb,
            @RequestBody DocumentPassport document,
            @AuthenticationPrincipal User user
    ) {
        if (!user.getRole().contains(ERole.ROLE_ADMIN))
            return new ResponseEntity<String>("Ошибка доступа.", HttpStatus.ACCEPTED);

        try {
            DocumentPassport updated  = documentService.update(documentFromDb, document);
            return new ResponseEntity<DocumentPassport>(updated, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<String>("Ошибка обновления записи.", HttpStatus.ACCEPTED);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable("id") DocumentPassport question) {
        documentService.delete(question);
        return new ResponseEntity<String>("Запись удалена.", HttpStatus.NO_CONTENT);
    }
}