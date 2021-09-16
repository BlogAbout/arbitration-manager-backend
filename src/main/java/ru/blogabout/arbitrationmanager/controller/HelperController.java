package ru.blogabout.arbitrationmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import ru.blogabout.arbitrationmanager.pojo.CallbackRequest;
import ru.blogabout.arbitrationmanager.service.MailService;

import javax.mail.MessagingException;

@RestController
@RequestMapping("/api/v1/helper")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HelperController {
    private final MailService mailService;

    @Value("${spring.mail.from}")
    private String mailAdmin;

    @Autowired
    public HelperController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping("/callback")
    public ResponseEntity<String> callback(@RequestBody CallbackRequest callbackRequest) {
        try {
            Context context = new Context();
            context.setVariable("clientName", callbackRequest.getName());
            context.setVariable("clientPhone", callbackRequest.getPhone());

            mailService.sendMessage(
                    mailAdmin,
                    "Запрос обратного звонка с сайта арбитражного управляющего",
                    "callback",
                    context
            );

            return new ResponseEntity<String>("Заявка на обратный звонок успешно отправлена.", HttpStatus.OK);
        } catch (MessagingException e) {
            return new ResponseEntity<String>("Прозошла ошибка отправки заявки. Попробуйте позже.", HttpStatus.ACCEPTED);
        }
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return new ResponseEntity<String>("Ping success.", HttpStatus.OK);
    }
}