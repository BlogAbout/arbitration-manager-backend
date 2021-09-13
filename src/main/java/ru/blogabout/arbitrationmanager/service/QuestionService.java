package ru.blogabout.arbitrationmanager.service;

import ru.blogabout.arbitrationmanager.dto.PageListDto;
import ru.blogabout.arbitrationmanager.entity.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.blogabout.arbitrationmanager.repository.QuestionRepository;

import java.io.IOException;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Question create(Question question) throws IOException {
        return questionRepository.save(question);
    }

    public Question update(Question questionFromDb, Question question) throws IOException {
        questionFromDb.setQuestion(question.getQuestion());
        questionFromDb.setAnswer(question.getAnswer());

        return questionRepository.save(questionFromDb);
    }

    public void delete(Question question) {
        questionRepository.delete(question);
    }

    public PageListDto findAll(Pageable pageable) {
        Page<Question> page = questionRepository.findAll(pageable);

        return new PageListDto(
                page.getContent(),
                pageable.getPageNumber(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}