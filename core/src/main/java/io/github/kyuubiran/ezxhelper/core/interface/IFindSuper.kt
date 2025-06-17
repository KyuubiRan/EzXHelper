package io.github.kyuubiran.ezxhelper.core.`interface`

interface IFindSuper<Finder> {

    /**
     * Contact the field sequence in super classes
     *
     * Only effect when the finder created from a class
     *
     * 查找父类的字段序列
     *
     * 仅在从类创建查找器时有效
     *
     * @param untilPredicate The predicate to stop finding(return true = break, false = continue) or null if until [Object.class] / [Any.javaClass] | 当返回 true 时停止查找(跳出循环)，返回 false 时继续查找，或条件为 null 时查找到 [Object.class] / [Any.javaClass]
     * @return [Finder] new finder | 返回新的查找器 [Finder]
     */
    fun findSuper(untilPredicate: (Class<*>.() -> Boolean)? = null): Finder
}