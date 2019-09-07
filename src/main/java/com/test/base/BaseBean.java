package com.test.base;

/**
 * @program: api
 * @description:
 * @author: duanwei
 * @create: 2019-08-29 17:34
 **/
public class BaseBean {

    private String oper;

    private String name;

    private String value;

    public String getOper() {
        return oper;
    }

    public void setOper(String oper) {
        this.oper = oper;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public BaseBean() {
    }

    public BaseBean(String oper, String name, String value) {
        this.oper = oper;
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return "BeseBean{" +
                "oper='" + oper + '\'' +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
