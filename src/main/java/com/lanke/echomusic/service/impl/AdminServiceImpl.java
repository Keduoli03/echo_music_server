package com.lanke.echomusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanke.echomusic.dto.user.AdminUpdateDTO;
import com.lanke.echomusic.dto.user.PasswordUpdateDTO;
import com.lanke.echomusic.dto.user.UserSearchDTO;
import com.lanke.echomusic.dto.user.UserUpdateDTO;
import com.lanke.echomusic.entity.User;
import com.lanke.echomusic.mapper.UserMapper;
import com.lanke.echomusic.service.IAdminService;
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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl extends ServiceImpl<UserMapper, User> implements IAdminService {
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
        if (dto.getUsername() != null && !dto.getUsername().isEmpty()) {
            // 检查新用户名是否已存在（排除当前用户）
            if (count(new LambdaQueryWrapper<User>()
                    .eq(User::getUsername, dto.getUsername())
                    .ne(User::getId, userId)) > 0) {
                throw new IllegalArgumentException("用户名已被使用");
            }
            user.setUsername(dto.getUsername());
        }

        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
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

    // 添加实体转VO方法（与AdminServiceImpl保持一致）
    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo); // 自动复制匹配字段
        vo.setRole(user.getRole()); // 手动设置扩展字段（如果有）
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

    @Override
    public IPage<UserVO> getUserList(UserSearchDTO searchDTO) {
        Page<User> page = new Page<>(searchDTO.getCurrent(), searchDTO.getSize());
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        // 添加状态过滤条件
        if (searchDTO.getUserStatus() != null) {
            queryWrapper.eq(User::getStatus, searchDTO.getUserStatus());
        }

        // 添加用户名模糊查询
        if (StringUtils.hasText(searchDTO.getUserName())) {
            queryWrapper.like(User::getUsername, searchDTO.getUserName());
        }

        // 添加昵称模糊查询
        if (StringUtils.hasText(searchDTO.getNickName())) {
            queryWrapper.like(User::getNickname, searchDTO.getNickName());
        }

        // 添加手机号模糊查询
        if (StringUtils.hasText(searchDTO.getUserPhone())) {
            queryWrapper.like(User::getPhone, searchDTO.getUserPhone());
        }

        // 添加邮箱模糊查询
        if (StringUtils.hasText(searchDTO.getUserEmail())) {
            queryWrapper.like(User::getEmail, searchDTO.getUserEmail());
        }

        // 排序逻辑
        if (StringUtils.hasText(searchDTO.getOrderBy())) {
            // 按分号分割多个排序条件
            String[] orderConditions = searchDTO.getOrderBy().split(";");
            for (String condition : orderConditions) {
                condition = condition.trim();
                if (condition.isEmpty()) {
                    continue;
                }
                // 分割每个条件，得到字段和方向
                String[] parts = condition.split(",");
                String field = parts[0].trim();
                boolean isAsc = false; // 默认降序
                if (parts.length >= 2) {
                    String direction = parts[1].trim().toLowerCase();
                    isAsc = "asc".equals(direction);
                }

                // 根据字段名选择排序
                switch (field) {
                    case "username":
                        if (isAsc) {
                            queryWrapper.orderByAsc(User::getUsername);
                        } else {
                            queryWrapper.orderByDesc(User::getUsername);
                        }
                        break;
                    case "nickname":
                        if (isAsc) {
                            queryWrapper.orderByAsc(User::getNickname);
                        } else {
                            queryWrapper.orderByDesc(User::getNickname);
                        }
                        break;
                    case "createdAt":
                        if (isAsc) {
                            queryWrapper.orderByAsc(User::getCreatedAt);
                        } else {
                            queryWrapper.orderByDesc(User::getCreatedAt);
                        }
                        break;
                    case "status":
                        if (isAsc) {
                            queryWrapper.orderByAsc(User::getStatus);
                        } else {
                            queryWrapper.orderByDesc(User::getStatus);
                        }
                        break;
                    // 可以添加更多字段
                    default:
                        // 如果字段不匹配，可以选择忽略或者按默认排序（这里我们忽略，不添加排序）
                        // 也可以按默认字段排序，但注意不要覆盖已有的排序，所以这里不处理
                        break;
                }
            }
        } else {
            // 默认按创建时间降序
            queryWrapper.orderByDesc(User::getCreatedAt);
        }


        // 执行查询
        IPage<User> pageResult = page(page, queryWrapper);

        // 转换为 VO 对象
        IPage<UserVO> voPage = new Page<>(
                pageResult.getCurrent(),
                pageResult.getSize(),
                pageResult.getTotal()
        );

        List<UserVO> voList = pageResult.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }



    // 校验并修改用户信息
    @Override
    @Transactional
    public void updateUserInfo(Long adminId, Long targetUserId, AdminUpdateDTO dto) {
        // 1. 校验管理员权限
        User admin = getUserWithRoleCheck(adminId, "ADMIN");

        // 2. 校验目标用户是否存在
        User targetUser = userMapper.selectById(targetUserId);
        if (targetUser == null) {
            throw new IllegalArgumentException("目标用户不存在");
        }

        // 3. 禁止修改超级管理员（可选）
        if ("ADMIN".equals(targetUser.getRole()) && !"ADMIN".equals(admin.getRole())) {
            throw new IllegalArgumentException("无权限修改管理员");
        }

        // 4. 更新用户信息
        if (dto.getUsername() != null) targetUser.setUsername(dto.getUsername());
        if (dto.getNickname() != null) targetUser.setNickname(dto.getNickname());
        if (dto.getEmail() != null) targetUser.setEmail(dto.getEmail());
        if (dto.getStatus() != null) targetUser.setStatus(dto.getStatus());

        userMapper.updateById(targetUser);

        // 5. 记录操作日志（可选）
//        log.info("管理员 {} 修改用户 {} 信息：{}", adminId, targetUserId, dto);
    }

    // 校验并修改用户密码
    @Override
    @Transactional
    public void updateUserPassword(Long adminId, Long targetUserId, String newPassword) {
        // 1. 校验管理员权限
        getUserWithRoleCheck(adminId, "ADMIN");

        // 2. 校验目标用户是否存在
        User targetUser = userMapper.selectById(targetUserId);
        if (targetUser == null) {
            throw new IllegalArgumentException("目标用户不存在");
        }

        // 3. 禁止修改超级管理员密码（可选）
        if ("ADMIN".equals(targetUser.getRole())) {
            throw new IllegalArgumentException("无权限修改超级管理员密码");
        }

        // 4. 加密并更新密码
        String encodedPassword = passwordEncoder.encode(newPassword);
        targetUser.setPassword(encodedPassword);
        userMapper.updateById(targetUser);

        // 5. 清除用户缓存（如需要）
        redisTemplate.delete("user:" + targetUserId);

        // 6. 记录操作日志（可选）
//        log.info("管理员 {} 修改用户 {} 密码", adminId, targetUserId);
    }

    // 校验用户角色的公共方法
    private User getUserWithRoleCheck(Long userId, String requiredRole) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 检查角色权限
        if (!requiredRole.equals(user.getRole())) {
            throw new IllegalArgumentException("无权限执行此操作");
        }

        return user;
    }


}