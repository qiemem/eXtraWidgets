package uk.ac.surrey.xw.chooser

import java.awt.BorderLayout.CENTER
import java.awt.event.ItemEvent.SELECTED

import org.nlogo.api.Dump
import org.nlogo.api.LogoList
import org.nlogo.api.LogoList.toIterator
import org.nlogo.api.Nobody
import org.nlogo.window.GUIWorkspace

import javax.swing.DefaultListCellRenderer
import javax.swing.JComboBox
import javax.swing.JList
import uk.ac.surrey.xw.api.LabeledPanelWidget
import uk.ac.surrey.xw.api.ListProperty
import uk.ac.surrey.xw.api.ObjectProperty
import uk.ac.surrey.xw.api.StateUpdater
import uk.ac.surrey.xw.api.WidgetKey
import uk.ac.surrey.xw.api.WidgetKind
import uk.ac.surrey.xw.api.swing.enrichItemSelectable

class ChooserKind[W <: Chooser] extends WidgetKind[W] {

  override val name = "CHOOSER"
  override val newWidget = new Chooser(_, _, _)

  val selectedItemProperty = new ObjectProperty[W](
    "SELECTED-ITEM",
    _.combo.setSelectedItem(_),
    w ⇒ Option(w.combo.getSelectedItem).getOrElse(Nobody)
  )

  val itemsProperty = new ListProperty[W](
    "ITEMS",
    (w, xs) ⇒ {
      w.combo.removeAllItems()
      xs.foreach(w.combo.addItem(_))
      w.combo.setSelectedItem(xs.toVector.headOption.orNull)
    },
    w ⇒ LogoList((0 until w.combo.getItemCount).map(w.combo.getItemAt): _*)
  )

  override def propertySet = Set(selectedItemProperty, itemsProperty)

  override def defaultProperty = Some(selectedItemProperty)
}

class Chooser(
  val key: WidgetKey,
  val stateUpdater: StateUpdater,
  ws: GUIWorkspace)
  extends LabeledPanelWidget {

  override val kind = new ChooserKind[this.type]

  val combo = new JComboBox()
  add(combo, CENTER)

  /* Use a custom renderer so Dump.logoObject is used instead of toString */
  combo.setRenderer(new DefaultListCellRenderer {
    override def getListCellRendererComponent(
      list: JList, value: AnyRef, index: Int,
      isSelected: Boolean, cellHasFocus: Boolean) = {
      super.getListCellRendererComponent(list, value, index,
        isSelected, cellHasFocus)
      setText(Option(value).map(Dump.logoObject).getOrElse(""))
      this
    }
  })

  combo.onItemStateChanged { event ⇒
    if (event.getStateChange == SELECTED)
      updateInState(kind.selectedItemProperty)
  }
}