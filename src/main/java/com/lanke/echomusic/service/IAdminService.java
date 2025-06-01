package com.lanke.echomusic.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lanke.echomusic.dto.user.UserSearchDTO;
import com.lanke.echomusic.entity.User;
import com.lanke.echomusic.vo.UserVO;

public interface IAdminService {
    String login(String usernameOrEmail, String password);
    void logout(String token);
    IPage<UserVO> getAllUsers(UserSearchDTO searchDTO);
}