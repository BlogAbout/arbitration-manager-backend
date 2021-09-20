package ru.blogabout.arbitrationmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.blogabout.arbitrationmanager.entity.DocumentPassport;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface DocumentPassportRepository extends JpaRepository<DocumentPassport, Long> {
    Optional<DocumentPassport> findById(Long id);

    Optional<DocumentPassport> findByUserId(Long id);

    @Transactional
    void deleteById(Long id);
}