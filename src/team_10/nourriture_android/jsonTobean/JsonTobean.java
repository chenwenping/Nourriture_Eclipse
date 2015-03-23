package team_10.nourriture_android.jsonTobean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class JsonTobean {
    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, false);

        mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
        mapper.configure(DeserializationFeature.WRAP_EXCEPTIONS, false);
    }

    public static <T> ArrayList<T> getList(Class<? extends T[]> beansClass,
                                           String json) throws JsonParseException, JsonMappingException,
            IOException {
        T[] beans = mapper.readValue(json, beansClass);
        ArrayList<T> list = new ArrayList<T>();
        for (T bean : beans) {
            list.add(bean);
        }
        return list;
    }

    public static <T> T getBean(Class<T> beanClass, String json)
            throws JsonParseException, JsonMappingException, IOException {
        T bean = mapper.readValue(json, beanClass);
        return bean;
    }

    public static List<LinkedHashMap<String, Object>> get(String json)
            throws JsonParseException, JsonMappingException, IOException {
        List<LinkedHashMap<String, Object>> list = mapper.readValue(json,
                List.class);

        return list;
    }

}
