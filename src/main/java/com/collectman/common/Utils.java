package com.collectman.common;

import com.google.gson.Gson;

import java.util.List;

public final class Utils {

    private Utils() {
    }

    public static final Gson GSON = new Gson();

    public static String join(List<String> list) {
        if(list == null || list.size() == 0) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for(int i = 0, len = list.size(); i < len; i++) {
            result.append(list.get(i));
            if(i < (len - 1)) {
                result.append(",");
            }
        }
        return result.toString();
    }

    public static boolean isEmpty(String src) {
        return src == null || src.isEmpty();
    }

    public static boolean isNotEmpty(String src) {
        return !isEmpty(src);
    }

    @SuppressWarnings("checked")
    public static Object[] toArray(Object value) {
        if (value == null) {
            return null;
        }
        Object[] values;
        if (value.getClass().isArray()) {
            values = (Object[]) value;
        } else if (value instanceof List) {
            List<Object> list = (List<Object>) value;
            values = list.toArray();
        } else {
            values = new Object[]{value};
        }
        return values;
    }

}
