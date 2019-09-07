package com.test.base;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.test.annotation.OneToManyBoot;
import com.test.common.Constant;
import com.test.common.GlobalCache;
import com.test.util.NameUtils;
import com.test.util.ReflectUtil;
import com.test.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @description: 通用增删改查父类
 * @author: duanwei
 * @create: 2019-08-28 19:48
 **/
@RestController
@Slf4j
@RequestMapping(value = "/api")
public class BaseController {

    private final String IPAGE = "page";

    private final String IGETBYID = "getById";

    private final String IREMOVEBYID = "removeById";

    private final String IREMOVEBYIDS = "removeByIds";

    private final String ISAVEORUPDATE = "saveOrUpdate";

    private final String IUPDATEBYID = "updateById";

    private final String ILIST = "list";

    @RequestMapping(value = "/{resource}", method = RequestMethod.GET)
    public Object selectAll(HttpServletRequest request, @RequestParam Map<String, String> map,
                            @PathVariable String resource) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        Page result = null;
        String page = map.get(Constant.PAGE);
        String size = map.get(Constant.SIZE);
        if (page == null) {
            page = Constant.PAGE_NUM;
        }
        if (size == null) {
            size = Constant.SIZE_NUM;
        }
        map.remove(Constant.PAGE);
        map.remove(Constant.SIZE);
        QueryWrapper<Object> ew = new QueryWrapper<>();

        String query = request.getQueryString();
        if (query != null) {
            String condition = map.get("condition");
            if (condition != null) {
                condition = condition.substring(1, condition.length() - 1);
                String[] params = condition.split(";");
                List<List<BaseBean>> baseBeanLists = new ArrayList<>();
                //集合对象
                for (String kv : params) {
                    List<BaseBean> baseBeanList = new ArrayList<>();
                    //获取集合对象分割
                    String[] param = kv.split(",");
                    for (String p : param) {
                        BaseBean baseBean = new BaseBean();
                        String name = p.substring(0, p.indexOf("="));
                        String operStart = p.substring(p.indexOf("=") + 1, p.indexOf("=") + 2);
                        String operEnd = p.substring(p.length() - 1, p.length());
                        String value = p.substring(p.indexOf("=") + 2, p.length());
                        if ("=".equals(operStart)) {
                            baseBean.setOper("eq");
                            baseBean.setName(NameUtils.camel2Underline(name));
                            baseBean.setValue(value);
                        }
                        if ("*".equals(operStart)) {
                            baseBean.setOper("likeLeft");
                            baseBean.setName(NameUtils.camel2Underline(name));
                            baseBean.setValue(value);
                        }
                        if ("=".equals(operStart) && "*".equals(operEnd)) {
                            baseBean.setOper("likeRight");
                            baseBean.setName(NameUtils.camel2Underline(name));
                            baseBean.setValue(value.substring(0, value.length() - 1));
                        }
                        if ("*".equals(operStart) && "*".equals(operEnd)) {
                            baseBean.setOper("like");
                            baseBean.setName(NameUtils.camel2Underline(name));
                            baseBean.setValue(value.substring(0, value.length() - 1));
                        }
                        baseBeanList.add(baseBean);
                    }
                    baseBeanLists.add(baseBeanList);
                }
                // sql handler
                for (List<BaseBean> baseBeanList : baseBeanLists) {
                    for (BaseBean baseBean : baseBeanList) {
                        if ("eq".equals(baseBean.getOper())) {
                            ew.eq(baseBean.getName(), baseBean.getValue());
                        }
                        if ("like".equals(baseBean.getOper())) {
                            ew.like(baseBean.getName(), baseBean.getValue());
                        }
                        if ("likeLeft".equals(baseBean.getOper())) {
                            ew.likeLeft(baseBean.getName(), baseBean.getValue());
                        }
                        if ("likeRight".equals(baseBean.getOper())) {
                            ew.likeRight(baseBean.getName(), baseBean.getValue());
                        }
                    }
                    ew.or();
                }
            } else {
                if (map.size() > 0) {
                    for (Map.Entry<String, String> next : map.entrySet()) {
                        String key = next.getKey();
                        if (key.equals("orderAsc") || key.equals("orderDesc") || key.equals("groupBy") || key.equals(
                                "OTM") || key.equals("access_token")) {
                            continue;
                        }
                        String value = next.getValue();
                        ew.eq(NameUtils.camel2Underline(key), value);
                    }
                }
            }
            //特殊处理 orderBy groupBy
            if (map.get("orderAsc") != null) {
                ew.orderByAsc(map.get("orderAsc"));
            }
            if (map.get("orderDesc") != null) {
                ew.orderByDesc(map.get("orderDesc"));
            }
            if (map.get("groupBy") != null) {
                ew.groupBy(map.get("groupBy"));
            }
        }
        String serviceName = GlobalCache.serviceNameMap.get(resource);
        Object obj = SpringUtils.getBean(serviceName);
        try {
            Method method = ReflectUtil.getAccessibleMethod(obj, IPAGE, IPage.class, Wrapper.class);
            Page<Object> pageBean = new Page<>(Integer.parseInt(page), Integer.parseInt(size));
            assert method != null;
            result = (Page) method.invoke(obj, pageBean, ew);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("request url:{} param:{} e:{}", request.getRequestURI(), query, e);
        }

        if (map.get("OTM") != null && map.get("OTM").equals("true")) {
            List records = result.getRecords();
            Object o = records.get(0);
            OneToManyBoot annotation = o.getClass().getAnnotation(OneToManyBoot.class);
            //url对象
            String name = GlobalCache.tableNameBeanMap.get(resource);
            Class sClass = Class.forName(name);
            String tableName = annotation.tableName();
            //关联对象
            String dClassName = GlobalCache.tableNameBeanMap.get(tableName);
            Class dClass = Class.forName(dClassName);
            String refBeanName = GlobalCache.serviceNameMap.get(tableName);
            Object refBean = SpringUtils.getBean(refBeanName);
            //映射关系
            String sName = annotation.fieldName();
            String dName = annotation.refName();
            Object refResult = null;
            if (annotation != null) {
                for (Object record : records) {
                    QueryWrapper<Object> refEw = new QueryWrapper<>();
                    //数据对象
                    Class<?> resultObj = record.getClass();
                    //从数据对象获取要映射属性
                    Field declaredField = resultObj.getDeclaredField(sName);
                    if (declaredField != null) {
                        declaredField.setAccessible(true);
                        //从目标对象获取类型
                        Field refFieldName = dClass.getDeclaredField(dName);
                        if (refFieldName.getType().equals(Integer.class)) {
                            Integer refFileValue = (Integer) declaredField.get(record);
                            refEw.eq(NameUtils.camel2Underline(dName), refFileValue);
                        } else if (refFieldName.getType().equals(Long.class)) {
                            Long refFileValue = (Long) declaredField.get(record);
                            refEw.eq(NameUtils.camel2Underline(dName), refFileValue);
                        } else if (refFieldName.getType().equals(String.class)) {
                            String refFileValue = (String) declaredField.get(record);
                            refEw.eq(NameUtils.camel2Underline(dName), refFileValue);
                        } else {
                            String refFileValue = (String) declaredField.get(record);
                            refEw.eq(NameUtils.camel2Underline(dName), refFileValue);
                        }
                    }
                    try {
                        Method method = ReflectUtil.getAccessibleMethod(refBean, ILIST, Wrapper.class);
                        assert method != null;
                        refResult = method.invoke(refBean, refEw);
                        Field ref = resultObj.getSuperclass().getDeclaredField("ref");
                        if (ref != null) {
                            ref.setAccessible(true);
                            ref.set(record, refResult);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }


    @RequestMapping(value = "/{resource}/{id}", method = RequestMethod.GET)
    public Object selectById(HttpServletRequest request, @PathVariable Long id, @RequestParam Map<String, String> map,
                             @PathVariable String resource) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        String serviceName = GlobalCache.serviceNameMap.get(resource);
        Object obj = SpringUtils.getBean(serviceName);
        Object result = null;
        try {
            Method method = ReflectUtil.getAccessibleMethod(obj, IGETBYID, Serializable.class);
            assert method != null;
            result = method.invoke(obj, id);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("request url:{} param:{} e:{}", request.getRequestURI(), id, e);
        }
        if (map.get("OTM") != null && map.get("OTM").equals("true")) {
            OneToManyBoot annotation = result.getClass().getAnnotation(OneToManyBoot.class);
            //url对象
            String name = GlobalCache.tableNameBeanMap.get(resource);
            Class sClass = Class.forName(name);
            String tableName = annotation.tableName();
            //关联对象
            String dClassName = GlobalCache.tableNameBeanMap.get(tableName);
            Class dClass = Class.forName(dClassName);
            String refBeanName = GlobalCache.serviceNameMap.get(tableName);
            Object refBean = SpringUtils.getBean(refBeanName);
            //映射关系
            String sName = annotation.fieldName();
            String dName = annotation.refName();
            Object refResult = null;
            if (annotation != null) {
                QueryWrapper<Object> refEw = new QueryWrapper<>();
                //数据对象
                Class<?> resultObj = result.getClass();
                //从数据对象获取要映射属性
                Field declaredField = resultObj.getDeclaredField(sName);
                if (declaredField != null) {
                    declaredField.setAccessible(true);
                    //从目标对象获取类型
                    Field refFieldName = dClass.getDeclaredField(dName);
                    if (refFieldName.getType().equals(Integer.class)) {
                        Integer refFileValue = (Integer) declaredField.get(result);
                        refEw.eq(NameUtils.camel2Underline(dName), refFileValue);
                    } else if (refFieldName.getType().equals(Long.class)) {
                        Long refFileValue = (Long) declaredField.get(result);
                        refEw.eq(NameUtils.camel2Underline(dName), refFileValue);
                    } else if (refFieldName.getType().equals(String.class)) {
                        String refFileValue = (String) declaredField.get(result);
                        refEw.eq(NameUtils.camel2Underline(dName), refFileValue);
                    } else {
                        String refFileValue = (String) declaredField.get(result);
                        refEw.eq(NameUtils.camel2Underline(dName), refFileValue);
                    }
                }
                try {
                    Method method = ReflectUtil.getAccessibleMethod(refBean, ILIST, Wrapper.class);
                    assert method != null;
                    refResult = method.invoke(refBean, refEw);
                    Field ref = resultObj.getSuperclass().getDeclaredField("ref");
                    if (ref != null) {
                        ref.setAccessible(true);
                        ref.set(result, refResult);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


    @RequestMapping(value = "/{resource}", method = RequestMethod.POST)
    public Object saveOrUpdate(@PathVariable String resource, HttpServletRequest request,
                               @RequestBody Map<String, String> body) throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException, InstantiationException {
        String name = GlobalCache.tableNameBeanMap.get(resource);
        String serviceName = GlobalCache.serviceNameMap.get(resource);
        Object obj = SpringUtils.getBean(serviceName);
        Class<?> aClass = Class.forName(name);
        Object o = aClass.newInstance();
        for (Map.Entry<String, String> map : body.entrySet()) {
            String key = map.getKey();
            String value = map.getValue();
            Field field = o.getClass().getDeclaredField(key);
            if (field != null) {
                field.setAccessible(true);
                if (field.getType().equals(Integer.class)) {
                    field.set(o, Integer.valueOf(value));
                } else if (field.getType().equals(Long.class)) {
                    field.set(o, Long.valueOf(value));
                } else if (field.getType().equals(String.class)) {
                    field.set(o, String.valueOf(value));
                } else if (field.getType().equals(Float.class)) {
                    field.set(o, Float.valueOf(value));
                } else if (field.getType().equals(Double.class)) {
                    field.set(o, Double.valueOf(value));
                } else {
                    field.set(o, value);
                }
            }
        }
        Object result = null;
        try {
            Method method = ReflectUtil.getAccessibleMethod(obj, ISAVEORUPDATE, Object.class);
            assert method != null;
            result = method.invoke(obj, o);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("request url:{} param:{} e:{}", request.getRequestURI(), o.toString(), e);
        }
        return result;
    }

    @RequestMapping(value = "/{resource}", method = RequestMethod.PUT)
    public Object update(@PathVariable String resource, HttpServletRequest request,
                         @RequestBody Map<String, String> body) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        String name = GlobalCache.tableNameBeanMap.get(resource);
        String serviceName = GlobalCache.serviceNameMap.get(resource);
        Object obj = SpringUtils.getBean(serviceName);
        Class<?> aClass = Class.forName(name);
        Object o = aClass.newInstance();
        for (Map.Entry<String, String> map : body.entrySet()) {
            String key = map.getKey();
            String value = map.getValue();
            Field field = o.getClass().getDeclaredField(key);
            if (field != null) {
                field.setAccessible(true);
                if (field.getType().equals(Integer.class)) {
                    field.set(o, Integer.valueOf(value));
                } else if (field.getType().equals(Long.class)) {
                    field.set(o, Long.valueOf(value));
                } else if (field.getType().equals(String.class)) {
                    field.set(o, String.valueOf(value));
                } else if (field.getType().equals(Float.class)) {
                    field.set(o, Float.valueOf(value));
                } else if (field.getType().equals(Double.class)) {
                    field.set(o, Double.valueOf(value));
                } else {
                    field.set(o, value);
                }
            }
        }
        Object result = null;
        if (ReflectUtil.getAccessibleField(o, "id") == null) {
            log.error("request stop");
            return false;
        } else {
            try {
                Method method = ReflectUtil.getAccessibleMethod(obj, IUPDATEBYID, Object.class);
                assert method != null;
                result = method.invoke(obj, o);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("request url:{} param:{} e:{}", request.getRequestURI(), obj.toString(), e);
            }
        }
        return result;
    }


    @RequestMapping(value = "/{resource}/{id}", method = RequestMethod.DELETE)
    public Object delete(HttpServletRequest request, @PathVariable Long id, @PathVariable String resource) {
        String serviceName = GlobalCache.serviceNameMap.get(resource);
        Object obj = SpringUtils.getBean(serviceName);
        Object result = null;
        try {
            Method method = ReflectUtil.getAccessibleMethod(obj, IREMOVEBYID, Serializable.class);
            assert method != null;
            result = method.invoke(obj, id);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("request url:{} param:{} e:{}", request.getRequestURI(), id, e);
        }
        return result;
    }


    @RequestMapping(value = "/{resource}/batch/{id}", method = RequestMethod.DELETE)
    public Object deletes(HttpServletRequest request, @PathVariable List<Long> id, @PathVariable String resource) {
        String serviceName = GlobalCache.serviceNameMap.get(resource);
        Object obj = SpringUtils.getBean(serviceName);
        Object result = null;
        try {
            Method method = ReflectUtil.getAccessibleMethod(obj, IREMOVEBYIDS, Collection.class);
            assert method != null;
            result = method.invoke(obj, id);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("request url:{} param:{} e:{}", request.getRequestURI(), id, e);
        }
        return result;
    }


}
