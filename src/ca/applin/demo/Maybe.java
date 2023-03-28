package ca.applin.demo;

import java.util.function.*;

public sealed interface Maybe<T> permits Maybe.Just, Maybe.Nothing {

    <R> Maybe<R> map(Function<T, R> fun);

    static <T> Maybe<T> create(T value) {
        if (value == null) {
            return nothing();
        }
        return new Just<>(value);
    }

    static <T> Maybe<T> nothing() {
        return new Nothing<>();
    }

    record Just<T>(T value) implements Maybe<T> {
        @Override
        public <R> Maybe<R> map(Function<T, R> fun) {
            R maybeNUll = fun.apply(value);
            if (maybeNUll == null) {
                return nothing();
            }
            return new Just<>(maybeNUll);
        }
    }

    record Nothing<T>() implements Maybe<T> {
        @Override
        @SuppressWarnings("unchecked")
        public <R> Maybe<R> map(Function<T, R> fun) {
            return (Maybe<R>) this;
        }
    }

    static void main(String[] args) {
        Maybe<String> mStr = Maybe.create(args.length > 0 ? args[0] :  null)
                .map(String::toUpperCase);

        if (mStr instanceof Maybe.Nothing<String> n) {
            System.out.printf("Expect Just but got Nothing: %s%n", n);
        }

        switch (mStr) {
            case Just<String>(String value) -> System.out.println(value);
            case Nothing<String> n          -> System.out.println("still nothing");
        }
    }
}


