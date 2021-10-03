package ru.blogabout.arbitrationmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.blogabout.arbitrationmanager.entity.DocumentInfo;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface DocumentInfoRepository extends JpaRepository<DocumentInfo, Long> {
    Optional<DocumentInfo> findById(Long id);

    Optional<DocumentInfo> findByUserId(Long id);

    Optional<DocumentInfo> findByUserIdAndType(Long id, String type);

    @Transactional
    void deleteById(Long id);
}