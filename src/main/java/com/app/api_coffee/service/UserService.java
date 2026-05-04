package com.app.api_coffee.service;

import com.app.api_coffee.repository.CoffeeRecordRepository;
import com.app.api_coffee.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CoffeeRecordRepository coffeRecordRepository;

    public UserService(UserRepository userRepository, CoffeeRecordRepository coffeRecordRepository){
        this.userRepository = userRepository;
        this.coffeRecordRepository = coffeRecordRepository;
    }



}

