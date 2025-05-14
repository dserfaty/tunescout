package com.dev1023.tunescout.mappers;

public interface Mapper<A, B> {
    B mapTo(A a);
    A mapFrom(B b);
}
