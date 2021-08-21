package com.chrislomeli.sendgridmail.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Example endpoint that returns version information about the application.
 * Also, provides a test interface for the actual functionality
 */
@RestController
@RequestMapping("/email/v2")
public class SendgridController {

    final SendgridMethods controllerFacade;

    public SendgridController(SendgridMethods controllerFacade) {
        this.controllerFacade = controllerFacade;
    }

    @PostMapping(path = "/event")
    public ResponseEntity<String> getActivity(@RequestBody String mailRequest)  {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");
        // create
        try {
            controllerFacade.eventActivity(mailRequest);
            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body("Ok");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .headers(responseHeaders)
                    .body(e.getLocalizedMessage());
        }
    }

}
