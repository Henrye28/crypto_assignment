package com.henry.portfolio;

public class Tuple<T1, T2> {
    T1 t1;
    T2 t2;

    public Tuple(T1 t1, T2 t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    public static <A, B> Tuple of(A a, B b){
        return new Tuple(a, b);
    }

    public T1 getT1() {
        return t1;
    }

    public T2 getT2() {
        return t2;
    }
}
