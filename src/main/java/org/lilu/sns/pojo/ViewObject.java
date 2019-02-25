package org.lilu.sns.pojo;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: lilu
 * @Date: 2019/1/24
 * @Description:
 */
public class ViewObject {
    private Map<String,Object> objs = new HashMap<>();

    public void set(String key,Object value) {
        objs.put(key,value);
    }

    public Object get(String key) {
        return objs.get(key);
    }

    public Map<String, Object> getObjs() {
        return objs;
    }
}