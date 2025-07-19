package com.widuq.mapper;

@FunctionalInterface
public interface Mapper<F, T> {
    T mapFrom(F object);
}
