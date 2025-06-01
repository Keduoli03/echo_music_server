package com.lanke.echomusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanke.echomusic.dto.user.UserSearchDTO;
import com.lanke.echomusic.entity.User;
import com.lanke.echomusic.mapper.UserMapper;
import com.lanke.echomusic.service.IAdminService;
import com.lanke.echomusic.utils.JwtUtil;
import com.lanke.echomusic.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
        claims.put("role", user.getRole());
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
    public IPage<UserVO> getAllUsers(UserSearchDTO searchDTO) {
        Page<User> page = new Page<>(searchDTO.getPageNum(), searchDTO.getPageSize());
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        // 添加状态过滤条件
        if (searchDTO.getStatus() != null) {
            queryWrapper.eq(User::getStatus, searchDTO.getStatus());
        }

        // 添加用户名模糊查询
        if (StringUtils.hasText(searchDTO.getUsernameLike())) {
            queryWrapper.like(User::getUsername, searchDTO.getUsernameLike());
        }

        // 添加昵称模糊查询
        if (StringUtils.hasText(searchDTO.getNicknameLike())) {
            queryWrapper.like(User::getNickname, searchDTO.getNicknameLike());
        }

        // 添加手机号模糊查询
        if (StringUtils.hasText(searchDTO.getPhoneLike())) {
            queryWrapper.like(User::getPhone, searchDTO.getPhoneLike());
        }

        // 添加邮箱模糊查询
        if (StringUtils.hasText(searchDTO.getEmailLike())) {
            queryWrapper.like(User::getEmail, searchDTO.getEmailLike());
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
    // 转换方法
    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }



}