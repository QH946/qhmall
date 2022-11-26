package com.qh.qhmall.member;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QhmallMemberApplicationTests {

    @Test
    public void contextLoads() {
        //MD5
        String s = DigestUtils.md5Hex("rjlxqh");
        System.out.println(s);
        //盐值加密 $1$+8位字符
        String s1 = Md5Crypt.md5Crypt("rjlxqh".getBytes());
        System.out.println(s1);
        //SpringBoot密码加密器
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String s2 = bCryptPasswordEncoder.encode("rjlxqh");
        boolean matches = bCryptPasswordEncoder.matches("rjlxqh", "$2a$10$LoowOJG7yScBbp/jfw0sNukEPfsouw/wiczhotO60TV.FU4H3OqQC");
        System.out.println(s2);
        System.out.println(matches);
    }


}
