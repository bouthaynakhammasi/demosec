package com.aziz.demosec.controller;

import com.aziz.demosec.dto.SpeakRequest;
import com.aziz.demosec.service.VoiceRssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tts")
public class TextToSpeechController {

    @Autowired
    private VoiceRssService voiceRssService;

    @PostMapping(value = "/speak", produces = "audio/mpeg")
    public ResponseEntity<byte[]> speak(@RequestBody SpeakRequest request) {
        byte[] audio = voiceRssService.convertTextToSpeech(request.getText());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
        headers.setContentLength(audio.length);
        // Tell browser to play inline, not download
        headers.set("Content-Disposition", "inline");

        return new ResponseEntity<>(audio, headers, HttpStatus.OK);
    }
}