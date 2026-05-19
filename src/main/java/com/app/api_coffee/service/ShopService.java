package com.app.api_coffee.service;

import com.app.api_coffee.dto.shop.ShopRequestDTO;
import com.app.api_coffee.dto.shop.ShopResponseDTO;
import com.app.api_coffee.model.Shop;
import com.app.api_coffee.repository.ShopRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;

    /*
    *  Criar ou retornar uma loja existente (Muito útil no fluxo de registro de café)
    * */
    @Transactional
    public Shop findOrCreateShop(ShopRequestDTO requestDTO){
        // Tenta encontrar por nome
        return shopRepository.findByNameIgnoreCase(requestDTO.getName())
                .orElseGet(() -> createNewShop(requestDTO));
    }
    /**
     * Busca uma loja por ID e retorna a Entity (usado internamente)
     */
    @Transactional
    public Shop getShopEntityById(Long id) {
        return shopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + id));
    }


    /*
        Cria uma nova loja
    */
    @Transactional
    public ShopResponseDTO createShop(ShopRequestDTO requestDTO){
        // Verifica se já existe antes de criar
        if(shopRepository.findByNameIgnoreCase(requestDTO.getName()).isPresent()) {
            throw  new RuntimeException("Shop with name " + requestDTO.getName() + " already exists");
        }

        Shop shop = Shop.builder()
                .name(requestDTO.getName())
                .address(requestDTO.getAddress())
                .city(requestDTO.getCity())
                .phone(requestDTO.getPhone())
                .imageUrl(requestDTO.getImageUrl())
                .build();
        Shop saveShop = shopRepository.save(shop);
        return convertToResponseDTO(saveShop);
    }

    /*
    *  Listar todas as lojas (ùtil para o usuário escolher)
    * */
    @Transactional
    public List<ShopResponseDTO> getAllShops() {
        List<Shop> shops = shopRepository.findAll();
        return shops.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /*
    * Busca loja por id
    * */
    @Transactional
    public ShopResponseDTO getShopById(Long id){
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + id));
        return convertToResponseDTO(shop);
    }

    /*
    * Busca loja por cidade
    * */
    @Transactional
    public List<ShopResponseDTO> getShopsByCity(String city){
        List<Shop> shops = shopRepository.findByCityIgnoreCase(city);
        return shops.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /*
    * Busca por nome
    * */
    @Transactional
    public List<ShopResponseDTO> searchShopsByName(String name){
        List<Shop> shops = shopRepository.findByNameContainingIgnoreCase(name);
        return shops.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Conversor Entity -> DTO
    private ShopResponseDTO convertToResponseDTO(Shop shop){
        return ShopResponseDTO.builder()
                .id(shop.getId())
                .name(shop.getName())
                .address(shop.getAddress())
                .description(shop.getDescription())
                .city(shop.getCity())
                .phone(shop.getPhone())
                .imageUrl(shop.getImageUrl())
                .totalCafesRegistrados(shop.getCoffeeRecords() != null ? shop.getCoffeeRecords().size() : null)
                .build();
    }

    // Metodo privado auxiliar
    private Shop createNewShop(ShopRequestDTO requestDTO) {
        Shop shop = Shop.builder()
                .name(requestDTO.getName())
                .address(requestDTO.getAddress())
                .description(requestDTO.getDescription())
                .city(requestDTO.getCity())
                .phone(requestDTO.getPhone())
                .imageUrl(requestDTO.getImageUrl())
                .build();

        return shopRepository.save(shop);
    }

}
