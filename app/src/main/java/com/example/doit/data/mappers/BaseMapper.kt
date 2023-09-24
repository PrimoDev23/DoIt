package com.example.doit.data.mappers

abstract class BaseMapper<I, O> {
    open fun map(item: I): O {
        throw NotImplementedError()
    }

    open fun mapBack(item: O): I {
        throw NotImplementedError()
    }
}