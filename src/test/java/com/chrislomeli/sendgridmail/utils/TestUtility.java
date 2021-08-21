package com.chrislomeli.sendgridmail.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.MediaType;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Scanner;

@Slf4j
public class TestUtility {

    public Pair<String,String> getAttachment(MediaType mediaType) {
        String fileName;
        String payload;
        if (MediaType.APPLICATION_JSON.equals(mediaType)) {
            fileName = "record.json";
            payload = getTextPayload(fileName);
        } else if (MediaType.APPLICATION_PDF.equals(mediaType)) {
            fileName = "ReturnLabel.pdf";
            payload = getBinaryPayload(fileName);
        } else if (MediaType.IMAGE_PNG.equals(mediaType)) {
            fileName = "pie.png";
            payload = getBinaryPayload(fileName);
        } else if (MediaType.TEXT_HTML.equals(mediaType)) {
            fileName = "webpage.html";
            payload = getTextPayload(fileName);
        } else if (MediaType.TEXT_PLAIN.equals(mediaType)) {
            fileName = "lyrics.txt";
            payload = getTextPayload(fileName);
        } else {
            return null;
        }
        return Pair.of(fileName,payload);
    }

    public String getBinaryPayload(String file) {
        String payload = null;
        try {
            URL payload1 = this.getClass().getClassLoader().getResource("files/"+file);
            byte[] attachmentContentBytes = Files.readAllBytes(Paths.get(payload1.getPath()));
            String attachmentContent = Base64.getMimeEncoder().encodeToString(attachmentContentBytes);
            payload = Base64.getEncoder().encodeToString(attachmentContentBytes);
        } catch (IOException e) {
            log.error("File not found. file - {}", file);
        }
        return payload;
    }

    public String getTextPayload(String file) {

        String payload = null;
        try {
            URL p = this.getClass().getClassLoader().getResource("files/"+file);
            if (p==null)
                return null;
            File i = new File(p.getPath());
            String raw = new Scanner(i)
                    .useDelimiter("\\Z").next().trim();
            payload = java.util.Base64.getEncoder().encodeToString(raw.getBytes());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return payload;
    }
}