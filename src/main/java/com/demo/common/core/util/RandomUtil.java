package com.demo.common.core.util;



import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 生成随机数工具类
 *
 * @author Spring
 */
public class RandomUtil {
    private static String RANDOM_NUMBER = "23456789";
    private static String RANDOM_UPPERCASE = "ABCDEFGHIJKMNPQRSTUVWXYZ";
    private static String RANDOM_LOWERCASE = "abcdefghijkmnpqrstuvwxyz";

    public static String generateUppercase(int length) {
        return generateRandom(RANDOM_UPPERCASE, length);
    }

    public static String generateLowercase(int length) {
        return generateRandom(RANDOM_LOWERCASE, length);
    }

    public static String generateNumber(int length) {
        return generateRandom(RANDOM_NUMBER, length);
    }

    public static String generateRandom(int length) {
        return generateRandom(RANDOM_NUMBER + RANDOM_UPPERCASE + RANDOM_LOWERCASE, length);
    }

    public static String generateRandom(String src, int length) {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(src.length());
            sb.append(src.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 根据权重随即取出对象
     *
     * @param weightMap key：存放的对象 value：权重值
     * @param <T>
     * @return
     */
    public static <T> WeightMeta<T> buildWeightMeta(final Map<T, Integer> weightMap) {
        final int size = weightMap.size();
        Object[] nodes = new Object[size];
        int[] weights = new int[size];
        int index = 0;
        int weightAdder = 0;
        for (Map.Entry<T, Integer> each : weightMap.entrySet()) {
            nodes[index] = each.getKey();
            weights[index++] = (weightAdder = weightAdder + each.getValue());
        }
        return new WeightMeta<T>((T[]) nodes, weights);
    }

    public static void main(String[] args) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("a", 10);
        map.put("b", 30);
        map.put("c", 60);
        WeightMeta<String> md = RandomUtil.buildWeightMeta(map);
        String weightRandomUrl = md.random();
        for (int i = 0; i < 100; i++) {
            System.out.println(md.random());
        }
    }
}
