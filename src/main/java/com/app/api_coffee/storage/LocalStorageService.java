package com.app.api_coffee.storage;

import com.app.api_coffee.storage.FileStorageService;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalStorageService implements FileStorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        try {

            validateImageFile(file);

            // Cria o diretório base + pasta específica
            Path basePath = Paths.get(uploadDir, folder).toAbsolutePath().normalize();
            if (!Files.exists(basePath)) {
                Files.createDirectories(basePath);
            }

            if(file == null || file.isEmpty()){
                System.out.println("⚠\uFE0F Nenhum arquivo enviando para upload!");
                return null;
            }

            String originalName = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
            String newFileName = UUID.randomUUID() + extension;

            Path filePath = basePath.resolve(newFileName);

            // ==================== COMPRESSÃO ====================
            compressAndSaveImage(file, filePath);

            System.out.println("Imagem comprimida e salva!" + newFileName);

            file.transferTo(filePath.toFile());

            return "/uploads/" + folder + "/" + newFileName;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar arquivo localmente: " + e.getMessage());
        }
    }

    // Metodo de compressão
    private void compressAndSaveImage(MultipartFile file, Path destinationPath) throws IOException {
        // Qualidade recomendada: 0.75 ~ 0.85 (bom equilíbrio)
        Thumbnails.of(file.getInputStream())
                .size(1200, 1200)           // Limita a largura máxima (mantém proporção)
                .outputQuality(0.82)        // Qualidade da compressão
                .toFile(destinationPath.toFile());
    }

    // Validação
    private void validateImageFile(MultipartFile file) {
        long maxSize = 10 * 1024 * 1024; // 10MB antes da compressão
        if (file.getSize() > maxSize) {
            throw new RuntimeException("Arquivo muito grande. Máximo: 10MB");
        }

        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.startsWith("image/jpeg") &&
                        !contentType.startsWith("image/png") &&
                        !contentType.startsWith("image/webp"))) {
            throw new RuntimeException("Tipo de arquivo não permitido.");
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        // Implementação futura
    }

    @Override
    public String getStorageType() {
        return "local";
    }
}
