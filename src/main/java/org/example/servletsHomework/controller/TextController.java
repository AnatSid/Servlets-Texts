package org.example.servletsHomework.controller;

import org.example.servletsHomework.dto.TextCreateRequest;
import org.example.servletsHomework.dto.TextCreateResponse;
import org.example.servletsHomework.exception.BadRequestException;
import org.example.servletsHomework.exception.InternalServerErrorException;
import org.example.servletsHomework.exception.NotFoundException;
import org.example.servletsHomework.model.Text;
import org.example.servletsHomework.service.IdGenerator;
import org.example.servletsHomework.service.TextService;
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

    private static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    private static final String EMPTY_TEXT_ERROR = "Text cannot be empty";
    private static final String INVALID_REQUEST_PATH = "Invalid request path";


    @GetMapping("/")
    public ResponseEntity<String> getAll(@RequestAttribute(value = "userId") Long userId) {
        try {
            return ResponseEntity.ok(textService.getAllTexts(userId).toString());
        } catch (Exception ex) {
            throw new InternalServerErrorException(INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getById(@PathVariable("id") String textId, @RequestAttribute(value = "userId") Long userId) {
        try {
            Text text = textService.getTextById(userId, parseStringToLong(textId));
            return ResponseEntity.ok("Text with id " + textId + ": " + text.getValue());

        } catch (NotFoundException notFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundException.getMessage());
        } catch (BadRequestException badRequestException) {
            return ResponseEntity.badRequest().body(badRequestException.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error occurred");
        }
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
        try {
            textService.deleteTextById(userId, parseStringToLong(textId));
            return ResponseEntity.ok("Text with id: " + textId + " has been deleted");
        } catch (BadRequestException badRequestException) {
            throw badRequestException;
        } catch (Exception ex) {
            throw new InternalServerErrorException(INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deleteAll(@RequestAttribute(value = "userId") Long userId) {
        try {
            textService.deleteAll(userId);
            return ResponseEntity.ok("All text has been deleted");
        } catch (Exception ex) {
            throw new InternalServerErrorException(INTERNAL_SERVER_ERROR);
        }
    }

    private Long parseStringToLong(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new BadRequestException(INVALID_REQUEST_PATH + ": " + id);
        }
    }
}

