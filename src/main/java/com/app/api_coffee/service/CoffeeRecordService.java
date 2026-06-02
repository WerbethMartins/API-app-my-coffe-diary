package com.app.api_coffee.service;

import com.app.api_coffee.dto.PageResponseDTO;
import com.app.api_coffee.dto.coffee.CoffeeRecordRequestDTO;
import com.app.api_coffee.dto.coffee.CoffeeRecordResponseDTO;
import com.app.api_coffee.dto.shop.ShopRequestDTO;
import com.app.api_coffee.enums.DrinkType;
import com.app.api_coffee.exception.ResourceNotFoundException;
import com.app.api_coffee.model.CoffeeRecord;
import com.app.api_coffee.model.Shop;
import com.app.api_coffee.model.User;
import com.app.api_coffee.repository.CoffeeRecordRepository;
import com.app.api_coffee.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoffeeRecordService {
    private final CoffeeRecordRepository coffeeRecordRepository;
    private final UserRepository userRepository;
    private final ShopService shopService;

    @Value("${app.upload.dir:uploads/coffee-images}")
    private String uploadDir;

    // Lista de cafés com paginação
    @Transactional
    public PageResponseDTO<CoffeeRecordResponseDTO> getRecordsByUserPaginated(Long userId, Pageable pageable){
        Page<CoffeeRecord> page = coffeeRecordRepository.findByUserIdOrderByRecordedAtDesc(userId, pageable);

        List<CoffeeRecordResponseDTO> content = page.getContent().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        return PageResponseDTO.<CoffeeRecordResponseDTO>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isLast(page.isLast())
                .build();
    }

    /* Cria um novo registro de café */
    @Transactional
    public CoffeeRecordResponseDTO createRecord(Long userId, CoffeeRecordRequestDTO requestDTO){
        // Busca o usuário e lança uma exceção se não encontrar
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado!" + userId));

        CoffeeRecord coffeeRecord = new CoffeeRecord();

        // Mapeamento manual do DTO para entity
        coffeeRecord.setTitle(requestDTO.getTitle());
        coffeeRecord.setTastingNotes(requestDTO.getTastingNotes());
        coffeeRecord.setNotes(requestDTO.getNotes());
        coffeeRecord.setRating(requestDTO.getRating());
        coffeeRecord.setPrice(requestDTO.getPrice());
        coffeeRecord.setOrigin(requestDTO.getOrigin());

        if(requestDTO.getImage() != null && !requestDTO.getImage().isEmpty()){
            String imageUrl = saveImage(requestDTO.getImage());
            coffeeRecord.setImageUrl(imageUrl);
        }

        // Converter String para Enum
        if(requestDTO.getDrinkType() != null && !requestDTO.getDrinkType().isBlank()){
            try {
                coffeeRecord.setDrinkType(DrinkType.valueOf(requestDTO.getDrinkType().toUpperCase()));
            }catch (IllegalArgumentException e) {
                coffeeRecord.setDrinkType(DrinkType.OUTRO);
            }
        }

        Shop shop;

        // Associação com a loja se o usuário informou loja id
        if(requestDTO.getShopId() != null) {
            shop = shopService.getShopEntityById(requestDTO.getShopId());
        } else {
            // Cria busca pelo nome
            ShopRequestDTO shopRequest = ShopRequestDTO.builder()
                    .name(requestDTO.getShopName())
                    .address("Endereço a ser atualizado!")
                    .build();
            shop = shopService.findOrCreateShop(shopRequest);
        }

        // Associa a loja oa registro
        coffeeRecord.setShop(shop);

        // Associação com o usuário
        user.addCoffeeRecord(coffeeRecord);

        CoffeeRecord savedRecord = coffeeRecordRepository.save(coffeeRecord);

        return convertToResponseDTO(savedRecord);

    }

    // Lista de todos os registros de um usuário
    @Transactional()
    public List<CoffeeRecordResponseDTO> listByUser(Long userId){
        List<CoffeeRecord> records = coffeeRecordRepository.findByUserIdOrderByRecordedAtDesc(userId);
        return records.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Busca um registro específico por ID
    @Transactional()
    public CoffeeRecordResponseDTO findById(Long id){
        CoffeeRecord records = coffeeRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de café não encontrado" + id));
        return convertToResponseDTO(records);
    }

    // Deletar um registro somente se pertencer ao usuário
    @Transactional
    public void deleteRecord(Long recordId, Long userId) {
        CoffeeRecord record = coffeeRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Você não tem permissão para deletar este registro!"));

        // Segurança: só permite deletar se o registro pertencer ao usuário
        if(!record.getUser().getId().equals(userId)) {
            throw  new RuntimeException("Você não tem permissão para deletar esse registro!");
        }

        coffeeRecordRepository.delete(record);
    }

    // Metodo auxiliar para converter Entiry -> ResponseDTO
    private CoffeeRecordResponseDTO convertToResponseDTO(CoffeeRecord coffeeRecord){
        String imageUrl = coffeeRecord.getImageUrl();
        String fullImageUrl = null;

        if (imageUrl != null && !imageUrl.isEmpty()) {
            fullImageUrl = "http://localhost:8080" + imageUrl;   // Em produção você vai mudar isso
        }

        return CoffeeRecordResponseDTO.builder()
                .id(coffeeRecord.getId())
                .title(coffeeRecord.getTitle())
                .tastingNotes(coffeeRecord.getTastingNotes())
                .notes(coffeeRecord.getNotes())
                .rating(coffeeRecord.getRating())
                .price(coffeeRecord.getPrice())
                .drinkType(coffeeRecord.getDrinkType() != null ? coffeeRecord.getDrinkType().name() : null)
                .origin(coffeeRecord.getOrigin())
                .imageUrl(imageUrl)
                .fullImageUrl(fullImageUrl)
                .recordedAt(coffeeRecord.getRecordedAt())
                .userId(coffeeRecord.getUser().getId())
                .username(coffeeRecord.getUser().getUsername())
                .shopId(coffeeRecord.getShop() != null ? coffeeRecord.getShop().getId() : null)
                .shopAddress(coffeeRecord.getShop() != null ? coffeeRecord.getShop().getAddress() : null)
                .build();
    }

    private String saveImage(MultipartFile file) {
        try {
            // Cria pasta se não existir
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            System.out.println("\uD83D\uDCC1 Salvando imagem em: " + uploadPath.toString());

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Gera nome único
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String newFileName = UUID.randomUUID() + fileExtension;

            Path filePath = uploadPath.resolve(newFileName);
            file.transferTo(filePath.toFile());

            return "/upload/coffee-images/" + newFileName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to save image: " + e.getMessage());
        }
    }

}
