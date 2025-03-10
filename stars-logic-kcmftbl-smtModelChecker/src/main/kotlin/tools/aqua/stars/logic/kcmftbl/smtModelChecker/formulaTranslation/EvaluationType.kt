package tools.aqua.stars.logic.kcmftbl.smtModelChecker.formulaTranslation

enum class EvaluationType {
  /** Corresponds to [EvalNode]. */
  EVALUATE,
  /** Corresponds to [WitnessEvalNode]. */
  WITNESS,
  /** Corresponds to [UniversalEvalNode]. */
  UNIV_INST
}
