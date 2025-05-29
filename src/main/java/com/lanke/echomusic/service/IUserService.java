package com.lanke.echomusic.service;

import com.lanke.echomusic.entity.User;

public interface IUserService {
    String login(String usernameOrEmail, String password);
    void logout(String token);
}