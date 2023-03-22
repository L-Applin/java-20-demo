package ca.applin.demo;

import java.util.*;

public class Pattern {

    interface Named {
        String name();
    }
    record Person(String name, int age) implements Named {}
    record Cat(String name) implements Named {}
    record Dog(String name, String breed) implements Named {}

    record Pair<T, R>(T left, R right){}

    public static void main(String[] args) {

        List<Named> named = List.of(
                new Person("Olivier", 35),
                new Person("John", 35),
                new Cat("Bilie"),
                new Cat("Edisson"),
                new Dog("Ruff", "")
        );

        for (Named n: named) {
            if (n instanceof Person(String name,int age)) {
                System.out.println("%s is %d years old".formatted(name, age));
            }
        }

        for (Named n: named) {
            String description = switch (n) {
                case Person(String name, int age) when "Olivier".equals(name) -> "%s is the best java programmer".formatted(name);
                case Person p -> "%s has been progrmming for %d years!".formatted(p.name, p.age);
                case Cat c -> "%s moews softly!".formatted(c.name());
                case Dog d -> "%s barks loudly!".formatted(d.name());
                default -> "unknown";
            };
            System.out.println(description);
        }

        List<Pair<String, Integer>> pairs = List.of(
                new Pair<>("first - 1", 0),
                new Pair<>("first - 2", 1),
                new Pair<>("first - 3", 2),
                new Pair<>("first - 4", 3),
                new Pair<>("first - 5", 4),
                new Pair<>("first - 6", 5),
                new Pair<>("first - 7", 6),
                new Pair<>("first - 8", 7)
        );
        for (Pair(var left, var right): pairs) {
            System.out.println(left + " :: " + (right + 1));
        }
    }
}
