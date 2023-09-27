package com.example.doit.data.mappers

abstract class BaseMapper<I, O> {
    open suspend fun map(item: I): O {
        throw NotImplementedError()
    }

    open suspend fun mapBack(item: O): I {
        throw NotImplementedError()
    }
}