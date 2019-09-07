package com.test.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @program: api
 * @description:
 * @author: duanwei
 * @create: 2019-08-30 12:02
 **/
public class BCryptPasswordUtils {
    public static void main(String[] args) {
       String pwd = "admin";
       BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
       String encodedPassword = passwordEncoder.encode(pwd);
       System.out.println(encodedPassword);
    }


    public static String encode(String pwd) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(pwd);
        return encodedPassword;
    }



}
