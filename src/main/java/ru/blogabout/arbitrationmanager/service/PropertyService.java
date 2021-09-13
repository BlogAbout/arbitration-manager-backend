package ru.blogabout.arbitrationmanager.service;

import ru.blogabout.arbitrationmanager.dto.PageListDto;
import ru.blogabout.arbitrationmanager.entity.Property;
import ru.blogabout.arbitrationmanager.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.blogabout.arbitrationmanager.repository.PropertyRepository;

import java.io.IOException;

@Service
public class PropertyService {
    private final PropertyRepository propertyRepository;
    private final UploaderService uploaderService;

    @Autowired
    public PropertyService(PropertyRepository propertyRepository, UploaderService uploaderService) {
        this.propertyRepository = propertyRepository;
        this.uploaderService = uploaderService;
    }

    public Property create(Property property, MultipartFile file, User user) throws IOException {
        if (file != null && !file.isEmpty()) {
            String fileName = uploaderService.save(file, "property", user.getId());
            property.setImage(fileName);
        }

        return propertyRepository.save(property);
    }

    public Property update(Property propertyFromDb, MultipartFile file, Property property, User user) throws IOException {
        propertyFromDb.setName(property.getName());
        propertyFromDb.setCost(property.getCost());
        propertyFromDb.setImage(property.getImage());
        propertyFromDb.setDescription(property.getDescription());

        if (file != null && !file.isEmpty()) {
            String fileName = uploaderService.save(file, "property", user.getId());
            propertyFromDb.setImage(fileName);

            // Важно - Доделать! Удаление старой картинки
        }

        return propertyRepository.save(propertyFromDb);
    }

    public void delete(Property property) {
        // Важно - Доделать! Удаление картинки
        propertyRepository.delete(property);
    }

    public PageListDto findAll(Pageable pageable) {
        Page<Property> page = propertyRepository.findAll(pageable);

        return new PageListDto(
                page.getContent(),
                pageable.getPageNumber(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}