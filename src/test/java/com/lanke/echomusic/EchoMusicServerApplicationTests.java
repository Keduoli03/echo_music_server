package com.lanke.echomusic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EchoMusicServerApplicationTests {

    @Test
    void generateCode() {
        // 直接调用main方法，传入空参数数组
        AutoCodeGenerator.main(new String[]{});
    }

}
