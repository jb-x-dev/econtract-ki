package com.jbx.econtract.controller;

import com.jbx.econtract.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller für Datei-Upload und -Download
 */
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "File Management", description = "Datei-Upload und -Download")
@CrossOrigin(origins = "*")
public class FileController {

    private final FileStorageService fileStorageService;

    /**
     * Lädt Datei für Vertrag hoch
     */
    @PostMapping("/upload/{contractId}")
    @Operation(summary = "Datei hochladen")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @PathVariable Long contractId,
            @RequestParam("file") MultipartFile file) {
        log.info("POST /api/v1/files/upload/{} - {}", contractId, file.getOriginalFilename());
        
        try {
            // Validierung
            fileStorageService.validateFile(file);
            
            // Speichern
            String filePath = fileStorageService.storeFile(file, contractId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("filename", file.getOriginalFilename());
            response.put("filePath", filePath);
            response.put("size", file.getSize());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error uploading file", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Lädt Datei herunter
     */
    @GetMapping("/download")
    @Operation(summary = "Datei herunterladen")
    public ResponseEntity<Resource> downloadFile(@RequestParam String filePath) {
        log.info("GET /api/v1/files/download?filePath={}", filePath);
        
        try {
            byte[] data = fileStorageService.loadFile(filePath);
            ByteArrayResource resource = new ByteArrayResource(data);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + extractFilename(filePath) + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("Error downloading file", e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Löscht Datei
     */
    @DeleteMapping("/delete")
    @Operation(summary = "Datei löschen")
    public ResponseEntity<Map<String, Object>> deleteFile(@RequestParam String filePath) {
        log.info("DELETE /api/v1/files/delete?filePath={}", filePath);
        
        try {
            fileStorageService.deleteFile(filePath);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            log.error("Error deleting file", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Extrahiert Dateinamen aus Pfad
     */
    private String extractFilename(String filePath) {
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }
}

