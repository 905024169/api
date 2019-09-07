package com.test.common;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: duanwei
 * @create: 2019-08-28 20:07
 **/
@Component
public class BeanNameConversion implements InitializingBean {
    /**
     * 项目路径
     */
    private String projectPath = "/com/test/";

    @Override
    public void afterPropertiesSet() throws Exception {
        List<File> fileBeanList = new ArrayList<>();
        List<File> fileServiceImplList = new ArrayList<>();
        String path = this.getClass().getResource(projectPath).getPath();
        File filePath = new File(path);
        fileToLine(filePath, fileBeanList, fileServiceImplList);
        for (File file : fileBeanList) {
            String absolutePath = file.getAbsolutePath();
            String info = absolutePath.substring(absolutePath.indexOf("com"), absolutePath.lastIndexOf(file.getName()));
            info = info.replace("\\", ".") + file.getName().replace(".class", "");
            GlobalCache.tableNameBeanMap.put(file.getName().toLowerCase().replace(".class", ""),
                    info);
        }
        for (File file : fileServiceImplList) {
            String absolutePath = file.getName();
            String serviceName = toLowerCaseFirstOne(absolutePath.replace(".class", ""));
            GlobalCache.serviceNameMap.put(serviceName.replace("ServiceImpl", "").toLowerCase().replace(".class", ""),
                    serviceName);
        }


        System.out.println("bean名称："+GlobalCache.tableNameBeanMap.toString());
        System.out.println("service名称:"+GlobalCache.serviceNameMap.toString());


    }

    /**
     * 递归查找bean/service文件
     */
    public static void fileToLine(File dir, List<File> beanList, List<File> serviceImplList) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 递归目录
                    fileToLine(file, beanList, serviceImplList);
                } else {
                    if (file.getParent().endsWith("bean")) {
                        if (file.getName().endsWith(".class")) {
                            beanList.add(file);
                        }
                    }
                    if (file.getParent().endsWith("impl")) {
                        if (file.getName().endsWith(".class")) {
                            serviceImplList.add(file);
                        }
                    }
                }
            }
        }
    }


    //首字母转小写
    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }


    //首字母转大写
    public static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }


}
