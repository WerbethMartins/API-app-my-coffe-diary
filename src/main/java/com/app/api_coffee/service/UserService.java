package com.app.api_coffee.service;

import com.app.api_coffee.repository.CoffeRecordRepository;
import com.app.api_coffee.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CoffeRecordRepository coffeRecordRepository;

    public UserService(UserRepository userRepository, CoffeRecordRepository coffeRecordRepository){
        this.userRepository = userRepository;
        this.coffeRecordRepository = coffeRecordRepository;
    }



}

