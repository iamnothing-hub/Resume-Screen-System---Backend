package com.resume.screening.service.impl;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class TikaParsingService {
    private final  AutoDetectParser parser = new AutoDetectParser();

    private final ParseContext context = new ParseContext();

    private final BodyContentHandler handler = new BodyContentHandler(-1);
    // Unlimited size
    public String parse(InputStream inputStream, String mediaType) {
        try (InputStream is = inputStream) {
            Metadata metadata = new Metadata();
            handler.startDocument();    // Doubt here
            parser.parse(is, handler, metadata, context);
            String text = handler.toString();
            return sanitizeText(text);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse resume", e);
        }
    }

    private String sanitizeText(String txt) {
        return txt.replaceAll("\\s{2,}", " ").trim();
    }
}
