package ru.blogabout.arbitrationmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.blogabout.arbitrationmanager.dto.ERole;
import ru.blogabout.arbitrationmanager.dto.PageListDto;
import ru.blogabout.arbitrationmanager.entity.Question;
import ru.blogabout.arbitrationmanager.entity.User;
import ru.blogabout.arbitrationmanager.service.QuestionService;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/question")
@CrossOrigin(origins = "*", maxAge = 3600)
public class QuestionController {
    private final int itemsPerPages = 20;
    private final QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public ResponseEntity<PageListDto> list(
            @PageableDefault(size = itemsPerPages, sort = { "id" }, direction = Sort.Direction.ASC) Pageable pageable
    ) {
        PageListDto list = questionService.findAll(pageable);

        return new ResponseEntity<PageListDto>(list, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<Question> info(@PathVariable("id") Question question) {
        return new ResponseEntity<Question>(question, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody Question question,
            @AuthenticationPrincipal User user
    ) {
        if (!user.getRole().contains(ERole.ROLE_ADMIN))
            return new ResponseEntity<String>("Ошибка доступа.", HttpStatus.ACCEPTED);

        try {
            Question updated  = questionService.create(question);
            return new ResponseEntity<Question>(updated, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<String>("Ошибка создания записи.", HttpStatus.ACCEPTED);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<?> update(
            @PathVariable("id") Question questionFromDb,
            @RequestBody Question question,
            @AuthenticationPrincipal User user
    ) {
        if (!user.getRole().contains(ERole.ROLE_ADMIN))
            return new ResponseEntity<String>("Ошибка доступа.", HttpStatus.ACCEPTED);

        try {
            Question updated  = questionService.update(questionFromDb, question);
            return new ResponseEntity<Question>(updated, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<String>("Ошибка обновления записи.", HttpStatus.ACCEPTED);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Question question) {
        questionService.delete(question);
        return new ResponseEntity<String>("Запись удалена.", HttpStatus.NO_CONTENT);
    }
}