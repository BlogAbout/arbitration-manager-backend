package ru.blogabout.arbitrationmanager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.blogabout.arbitrationmanager.dto.ERole;
import ru.blogabout.arbitrationmanager.dto.PageListDto;
import ru.blogabout.arbitrationmanager.entity.Property;
import ru.blogabout.arbitrationmanager.entity.User;
import ru.blogabout.arbitrationmanager.service.PropertyService;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/property")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PropertyController {
    private final int itemsPerPages = 20;
    private final PropertyService propertyService;
    private final ObjectMapper objectMapper;

    @Autowired
    public PropertyController(PropertyService propertyService, ObjectMapper objectMapper) {
        this.propertyService = propertyService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ResponseEntity<PageListDto> list(
            @PageableDefault(size = itemsPerPages, sort = { "id" }, direction = Sort.Direction.ASC) Pageable pageable
    ) {
        PageListDto list = propertyService.findAll(pageable);

        return new ResponseEntity<PageListDto>(list, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<Property> info(@PathVariable("id") Property property) {
        return new ResponseEntity<Property>(property, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody Property property,
            @AuthenticationPrincipal User user
    ) {
        if (!user.getRole().contains(ERole.ROLE_ADMIN))
            return new ResponseEntity<String>("Ошибка доступа.", HttpStatus.ACCEPTED);

        try {
            Property updated = propertyService.create(property, null, user);
            return new ResponseEntity<Property>(updated, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<String>("Ошибка создания записи.", HttpStatus.ACCEPTED);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> create(
            @RequestParam(value = "model") String jsonData,
            @RequestParam(value = "file") MultipartFile file,
            @AuthenticationPrincipal User user
    ) {
        System.out.println(jsonData);
        System.out.println(file);
        if (!user.getRole().contains(ERole.ROLE_ADMIN))
            return new ResponseEntity<String>("Ошибка доступа.", HttpStatus.ACCEPTED);

        try {
            Property property = objectMapper.readValue(jsonData, Property.class);
            Property updated = propertyService.create(property, file, user);
            return new ResponseEntity<Property>(updated, HttpStatus.CREATED);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<String>("Ошибка получения данных.", HttpStatus.ACCEPTED);
        } catch (IOException e) {
            return new ResponseEntity<String>("Ошибка создания записи.", HttpStatus.ACCEPTED);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<?> update(
            @PathVariable("id") Property propertyFromDb,
            @RequestBody Property property,
            @AuthenticationPrincipal User user
    ) {
        if (!user.getRole().contains(ERole.ROLE_ADMIN))
            return new ResponseEntity<String>("Ошибка доступа.", HttpStatus.ACCEPTED);

        try {
            Property updated = propertyService.update(propertyFromDb, null, property, user);
            return new ResponseEntity<Property>(updated, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<String>("Ошибка обновления записи.", HttpStatus.ACCEPTED);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(
            value = "{id}",
            method = RequestMethod.PUT,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> update(
            @PathVariable("id") Property propertyFromDb,
            @RequestParam(value = "model") String jsonData,
            @RequestParam(value = "file") MultipartFile file,
            @AuthenticationPrincipal User user
    ) {
        try {
            Property property = objectMapper.readValue(jsonData, Property.class);
            Property updated = propertyService.update(propertyFromDb, file, property, user);
            return new ResponseEntity<Property>(updated, HttpStatus.OK);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<String>("Ошибка получения данных.", HttpStatus.ACCEPTED);
        } catch (IOException e) {
            return new ResponseEntity<String>("Ошибка обновления записи.", HttpStatus.ACCEPTED);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Property property) {
        propertyService.delete(property);
        return new ResponseEntity<String>("Запись удалена.", HttpStatus.NO_CONTENT);
    }
}