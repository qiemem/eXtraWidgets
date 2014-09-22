package uk.ac.surrey.soc.cress.extrawidgets.plugin

import java.awt.Component

import scala.Option.option2Iterable
import scala.collection.TraversableOnce.flattenTraversableOnce

import org.nlogo.api.I18N
import org.nlogo.app.Tabs
import org.nlogo.app.ToolsMenu
import org.nlogo.swing.TabsMenu

import GUIStrings.TabsManager.DefaultTabName
import GUIStrings.TabsManager.InvalidTabName
import GUIStrings.TabsManager.TabNameQuestion
import uk.ac.surrey.soc.cress.extrawidgets.plugin.data.MutableExtraWidgetsData
import util.Swing.inputDialog
import util.Swing.warningDialog

class TabsManager(val tabs: Tabs, val toolsMenu: ToolsMenu, val data: MutableExtraWidgetsData) {

  toolsMenu.addSeparator()
  toolsMenu.addMenuItem(
    GUIStrings.ToolsMenu.CreateTab,
    'X', true, () ⇒ createNewTab())

  def removeTab(component: Component): Unit =
    (0 until tabs.getTabCount)
      .find(i ⇒ tabs.getComponentAt(i) == component)
      .foreach { i ⇒
        tabs.remove(component)
        tabs.removeMenuItem(i)
      }

  def extraWidgetTabs: Vector[ExtraWidgetsTab] =
    tabs.getComponents.collect {
      case t: ExtraWidgetsTab ⇒ t
    }(collection.breakOut)

  def createNewTab(): Unit = {
    def askName(default: String) = inputDialog(
      GUIStrings.ToolsMenu.CreateTab,
      TabNameQuestion, default)
    Iterator
      .iterate(askName(DefaultTabName))(_.flatMap(askName))
      .takeWhile(_.isDefined)
      .flatten
      .map(addTab)
      .takeWhile(_.isLeft)
      .flatMap(_.left.toSeq)
      .foreach(warningDialog(InvalidTabName, _))
  }

  def addTab(name: String): Either[String, ExtraWidgetsTab] =
    for {
      _ ← data.add("tab", name).right
    } yield {
      val tab = new ExtraWidgetsTab(name)
      tabs.addTab(name, tab)
      tabs.tabsMenu = new TabsMenu(I18N.gui.get("menu.tabs"), tabs)
      tab
    }
}
