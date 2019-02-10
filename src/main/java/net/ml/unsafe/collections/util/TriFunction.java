package net.ml.unsafe.collections.util;

/**
 * A functional interface which takes 3 arguments
 *
 * @author micha
 * @param <A> first parameter
 * @param <B> second parameter
 * @param <C> third parameter
 * @param <R> return type
 */
@FunctionalInterface
public interface TriFunction<A,B,C,R> {
    R apply(A a, B b, C c) throws Exception;
}
