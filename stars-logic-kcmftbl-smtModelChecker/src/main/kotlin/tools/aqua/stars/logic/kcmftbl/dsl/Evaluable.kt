package tools.aqua.stars.logic.kcmftbl.dsl

sealed interface Evaluable

sealed interface EvaluablePredicate : Formula

sealed interface EvaluableRelation<T> : EvaluablePredicate {
  val lhs: Term<T>
  val rhs: Term<T>
  val type: Relation
}
