package com.lanke.echomusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanke.echomusic.entity.User;
import com.lanke.echomusic.mapper.UserMapper;
import com.lanke.echomusic.service.IUserService;
import com.lanke.echomusic.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate; // 明确泛型为 <String, Object>
    @Autowired
    private JwtUtils jwtUtils;
    @Value("${jwt.expire}")
    private Long expireSeconds;

    @Override
    public String login(String usernameOrEmail, String password) {
        User user = findUserByUsernameOrEmail(usernameOrEmail);
        // 1. 用户不存在或密码错误
        if (user == null) {
            throw new BadCredentialsException("用户名或邮箱不存在"); // 明确异常类型
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("密码错误"); // 明确异常类型
        }

        // 2. 用户被禁用
        if (user.getStatus() == 0) {
            throw new IllegalStateException("用户已被禁用"); // 或自定义异常
        }

        user.setLastActiveAt(LocalDateTime.now());
        updateById(user);

        String token = generateToken(user);
        saveTokenToRedis(token, user);
        return token;
    }

    @Override
    public void logout(String token) {
        if (token != null) {
            redisTemplate.delete("token:" + token);
        }
    }

    private User findUserByUsernameOrEmail(String usernameOrEmail) {
        return usernameOrEmail.contains("@") ?
                baseMapper.selectOne(new QueryWrapper<User>().eq("email", usernameOrEmail)) :
                baseMapper.selectOne(new QueryWrapper<User>().eq("username", usernameOrEmail));
    }

    private String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole());
        return jwtUtils.generateToken(claims);
    }

    private void saveTokenToRedis(String token, User user) {
        redisTemplate.opsForValue().set(
                "token:" + token,
                user.getId().toString(),
                expireSeconds,
                TimeUnit.SECONDS
        );
    }
}