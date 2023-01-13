package com.github.kyuubiran.ezxhelper.interfaces

interface IFindSuper<Self> {
    /**
     * Contact the field sequence in super classes. Only effect when the finder created from a class.
     * @param untilPredicate The predicate to stop finding(return true = break, false = continue) or null if until [Object.class] / [Any.javaClass]
     * @return [Self] this finder.
     */
    fun findSuper(untilPredicate: (Class<*>.() -> Boolean)? = null): Self
}