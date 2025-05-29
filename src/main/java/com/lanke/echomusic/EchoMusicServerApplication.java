package com.lanke.echomusic;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
@MapperScan("com.lanke.echomusic.mapper")
public class EchoMusicServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EchoMusicServerApplication.class, args);
    }
}
