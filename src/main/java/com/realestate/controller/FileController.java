package com.realestate.controller;

import com.realestate.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "Files", description = "API pour la gestion des fichiers")
public class FileController {

    private final FileStorageService fileStorageService;

    @GetMapping("/{fileName:.+}")
    @Operation(summary = "Télécharger un fichier")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        // Charger le fichier en tant que ressource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Déterminer le type de contenu du fichier
        String contentType = "application/octet-stream";
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Téléverser un fichier",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);
        return ResponseEntity.ok(fileName);
    }
}
