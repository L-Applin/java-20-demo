package ca.applin.demo;

import java.util.*;
import java.util.stream.*;

public sealed interface Json<T>
        permits JsonNull, JsonNum, JsonStr, JsonArray, JsonObject {

    T value();

    default String toJsonString(String tabs) {
        return toJsonString(tabs, 0, new StringBuilder());
    }

    default String toJsonString(String tabs, int level, StringBuilder sb) {
        final String actualTabs  = tabs.repeat(level);
        switch (this) {
            case JsonNull jn -> sb.append("null");
            case JsonNum(Double value) -> sb.append(value);
            case JsonStr(String value) -> sb.append("\"").append(value).append("\"");
            case JsonArray(List<Json<?>> value) -> sb
                    .append("[")
                    .append(value.stream()
                            .map(json -> json.toJsonString(actualTabs, level, new StringBuilder()))
                            .collect(Collectors.joining(", ")))
                    .append("]");
            case JsonObject(Map<String, Json<?>> value) -> sb
                    .append("{")
                    .append("\n")
                    .append(value.entrySet().stream()
                            .map(json -> toObjectElement(json.getKey(), json.getValue(), tabs, level + 1))
                            .collect(Collectors.joining(",\n")))
                    .append("\n")
                    .append(actualTabs)
                    .append("}");
        }
        return sb.toString();
    }

    static String toObjectElement(String key, Json<?> value, String tabs, int level) {
        StringBuilder sb = new StringBuilder();
        sb.append(tabs.repeat(level));
        sb.append("\"").append(key).append("\"");
        sb.append(": ");
        sb.append(value.toJsonString(tabs, level, new StringBuilder()));
        return sb.toString();
    }

    static void main(String[] args) {
        String tabs = "    ";
        System.out.println(new JsonNum(10.).toJsonString(tabs));

        System.out.println(new JsonArray(
                List.of(new JsonStr("Hello!"),
                        new JsonNum(1.0),
                        new JsonNum(2.0),
                        new JsonNum(3.0))
        ).toJsonString(tabs));

        Map<String, Json<?>> map = new LinkedHashMap<>();
        map.put("key1", new JsonNum(0.0));
        map.put("key2", new JsonArray(List.of(new JsonNum(0.), new JsonNum(1.))));
        map.put("key3", new JsonObject(Map.of("inner1key", new JsonStr("inner1Value"))));
        System.out.println(new JsonObject(map).toJsonString(tabs));
    }
}

record JsonNull(Void value) implements Json<Void> {}
record JsonNum(Double value) implements Json<Double> {}
record JsonStr(String value) implements Json<String> {}
record JsonArray(List<Json<?>> value) implements Json<List<Json<?>>> {}
record JsonObject(Map<String, Json<?>> value) implements Json<Map<String, Json<?>>> {}