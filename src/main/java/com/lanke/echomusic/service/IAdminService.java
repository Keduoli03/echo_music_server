package com.lanke.echomusic.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lanke.echomusic.dto.user.AdminUpdateDTO;
import com.lanke.echomusic.dto.user.PasswordUpdateDTO;
import com.lanke.echomusic.dto.user.UserSearchDTO;
import com.lanke.echomusic.dto.user.UserUpdateDTO;
import com.lanke.echomusic.vo.UserVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public interface IAdminService {
    String login(String usernameOrEmail, String password);
    void logout(String token);

    void updateProfile(Long userId, @Valid UserUpdateDTO dto);

    UserVO getProfile(Long userId);

    void updateUserAvatar(Long userId, MultipartFile avatarFile);

    void updatePassword(Long userId, PasswordUpdateDTO dto);
    IPage<UserVO> getUserList(UserSearchDTO searchDTO);

    void updateUserInfo(Long adminId, Long userId, @Valid AdminUpdateDTO dto);

    void updateUserPassword(Long adminId, Long userId, @NotBlank(message = "新密码不能为空") @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间") String newPassword);
}