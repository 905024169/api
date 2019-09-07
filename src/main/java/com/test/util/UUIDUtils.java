package com.test.util;

import java.util.UUID;

/**
 * @program: api-test
 * @description:
 * @author: duanwei
 * @create: 2019-08-15 12:50
 **/
public class UUIDUtils {
    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }

    public static Integer getUUIDInOrderId(){
        Integer orderId=UUID.randomUUID().toString().hashCode();
        orderId = orderId < 0 ? -orderId : orderId; //String.hashCode() 值会为空
        return orderId;
    }

    public static void main(String[] args){
        for (int i = 0; i<100; i++) {
            System.out.println(UUIDUtils.getUUIDInOrderId());
        }
    }
}
