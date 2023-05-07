package com.yoyo.admin.common.utils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * List相关转换处理工具类
 */
public class ListUtils {

    /**
     * 按照指定长度拆分list
     * @param list
     * @param groupSize
     * @return
     */
    public static List<List<String>> splitList(List<String> list, int groupSize){
        int length = list.size();
        // 计算可以分成多少组
        int num = ( length + groupSize - 1 )/groupSize ;
        List<List<String>> newList = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            // 开始位置
            int fromIndex = i * groupSize;
            // 结束位置
            int toIndex = Math.min((i + 1) * groupSize, length);
            newList.add(list.subList(fromIndex,toIndex)) ;
        }
        return  newList ;
    }

    /**
     * 合并两个List<Map<String, Object>>
     * @param list1
     * @param list2
     * @return
     */
    public static List<Map<String, Object>> mergeMapList(List<Map<String, Object>> list1, List<Map<String, Object>> list2){
        list1.addAll(list2);
        Set<String> set = new HashSet<>();
        return list1.stream()
                .collect(Collectors.groupingBy(o->{
                    //暂存所有key
                    set.addAll(o.keySet());
                    //按a_id分组
                    return o.get("id");
                })).values().stream().map(maps -> {
                    //合并
                    Map<String, Object> map = maps.stream().flatMap(m -> {
                        return m.entrySet().stream();
                    }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b));
                    //为没有的key赋值0
                    set.forEach(k -> {
                        if (!map.containsKey(k)) {
                            map.put(k, 0);
                        }
                    });
                    return map;
                }).collect(Collectors.toList());
    }

    /**
     * 排列组合（字符重复排列）
     * @param list 待排列组合字符集合
     * @param length 排列组合生成长度
     * @return 指定长度的排列组合后的字符串集合
     */
    public static List<String> permutation(List<String> list, int length) {
        Stream<String> stream = list.stream();
        for (int n = 1; n < length; n++) {
            stream = stream.flatMap(i -> list.stream().map(i::concat));
        }
        return stream.collect(Collectors.toList());
    }

    /**
     * 排列组合(字符不重复排列)
     * @param list 待排列组合字符集合(忽略重复字符)
     * @param length 排列组合生成长度
     * @return 指定长度的排列组合后的字符串集合
     */
    public static List<String> permutationNoRepeat(List<String> list, int length) {
        Stream<String> stream = list.stream().distinct();
        for (int n = 1; n < length; n++) {
            stream = stream.flatMap(i -> list.stream().filter(j -> !i.contains(j)).map(i::concat));
        }
        return stream.collect(Collectors.toList());
    }

    /**
     * 生成数值列表
     * @param start
     * @param end
     * @return
     */
    public static List<Integer> generateOrdinalList(int start, int end) {
        List<Integer> range = IntStream.rangeClosed(start, end)
                .boxed().collect(Collectors.toList());
        return range;
    }

    /**
     * 将List转成指定分隔符分隔的字符串
     * @param list
     * @param format
     * @return
     */
    public static String listToString(List<String> list, String format) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i == list.size() - 1) {
                str.append(list.get(i));
            } else {
                str.append(list.get(i)).append(format);
            }
        }
        return str.toString();
    }

    /**
     * 将指定分隔符分隔的字符串转成List
     * @param str
     * @param format
     * @return
     */
    public static List<String> stringToList(String str, String format) {
        return Arrays.asList(str.split(format));
    }

    /**
     * 计算并集并去重
     * @param list1
     * @param list2
     * @return
     */
    public static List<String> union(List<String> list1, List<String> list2){
        List<String> list = new LinkedList<>();
        list.addAll(list1);
        list.addAll(list2);
        HashSet<String> hs = new HashSet<>(list);
        list.clear();
        list.addAll(hs);
        return list;
    }

    /**
     * 计算交集
     * @param list1
     * @param list2
     * @return
     */
    public static List<String> retainAll(List<String> list1, List<String> list2){
        List<String> list = new LinkedList<>();
        list.addAll(list1);
        list.retainAll(list2);
        return list;
    }

    /**
     * 计算差集
     * @param list1
     * @param list2
     * @return
     */
    public static List<String> subtraction(List<String> list1, List<String> list2) {
        List<String> list = new LinkedList<>();
        list.addAll(list1);
        list.removeAll(list2);
        return list;
    }


    public static void main(String[] args) {
        List<String> list = Arrays.asList("1","2","3");

        System.out.println(splitList(list,2));
        System.out.println(permutation(list, 2));
        System.out.println(permutationNoRepeat(list, 2));

        List<Integer> lengthList = generateOrdinalList(1,list.size());
        List<String> resultList = new ArrayList<>();
        for(Integer length:lengthList){
            List<String> itemList = permutationNoRepeat(list,length);
            for(String item:itemList){
                item = listToString(stringToList(item,""),",");
                resultList.add(item);
            }
        }
        System.out.println(resultList);

        String str = listToString(list,",");
        System.out.println(str);
        System.out.println(stringToList(str,","));

        List<String> list1 = Arrays.asList("a","b","c");
        List<String> list2 = Arrays.asList("c","d","e");
        System.out.println(union(list1,list2));
        System.out.println(retainAll(list1,list2));
        System.out.println(subtraction(list1,list2));

    }

}
