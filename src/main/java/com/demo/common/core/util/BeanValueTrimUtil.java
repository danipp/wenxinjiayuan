package com.demo.common.core.util;


import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 去掉字符串类型的首尾空格
 *
 * @author zane
 */
public class BeanValueTrimUtil {

    /**
     * 去掉bean中所有属性为字符串的《前后空格》
     *
     * @param bean
     * @throws Exception
     */
    public static void beanValueTrim(Object bean) throws Exception {
        if (bean != null) {
            //获取所有的字段包括public,private,protected,private
            Field[] fields = bean.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                if (f.getType().getName().equals("java.lang.String")) {
                    //获取字段名
                    String key = f.getName();
                    Object value = getFieldValue(bean, key);
                    if (null == value) {
                        continue;
                    }
                    setFieldValue(bean, key, value.toString().trim());
                }
            }
        }
    }

    /**
     * 去掉bean中所有属性为字符串的《前后空格和前后引号》（包括单引号和双引号）
     *
     * @param bean
     * @throws Exception
     */
    public static void beanValueTrimAndQuotationMarks(Object bean) throws Exception {
        if (bean != null) {
            //获取所有的字段包括public,private,protected,private
            Field[] fields = bean.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                if (f.getType().getName().equals("java.lang.String")) {
                    //获取字段名
                    String key = f.getName();
                    Object value = getFieldValue(bean, key);
                    if (null == value) {
                        continue;
                    }
                    setFieldValue(bean, key, quotationMarks(value.toString().trim()));
                }
            }
        }
    }

    /**
     * 去掉bean中所有属性为字符串的《所有空格和前后引号》（包括单引号和双引号）
     *
     * @param bean
     * @throws Exception
     */
    public static void beanValueSpaceAndQuotationMarks(Object bean) throws Exception {
        if (bean != null) {
            //获取所有的字段包括public,private,protected,private
            Field[] fields = bean.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                if (f.getType().getName().equals("java.lang.String")) {
                    //获取字段名
                    String key = f.getName();
                    Object value = getFieldValue(bean, key);
                    if (null == value) {
                        continue;
                    }
                    setFieldValue(bean, key, quotationMarks(value.toString().replaceAll(" ", "")));
                }
            }
        }
    }

    /**
     * 去掉《前后引号》（包括单引号和双引号）
     *
     * @param param 需要处理的字符串
     * @return
     */
    public static String quotationMarks(String param) {
        boolean[] flag = {false, false};
        if (param.startsWith("\"")) {
            param = param.substring(1, param.length());
        } else if (param.startsWith("\'")) {
            param = param.substring(1, param.length());
        } else {
            flag[0] = true;
        }

        if (param.endsWith("\"")) {
            param = param.substring(0, param.length() - 1);
        } else if (param.endsWith("\'")) {
            param = param.substring(0, param.length() - 1);
        } else {
            flag[1] = true;
        }
        if (flag[0] && flag[1]) {
            return param;
        }
        return quotationMarks(param);
    }

    /**
     * 利用反射通过get方法获取bean中字段fieldName的值
     *
     * @param bean
     * @param fieldName
     * @return
     * @throws Exception
     */
    private static Object getFieldValue(Object bean, String fieldName)
            throws Exception {
        StringBuffer result = new StringBuffer();
        String methodName = result.append("get")
                .append(fieldName.substring(0, 1).toUpperCase())
                .append(fieldName.substring(1)).toString();
        Object rObject = null;
        Method method = null;
        @SuppressWarnings("rawtypes")
        Class[] classArr = new Class[0];
        method = bean.getClass().getMethod(methodName, classArr);
        rObject = method.invoke(bean, new Object[0]);
        return rObject;
    }

    /**
     * 利用发射调用bean.set方法将value设置到字段
     *
     * @param bean
     * @param fieldName
     * @param value
     * @throws Exception
     */
    private static void setFieldValue(Object bean, String fieldName, Object value)
            throws Exception {
        StringBuffer result = new StringBuffer();
        String methodName = result.append("set")
                .append(fieldName.substring(0, 1).toUpperCase())
                .append(fieldName.substring(1)).toString();
        /**
         * 利用发射调用bean.set方法将value设置到字段
         */
        Class[] classArr = new Class[1];
        classArr[0] = "java.lang.String".getClass();
        Method method = bean.getClass().getMethod(methodName, classArr);
        method.invoke(bean, value);
    }

}
