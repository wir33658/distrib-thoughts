package de.mid.bus

class Subjects(val base: String) {
  val transBase         = base + ".trans."
  val blobStoreBase     = base + ".blobstore."
  val mwhManagerBase    = base + ".mwhmanager."
  val modelManagerBase  = base + ".modelmanager."
  val modelBase         = base + ".model."
  val heartbeat         = base + ".heartbeat."
}

object EventSubjects extends Subjects("de.mid.smartfacts.event")

object CommandSubjects extends Subjects("de.mid.smartfacts.command")