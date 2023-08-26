package com.cryptopos.user.service.user;

import org.springframework.stereotype.Component;

import com.cryptopos.user.repository.UserRepository;

@Component
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository uRepository) {
        this.userRepository = uRepository;
    }

}
