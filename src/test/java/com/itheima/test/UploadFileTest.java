package com.itheima.test;


import org.junit.jupiter.api.Test;

public class UploadFileTest {
    @Test
    public void test1()
    {
        String fileName="ererewe.jpg";
        String substring = fileName.substring(fileName.lastIndexOf("."));
        String[] split = fileName.split("\\.");
        System.out.println(substring);
        System.out.println(split[1]);
    }
}
