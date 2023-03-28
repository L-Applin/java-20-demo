package ca.applin.demo;

import java.util.*;

/**
 * <h2><a href="https://openjdk.org/jeps/433">JEP 433</a>: Pattern Matching for switch (Fourth Preview)</h2>
 * The main changes since the third preview are:
 * <ul>
 *   <li>An exhaustive switch (i.e., a switch expression or a pattern switch statement) over an enum class now throws
 * MatchException rather than IncompatibleClassChangeError if no switch label applies at run time.</li>
 *   <li>The grammar for switch labels is simpler.</li>
 *   <li>Inference of type arguments for generic record patterns is now supported in switch expressions and statements,
 * along with the other constructs that support patterns.</li>
 * </ul>
 *
 * <h2><a href="https://openjdk.org/jeps/432">JEP 432</a>: Record Patterns (Second Preview)</h2>
 *The main changes since the first preview are to:
 * <ul>
 *   <li>Add support for inference of type arguments of generic record patterns</li>
 *   <li>Add support for record patterns to appear in the header of an enhanced for statement</li>
 *   <li>Remove support for named record patterns</li>
 * </ul>
 */
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
            if (n instanceof Person(String name, int age)) {
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

        for (Pair<String, Integer> p: pairs) {
            switch (p) {
                case Pair(var left, var right) when right < 5 -> System.out.println("LOW");
                case Pair(var left, var right) -> System.out.println("HIGH");
            }
        }

        for (Pair(var left, var right): pairs) {
            System.out.println("demo: " + left + " :: " + (right + 1));
        }
    }
}
