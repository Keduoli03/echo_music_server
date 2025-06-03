package com.lanke.echomusic.service;

import com.lanke.echomusic.dto.user.PasswordUpdateDTO;
import com.lanke.echomusic.dto.user.UserUpdateDTO;
import com.lanke.echomusic.vo.UserVO;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService {
    String login(String usernameOrEmail, String password);
    void logout(String token);

    void updateProfile(Long userId, @Valid UserUpdateDTO dto);

    UserVO getProfile(Long userId);

    void updateUserAvatar(Long userId, MultipartFile avatarFile);

    void updatePassword(Long userId, PasswordUpdateDTO dto);
}