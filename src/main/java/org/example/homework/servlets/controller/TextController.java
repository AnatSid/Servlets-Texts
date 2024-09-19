package org.example.homework.servlets.controller;

import org.example.homework.servlets.dto.TextCreateRequest;
import org.example.homework.servlets.dto.TextCreateResponse;
import org.example.homework.servlets.exception.BadRequestException;
import org.example.homework.servlets.model.Text;
import org.example.homework.servlets.service.IdGenerator;
import org.example.homework.servlets.service.TextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController()
@RequestMapping("/texts")
public class TextController {

    private final IdGenerator idGenerator;
    private final TextService textService;

    @Autowired
    public TextController(IdGenerator idGenerator, TextService textService) {
        this.idGenerator = idGenerator;
        this.textService = textService;
    }

    private static final String EMPTY_TEXT_ERROR = "Text cannot be empty";
    private static final String INVALID_REQUEST_PATH = "Invalid request path";


    @GetMapping("/")
    public ResponseEntity<String> getAll(@RequestAttribute(value = "userId") Long userId) {
        return ResponseEntity.ok(textService.getAllTexts(userId).toString());
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getById(@PathVariable("id") String textId, @RequestAttribute(value = "userId") Long userId) {
        Text text = textService.getTextById(userId, parseStringToLong(textId));
        return ResponseEntity.ok("Text with id " + textId + ": " + text.getValue());
    }

    @PostMapping("/*")
    public ResponseEntity<TextCreateResponse> post(@RequestBody TextCreateRequest requestBody, @RequestAttribute(value = "userId") Long userId) {
        String text = requestBody.getText();
        if (text == null || text.trim().isEmpty()) {
            throw new BadRequestException(EMPTY_TEXT_ERROR);
        } else {
            long textId = idGenerator.getNextId();
            textService.addText(userId, new Text(textId, text, userId));

            TextCreateResponse responseBody = new TextCreateResponse();
            responseBody.setTextId(textId);
            responseBody.setText(text);
            responseBody.setMessage("Text saved with id: " + textId);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> handleDelete(@PathVariable("id") String textId, @RequestAttribute(value = "userId") Long userId) {
        textService.deleteTextById(userId, parseStringToLong(textId));
        return ResponseEntity.ok("Text with id: " + textId + " has been deleted");
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deleteAll(@RequestAttribute(value = "userId") Long userId) {
        textService.deleteAll(userId);
        return ResponseEntity.ok("All text has been deleted");

    }

    private Long parseStringToLong(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new BadRequestException(INVALID_REQUEST_PATH + ": " + id);
        }
    }
}

