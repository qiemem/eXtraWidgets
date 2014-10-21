package uk.ac.surrey.soc.cress.extrawidgets.note

import org.nlogo.window.GUIWorkspace

import javax.swing.JLabel
import uk.ac.surrey.soc.cress.extrawidgets.api.JComponentWidget
import uk.ac.surrey.soc.cress.extrawidgets.api.StateUpdater
import uk.ac.surrey.soc.cress.extrawidgets.api.StringPropertyDef
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey
import uk.ac.surrey.soc.cress.extrawidgets.api.const

class Note(
  val key: WidgetKey,
  val stateUpdater: StateUpdater,
  ws: GUIWorkspace)
  extends JLabel
  with JComponentWidget {

  val xwText = new StringPropertyDef(this, setText, getText, const(getText))

}