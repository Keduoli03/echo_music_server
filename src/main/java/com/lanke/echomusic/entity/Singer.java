package com.lanke.echomusic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 歌手表
 * </p>
 *
 * @author lanke
 * @since 2025-06-02
 */
@TableName("l_singer")
public class Singer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 歌手ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 歌手名称
     */
    private String name;

    /**
     * 别名（多个用逗号分隔）
     */
    private String alias;

    /**
     * 歌手头像URL
     */
    private String avatar;

    /**
     * 歌手简介
     */
    private String description;

    /**
     * 国籍
     */
    private String nationality;

    /**
     * 出生日期
     */
    private LocalDate birthDate;

    /**
     * 性别（0-未知，1-男，2-女）
     */
    private Byte gender;

    /**
     * 状态（1-启用，0-禁用）
     */
    private Byte status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Byte getGender() {
        return gender;
    }

    public void setGender(Byte gender) {
        this.gender = gender;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Singer{" +
            "id = " + id +
            ", name = " + name +
            ", alias = " + alias +
            ", avatar = " + avatar +
            ", description = " + description +
            ", nationality = " + nationality +
            ", birthDate = " + birthDate +
            ", gender = " + gender +
            ", createdAt = " + createdAt +
            ", updatedAt = " + updatedAt +
            "}";
    }
}
