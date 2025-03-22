@file:Suppress("unused", "ClassOrdering")

package tools.aqua.stars.logic.kcmftbl.smtModelChecker.dataTranslation

/** Ensures that each object has a unique ID. */
abstract class SmtTranslatableBase {

  companion object {

    private var nextSmtId = 0

    internal fun uniqueSmtID() = nextSmtId++
  }

  private var _smtID: Int? = null

  fun getSmtID(): Int {
    var smtID = _smtID
    if (smtID == null) {
      smtID = uniqueSmtID()
      _smtID = smtID
    }
    return smtID
  }

  internal fun getSmtTranslationClassInfo(): SmtTranslationClassInfo =
      smtTranslationClassInfo(this::class)
}
