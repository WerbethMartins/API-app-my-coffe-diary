package com.app.api_coffee.service;

import com.app.api_coffee.dto.coffee.CoffeeRecordRequestDTO;
import com.app.api_coffee.dto.coffee.CoffeeRecordResponseDTO;
import com.app.api_coffee.enums.DrinkType;
import com.app.api_coffee.model.CoffeeRecord;
import com.app.api_coffee.model.Shop;
import com.app.api_coffee.model.User;
import com.app.api_coffee.repository.CoffeeRecordRepository;
import com.app.api_coffee.repository.ShopRepository;
import com.app.api_coffee.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoffeRecordService {
    private final CoffeeRecordRepository coffeeRecordRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;

    /* Cria um novo registro de café */
    @Transactional
    public CoffeeRecordResponseDTO createRecord(Long userId, CoffeeRecordRequestDTO requestDTO){
        // Busca o usuário e lança uma exceção se não encontrar
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

        CoffeeRecord coffeeRecord = new CoffeeRecord();

        // Mapeamento manual do DTO para entity
        coffeeRecord.setTitle(requestDTO.getTitle());
        coffeeRecord.setTastingNotes(requestDTO.getTastingNotes());
        coffeeRecord.setNotes(String.valueOf(requestDTO.getNotes()));
        coffeeRecord.setPrice(requestDTO.getPrice());
        coffeeRecord.setOrigin(requestDTO.getOrigin());
        coffeeRecord.setImageUrl(requestDTO.getImageUrl());

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
            shop = shopRepository.findById(requestDTO.getShopId())
                    .orElseThrow(() -> new RuntimeException("Loja não encontrada!"));
            coffeeRecord.setShop(shop);
        }else {
            shop = shopRepository.findByNameIgnoreCase(requestDTO.getShopName())
                    .orElseGet(() -> {
                        Shop newShop = new Shop();
                        newShop.setName(requestDTO.getShopName());
                        return shopRepository.save(newShop);
                    });
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
        List<CoffeeRecord> records = coffeeRecordRepository.findByUserIdOrderDataRecordDesc(userId);
        return records.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Busca um registro específico por ID
    @Transactional()
    public CoffeeRecordResponseDTO findById(Long id){
        CoffeeRecord records = coffeeRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de café não encontrado"));
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
        return CoffeeRecordResponseDTO.builder()
                .id(coffeeRecord.getId())
                .title(coffeeRecord.getTitle())
                .tastingNotes(coffeeRecord.getTastingNotes())
                .notes(Integer.valueOf(coffeeRecord.getNotes()))
                .price(coffeeRecord.getPrice())
                .drinkType(coffeeRecord.getDrinkType() != null ? coffeeRecord.getDrinkType().name() : null)
                .origin(coffeeRecord.getOrigin())
                .imageUrl(coffeeRecord.getImageUrl())
                .recordedAt(coffeeRecord.getRecordedAt())
                .userId(coffeeRecord.getUser().getId())
                .username(coffeeRecord.getUser().getUsername())
                .shopId(coffeeRecord.getShop() != null ? coffeeRecord.getShop().getId() : null)
                .shopAddress(coffeeRecord.getShop() != null ? coffeeRecord.getShop().getAddress() : null)
                .build();
    }
}
