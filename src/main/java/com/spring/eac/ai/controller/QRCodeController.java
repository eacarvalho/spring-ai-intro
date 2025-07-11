package com.spring.eac.ai.controller;

import com.spring.eac.ai.model.Question;
import com.spring.eac.ai.service.QRCodeAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class QRCodeController {

    private final QRCodeAIService qrCodeAIService;

    @PostMapping(value = "/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] generateQRCode(@RequestBody Question question) {
        return qrCodeAIService.generateQRCode(question);
    }
}
