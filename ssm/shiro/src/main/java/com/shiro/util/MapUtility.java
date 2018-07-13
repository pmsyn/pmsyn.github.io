package com.shiro.util;

import com.shiro.exception.GeneralException;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Method;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MapUtility {
    private static SimpleDateFormat dforamter = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat DHforamter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public static Map<String, Object> getEmptyMap() {
        return new HashMap<String, Object>();
    }

    public static List<Map<String, Object>> getEmptyList() {
        return new ArrayList<Map<String, Object>>();
    }

    public static String getMapStringValueWhenEmptyRaiseError(Map<String, Object> map, String key, String errMsg)
			throws GeneralException {
        String v = getMapStringValue(map, key);
        if ("".equals(v)) {
            throw new GeneralException(errMsg);
        }
        return v;
    }

    public static void extend(Map<String, Object> dest, Map<String, Object> obj) {
        Set<String> keys = obj.keySet();
        for (String k : keys) {
            if (ContainsKey(dest, k)) continue;
            setMapObjectValue(dest, k, getMapObjectValue(obj, k));
        }
    }

    public static void changeKeyName(List<Map<String, Object>> list, String oldKey, String newKey) {
        for (Map<String, Object> map : list) {
            changeKeyName(map, oldKey, newKey);
        }
    }

    public static void keyLowerCase(List<Map<String, Object>> data) {
        for (Map<String, Object> m : data) {
            keyLowerCase(m);
        }
    }

    public static void keyLowerCase(Map<String, Object> m) {
        Map<String, Object> kv = new HashMap<String, Object>();

        Set<String> ks = m.keySet();
        Iterator<String> key = ks.iterator();
        while (key.hasNext()) {
            String k = key.next();
            if (!k.equals(k.toLowerCase())) {
                kv.put(k.toLowerCase(), m.get(k));
                key.remove();
            }
        }
        for (String k : kv.keySet()) {
            m.put(k, kv.get(k));
        }
    }

    public static void changeKeyName(Map<String, Object> map, String oldKey, String newKey) {

        Object o = MapUtility.getMapObjectValue(map, oldKey);
        MapUtility.removeMapKey(map, oldKey);
        map.put(newKey, o);

    }

    public static String getMapStringValue(Map<String, Object> map, String key, String defaultValue) {
        String v = getMapStringValue(map, key);
        if ("".equals(v)) v = defaultValue;
        return v;
    }

    public static String getMapStringValueByKeys(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            String v = MapUtility.getMapStringValue(map, key);
            if (!"".equals(v)) {
                return v;
            }
        }

        return "";
    }

    public static String getMapStringValue(Map<String, Object> map, String key) {
        String ret = "";
        if (map == null) {
            return ret;
        }
        Object v = null;
        for (String k : map.keySet()) {
            String kk = k.replace("_", "");
            if (kk.equalsIgnoreCase(key.replace("_", ""))) {
                v = map.get(k);
                break;
            }
        }
        if (v != null) {
            if (v instanceof Clob) {
                try {
                    ret = ClobToString((Clob) v);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                ret = v.toString();
            }
        }
        if (ret == null || "undefined".equals(ret)) {
            ret = "";
        }
        return ret;
    }

    public static String ClobToString(Clob clob) throws SQLException, IOException {
        String reString = "";
        Reader is = clob.getCharacterStream();// 得到流
        BufferedReader br = new BufferedReader(is);
        String s = br.readLine();
        StringBuffer sb = new StringBuffer();
        while (s != null) {// 执行循环将字符串全部取出付值给StringBuffer由StringBuffer转成STRING
            sb.append(s);
            s = br.readLine();
        }
        reString = sb.toString();
        return reString;
    }

    public static boolean ContainsKey(Map<String, Object> map, String key) {
        for (String k : map.keySet()) {
            if (key.equalsIgnoreCase(k)) return true;
        }
        return false;
    }

    public static String getMapStringValue(Map<String, Object> map, int index) {
        String ret = "";
        if (map == null) {
            return ret;
        }
        Object v = null;

        int fc = map.keySet().size();
        if (fc <= index) return "";
        int cnt = 0;
        for (String k : map.keySet()) {
            if (cnt == index) {
                v = map.get(k);
                break;
            }
            cnt++;
        }
        if (v != null) {

            ret = v.toString();
        }
        return ret.replace("null", "").replace("undefined", "");
    }

    public static List<Map<String, Object>> merageList(List<Map<String, Object>> list1, List<Map<String, Object>>
			list2) {
        if (list1.size() == 0) return list2;

        Map<String, Object> map = list1.get(0);
        Set<String> keys = map.keySet();
        for (Map<String, Object> m : list2) {
            Map<String, Object> nm = new HashMap<String, Object>();
            for (String key : keys) {
                nm.put(key, MapUtility.getMapObjectValue(m, key));
            }
            list1.add(nm);
        }
        return list1;
    }

    public static String map2Xml(Map<String, Object> map) {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root>";
        xml += mapXml(map);
        xml += "</root>";
        return xml;
    }

    static String mapXml(Map<String, Object> map) {
        String xml = "";
        for (String key : map.keySet()) {
            Object val = map.get(key);
            if (val instanceof Map) {
                xml += "<" + key.toLowerCase() + ">" + mapXml((Map<String, Object>) val) + "</" + key.toLowerCase() +
						">";
            } else if (val instanceof List) {
                xml += "<records name=\"" + key.toLowerCase() + "\">";
                ArrayList<Map<String, Object>> ms = (ArrayList<Map<String, Object>>) val;
                for (Map<String, Object> m : ms) {
                    xml += "<record>";
                    xml += mapXml(m);
                    xml += "</record>";
                }
                xml += "</records>";
            } else {
                xml += "<field name=\"" + key.toLowerCase() + "\"><![CDATA[" + obj2String(val) + "]]></field>";
            }
        }

        return xml;
    }

    public static Object getMapObjectValue(Map<String, Object> map, String key) {

        Object v = null;
        for (String k : map.keySet()) {
            if (k.equalsIgnoreCase(key) || k.replaceAll("_", "").equalsIgnoreCase(key.replaceAll("_", ""))) {
                v = map.get(k);
                break;
            }
        }
        return v;
    }

    public static void setMapObjectValue(List<Map<String, Object>> data, String key, Object value) {
        for (Map<String, Object> map : data) {
            MapUtility.setMapObjectValue(map, key, value);
        }
    }


    public static void setMapObjectValue(Map<String, Object> map, String key, Object value) {

        for (String k : map.keySet()) {
            if (k.equalsIgnoreCase(key) || k.replaceAll("_", "").equalsIgnoreCase(key.replaceAll("_", ""))) {
                map.put(k, value);
                return;
            }
        }
        map.put(key, value);
    }

    public static void removeMapKey(Map<String, Object> map, String key) {

        for (String k : map.keySet()) {
            if (k.equalsIgnoreCase(key) || k.replaceAll("_", "").equalsIgnoreCase(key.replaceAll("_", ""))) {
                map.remove(k);
                break;
            }
        }
    }

    public static void removeMapKey(List<Map<String, Object>> maps, String key) {
        for (Map<String, Object> map : maps) {
            for (String k : map.keySet()) {
                if (k.equalsIgnoreCase(key) || k.replaceAll("_", "").equalsIgnoreCase(key.replaceAll("_", ""))) {
                    map.remove(k);
                    break;
                }
            }
        }
    }

    public static void copyMapValue(Map<String, Object> from, Map<String, Object> to, String key) {
        copyMapValue(from, key, to, key);
    }

    public static void copyMapValue(Map<String, Object> from, String fromKey, Map<String, Object> to, String tokey) {
        setMapObjectValue(to, tokey, getMapObjectValue(from, fromKey));
    }


    public static Object typeConversion(Class<?> cls, String str) {
        if (str == null || "".equals(str)) {
            return null;
        }
        Object obj = null;
        String nameType = cls.getSimpleName();
        if ("Integer".equals(nameType)) {
            obj = Integer.valueOf(str);
        }
        if ("String".equals(nameType)) {
            obj = str;
        }
        if ("Float".equals(nameType)) {
            obj = Float.valueOf(str);
        }
        if ("Double".equals(nameType)) {
            obj = Double.valueOf(str);
        }

        if ("Boolean".equals(nameType)) {
            obj = Boolean.valueOf(str);
        }
        if ("Long".equals(nameType)) {
            obj = Long.valueOf(str);
        }

        if ("Short".equals(nameType)) {
            obj = Short.valueOf(str);
        }

        if ("Character".equals(nameType)) {
            obj = str.charAt(1);
        }

        if ("Date".equals(nameType)) {
            try {
                obj = dforamter.parse(str);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return obj;
    }

    public static String obj2String(Object obj) {
        if (obj == null) return "";
        String str = "";
        if (obj instanceof Integer) {
            str = ((Integer) obj).toString();
        } else if (obj instanceof Integer) {
            str = (String) obj;
        } else if (obj instanceof Float) {
            str = ((Integer) obj).toString();
        } else if (obj instanceof Double) {
            str = ((Double) obj).toString();
        } else if (obj instanceof Boolean) {
            if (((Boolean) obj) == true) {
                str = "true";
            } else {
                str = "false";
            }
        } else if (obj instanceof Long) {
            str = ((Long) obj).toString();
        } else if (obj instanceof Short) {
            str = ((Short) obj).toString();
        } else if (obj instanceof Character) {
            str = obj.toString();
        } else if (obj instanceof Date) {
            str = dforamter.format(obj);
        } else {
            str = obj.toString();
        }

        return str;
    }

    public static Map<String, Object> fromString(String s) {
        String[] ss = s.split("&");
        Map<String, Object> record = new HashMap<String, Object>();
        for (String str : ss) {
            String[] pArray = str.split("=");
            if (pArray.length == 2) {
                record.put(pArray[0], pArray[1]);
            } else {
                record.put(pArray[0], "");
            }
        }

        return record;
    }


}
