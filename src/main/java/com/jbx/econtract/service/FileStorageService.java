package com.jbx.econtract.service;

import lombok.extern.slf4j.Slf4j;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Service für Datei-Upload und -Verwaltung
 */
@Service
@Slf4j
public class FileStorageService {

    @Value("${file.upload-dir:/var/econtract/uploads}")
    private String uploadDir;

    /**
     * Speichert hochgeladene Datei
     */
    public String storeFile(MultipartFile file, Long contractId) throws IOException {
        log.info("Storing file: {} for contract: {}", file.getOriginalFilename(), contractId);
        
        // Upload-Verzeichnis erstellen falls nicht vorhanden
        Path uploadPath = Paths.get(uploadDir, "contracts", String.valueOf(contractId));
        Files.createDirectories(uploadPath);
        
        // Eindeutigen Dateinamen generieren
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : "";
        String filename = UUID.randomUUID().toString() + extension;
        
        // Datei speichern
        Path targetPath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        log.info("File stored successfully: {}", targetPath);
        return targetPath.toString();
    }

    /**
     * Löscht Datei
     */
    public void deleteFile(String filePath) throws IOException {
        log.info("Deleting file: {}", filePath);
        Path path = Paths.get(filePath);
        Files.deleteIfExists(path);
    }

    /**
     * Lädt Datei
     */
    public byte[] loadFile(String filePath) throws IOException {
        log.info("Loading file: {}", filePath);
        Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    }

    /**
     * Prüft ob Datei existiert
     */
    public boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    /**
     * Validiert Datei-Upload
     */
    public void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Datei ist leer");
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null || filename.contains("..")) {
            throw new RuntimeException("Ungültiger Dateiname");
        }
        
        // Dateigröße prüfen (10 MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("Datei ist zu groß (max. 10 MB)");
        }
        
        // Dateiendung prüfen
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        if (!isAllowedExtension(extension)) {
            throw new RuntimeException("Dateityp nicht erlaubt: " + extension);
        }
    }

    /**
     * Prüft erlaubte Dateiendungen
     */
    private boolean isAllowedExtension(String extension) {
        return List.of("pdf", "doc", "docx", "txt", "jpg", "jpeg", "png").contains(extension);
    }
}

