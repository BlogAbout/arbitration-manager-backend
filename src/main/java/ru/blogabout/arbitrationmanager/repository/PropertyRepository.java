package ru.blogabout.arbitrationmanager.repository;

import ru.blogabout.arbitrationmanager.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
}