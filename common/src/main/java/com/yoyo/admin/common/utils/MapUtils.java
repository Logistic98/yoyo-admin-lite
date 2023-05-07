package com.yoyo.admin.common.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Maps;
import org.springframework.cglib.beans.BeanMap;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Map相关转换处理工具类
 */
public class MapUtils {

    /**
     * 将字符串转换成Map<String, Object>
     * @param str
     * @return
     */
    public static Map<String, Object> strToMap(String str) {
        Map<String, Object> result = new HashMap<>();
        try{
            result = JSON.parseObject(str, new TypeReference<Map<String, Object>>(){});
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 将字符串转成List<Map<String,Object>>
     * @param str
     * @return
     */
    public static List<Map<String, Object>> strToListOfMap(String str) {
        List<Map<String, Object>> result = new ArrayList<>();
        try{
            result = JSON.parseObject(str, new TypeReference<List<Map<String, Object>>>() {});
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 合并两个Map
     * @param map1
     * @param map2
     * @return
     */
    public static Map<String, Object> mergeHashMap(Map<String, Object> map1, Map<String, Object> map2) {
        HashMap<String, Object> combineResultMap = new HashMap<>();
        combineResultMap.putAll(map1);
        combineResultMap.putAll(map2);
        return combineResultMap;
    }

    /**
     * 使用BeanMap将实体类转成Map
     * @param bean
     * @param <T>
     * @return
     */
    public static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = Maps.newHashMap();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                map.put(key + "", beanMap.get(key));
            }
        }
        return map;
    }

    /**
     * 将 map 转成实体类
     * @param clazz
     * @param map
     * @param <T>
     * @return
     */
    public static <T> T convertMapToBean(Class<T> clazz, Map<String, Object> map) {
        T obj = null;
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
            // 创建 JavaBean 对象
            obj = clazz.newInstance();
            // 给 JavaBean 对象的属性赋值
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (int i = 0; i < propertyDescriptors.length; i++) {
                PropertyDescriptor descriptor = propertyDescriptors[i];
                String propertyName = descriptor.getName();
                if (map.containsKey(propertyName)) {
                    Object value = map.get(propertyName);
                    if ("".equals(value)) {
                        value = null;
                    }
                    Object[] args = new Object[1];
                    args[0] = value;
                    descriptor.getWriteMethod().invoke(obj, args);
                }
            }
        } catch (IllegalAccessException e) {
            System.out.println(StrUtil.format("convertMapToBean 实例化JavaBean失败 Error{}",e));
        } catch (IntrospectionException e) {
            System.out.println(StrUtil.format("convertMapToBean 分析类属性失败 Error{}" ,e));
        } catch (IllegalArgumentException e) {
            System.out.println(StrUtil.format("convertMapToBean 映射错误 Error{}" ,e));
        } catch (InstantiationException e) {
            System.out.println(StrUtil.format("convertMapToBean 实例化 JavaBean 失败 Error{}" ,e));
        }catch (InvocationTargetException e){
            System.out.println(StrUtil.format("convertMapToBean字段映射失败 Error{}" ,e));
        }catch (Exception e){
            System.out.println(StrUtil.format("convertMapToBean Error{}" ,e));
        }
        return (T) obj;
    }

    /**
     * 按照value对Map排序
     * @param map
     * @param isAsc 是否升序排序
     * @return ArrayList 有序存储着Map的元素
     */
    public static ArrayList<Map.Entry<String, Double>> sortMapByValue(Map<String, Double> map, final boolean isAsc) {
        //把Map转换为List
        List<Map.Entry<String, Double>> entries = new ArrayList<>(
                map.entrySet());
        //利用Collections.sort进行排序
        Collections.sort(entries, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> obj1,
                               Map.Entry<String, Double> obj2) {
                if(isAsc) {
                    return obj1.getValue().compareTo(obj2.getValue());
                } else {
                    return obj2.getValue().compareTo(obj1.getValue());
                }
            }
        });
        return (ArrayList<Map.Entry<String, Double>>) entries;
    }

}
