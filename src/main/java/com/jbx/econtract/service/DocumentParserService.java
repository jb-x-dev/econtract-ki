package com.jbx.econtract.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
@Slf4j
public class DocumentParserService {
    
    /**
     * Dateiinhalt basierend auf Dateityp extrahieren
     */
    public String extractText(String filePath, String mimeType) throws IOException {
        log.info("Extrahiere Text aus: {} ({})", filePath, mimeType);
        
        if (mimeType == null) {
            mimeType = guessMimeType(filePath);
        }
        
        if (mimeType.contains("pdf")) {
            return extractFromPDF(filePath);
        } else if (mimeType.contains("word") || mimeType.contains("officedocument")) {
            return extractFromWord(filePath);
        } else if (mimeType.contains("text")) {
            return extractFromText(filePath);
        } else {
            log.warn("Unbekannter Dateityp: {}, versuche als Text zu lesen", mimeType);
            return extractFromText(filePath);
        }
    }
    
    /**
     * Text aus PDF extrahieren
     */
    private String extractFromPDF(String filePath) throws IOException {
        try (PDDocument document = PDDocument.load(new FileInputStream(filePath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            log.info("PDF erfolgreich gelesen: {} Zeichen", text.length());
            return text;
        } catch (Exception e) {
            log.error("Fehler beim Lesen der PDF: {}", e.getMessage());
            throw new IOException("Fehler beim Lesen der PDF: " + e.getMessage(), e);
        }
    }
    
    /**
     * Text aus Word-Dokument extrahieren
     */
    private String extractFromWord(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument document = new XWPFDocument(fis)) {
            
            StringBuilder text = new StringBuilder();
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            
            for (XWPFParagraph paragraph : paragraphs) {
                text.append(paragraph.getText()).append("\n");
            }
            
            String result = text.toString();
            log.info("Word-Dokument erfolgreich gelesen: {} Zeichen", result.length());
            return result;
            
        } catch (Exception e) {
            log.error("Fehler beim Lesen des Word-Dokuments: {}", e.getMessage());
            throw new IOException("Fehler beim Lesen des Word-Dokuments: " + e.getMessage(), e);
        }
    }
    
    /**
     * Text aus Textdatei extrahieren
     */
    private String extractFromText(String filePath) throws IOException {
        try {
            String text = Files.readString(Paths.get(filePath));
            log.info("Textdatei erfolgreich gelesen: {} Zeichen", text.length());
            return text;
        } catch (Exception e) {
            log.error("Fehler beim Lesen der Textdatei: {}", e.getMessage());
            throw new IOException("Fehler beim Lesen der Textdatei: " + e.getMessage(), e);
        }
    }
    
    /**
     * MIME-Type basierend auf Dateiendung erraten
     */
    private String guessMimeType(String filePath) {
        String lowerPath = filePath.toLowerCase();
        
        if (lowerPath.endsWith(".pdf")) {
            return "application/pdf";
        } else if (lowerPath.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else if (lowerPath.endsWith(".doc")) {
            return "application/msword";
        } else if (lowerPath.endsWith(".txt")) {
            return "text/plain";
        } else {
            return "application/octet-stream";
        }
    }
}

