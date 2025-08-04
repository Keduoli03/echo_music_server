package com.lanke.echomusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanke.echomusic.dto.user.PasswordUpdateDTO;
import com.lanke.echomusic.dto.user.UserUpdateDTO;
import com.lanke.echomusic.entity.User;
import com.lanke.echomusic.mapper.UserMapper;
import com.lanke.echomusic.service.IUserService;
import com.lanke.echomusic.service.MinioService;
import com.lanke.echomusic.utils.JwtUtil;
import com.lanke.echomusic.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private JwtUtil jwtUtil;
    @Value("${jwt.expire}")
    private Long expireSeconds;

    @Autowired
    private MinioService minioService;

    @Autowired
    private UserMapper userMapper;


    @Override
    public String login(String usernameOrEmail, String password) {
        // 1. 查找用户
        User user = findUserByUsernameOrEmail(usernameOrEmail);
        if (user == null) {
            throw new BadCredentialsException("用户名或邮箱不存在");
        }

        // 2. 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("密码错误");
        }

        // 3. 检查用户状态
        if (user.getStatus() == 0) {
            throw new BadCredentialsException("账户已被禁用");
        }

        // 4. 生成并保存 Token
        String token = generateToken(user);
        saveTokenToRedis(token, user);
        
        // 5. 手动记录登录日志
        try {
            // 需要先注入 IOperationLogService
            // operationLogService.saveLog(
            //     "用户认证",
            //     "登录", 
            //     String.format("用户 %s 登录系统", user.getUsername()),
            //     "POST",
            //     "/api/user/login",
            //     String.format("[{\"usernameOrEmail\":\"%s\"}]", usernameOrEmail),
            //     "成功",
            //     null,
            //     0L
            // );
        } catch (Exception e) {
            // 日志记录失败不影响登录流程
            System.err.println("记录登录日志失败: " + e.getMessage());
        }
        
        return token;
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
        return jwtUtil.generateToken(claims);
    }

    private void saveTokenToRedis(String token, User user) {
        redisTemplate.opsForValue().set(
                "token:" + token,
                user.getId().toString(),
                expireSeconds,
                TimeUnit.SECONDS
        );
    }




    @Override
    public void logout(String token) {
        if (token != null) {
            redisTemplate.delete("token:" + token);
        }
    }

    @Override
    @Transactional
    public void updateProfile(Long userId, UserUpdateDTO dto) {
        // 查询用户信息
        User user = getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
    
        // 更新用户信息（只更新DTO中提供的字段）
        if (dto.getUserName() != null && !dto.getUserName().isEmpty()) {  // 改为 getUserName()
            // 检查新用户名是否已存在（排除当前用户）
            if (count(new LambdaQueryWrapper<User>()
                    .eq(User::getUsername, dto.getUserName())  // 改为 getUserName()
                    .ne(User::getId, userId)) > 0) {
                throw new IllegalArgumentException("用户名已被使用");
            }
            user.setUsername(dto.getUserName());  // 改为 getUserName()
        }
    
        if (dto.getNickName() != null) {  // 改为 getNickName()
            user.setNickname(dto.getNickName());  // 改为 getNickName()
        }
    
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            // 检查新邮箱是否已存在（排除当前用户）
            if (count(new LambdaQueryWrapper<User>()
                    .eq(User::getEmail, dto.getEmail())
                    .ne(User::getId, userId)) > 0) {
                throw new IllegalArgumentException("邮箱已被注册");
            }
            user.setEmail(dto.getEmail());
        }
    
        if (dto.getAvatarUrl() != null) {
            user.setAvatarUrl(dto.getAvatarUrl());
        }
    
        if (dto.getIntroduction() != null) {
            user.setIntroduction(dto.getIntroduction());
        }
    
        // 保存更新后的用户信息
        updateById(user);
    }

    @Override
    public UserVO getProfile(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        return convertToVO(user); // 调用转换方法
    }

    // 添加实体转VO方法（手动映射字段）
    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        
        // 手动设置所有字段映射
        vo.setId(user.getId());
        vo.setUserName(user.getUsername());        // username -> userName
        vo.setNickName(user.getNickname());        // nickname -> nickName
        vo.setPhone(user.getPhone());              // phone 保持不变
        vo.setEmail(user.getEmail());              // email 保持不变
        vo.setIntroduction(user.getIntroduction());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setRole(user.getRole());
        vo.setStatus(user.getStatus());
        vo.setCreatedAt(user.getCreatedAt());
        vo.setLastActiveAt(user.getLastActiveAt());
        
        return vo;
    }



    @Override
    @Transactional
    public void updateUserAvatar(Long userId, MultipartFile avatarFile) {
        // 1. 查找用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 2. 保存旧头像URL（用于后续删除）
        String oldAvatarUrl = user.getAvatarUrl();
        // 3. 上传新头像到MinIO（使用"avatars"文件夹）
        String newAvatarUrl = minioService.uploadFile(avatarFile, "avatars");
        // 4. 更新用户头像URL
        user.setAvatarUrl(newAvatarUrl);
        userMapper.updateById(user);
        // 5. 删除旧头像（如果存在）
        if (oldAvatarUrl != null && !oldAvatarUrl.isEmpty()) {
            try {
                minioService.deleteFile(oldAvatarUrl);
            } catch (Exception e) {
                // 记录错误但不中断流程
                System.err.println("删除旧头像失败: " + e.getMessage());
            }
        }
    }

    //修改密码
    @Override
    @Transactional
    public void updatePassword(Long userId, PasswordUpdateDTO dto) {
        // 1. 校验用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在"); // 自定义异常
        }

        // 2. 校验旧密码是否正确
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("当前密码错误");
        }

        // 3. 校验新密码和确认密码是否一致
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("新密码与确认密码不一致");
        }

        // 4. 加密新密码并更新
        String encodedNewPassword = passwordEncoder.encode(dto.getNewPassword());
        user.setPassword(encodedNewPassword);
        userMapper.updateById(user);
    }


}