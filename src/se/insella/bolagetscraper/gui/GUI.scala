package se.insella.bolagetscraper.gui

import scala.swing._
import se.insella.bolagetscraper.Drink
import scala.swing.event.MouseClicked
import java.awt.Desktop
import java.awt.Desktop
import se.insella.bolagetscraper.URLHandler
import se.insella.bolagetscraper.Varunummer
import java.net.URI

class InventoryView extends ScrollPane {
	val headers = Array[Any]("Varunummer","Varugrupp","Undertitel","Sort","Land")
	var table:ListView[Drink] = new ListView
	listenTo(table)
	
	
	def updateContents(cont: Set[Drink]) = {
		table = new ListView(cont.toList) 
		table.listenTo(table.mouse.clicks)
		table.reactions += {
			case MouseClicked(_,_,_,2,_) => {
				if(Desktop.isDesktopSupported()) {
					val desktop = Desktop.getDesktop()
					if (desktop.isSupported(Desktop.Action.BROWSE))
						for (u <- table.selection.items) {
							val uri = new URI(URLHandler.itemURL + u.no)
							println(uri)
							desktop.browse(uri)
						}
					//Desktop.getDesktop().browse(new URLManager())
				}
			}
		}
		contents = table
	}
	
}

class DiffFrame(items: Set[Drink]) extends Frame {
	title = "New Arrivals!"
	contents = new FlowPanel {
		val view = new InventoryView
		contents += view
		view.updateContents(items)
		
	}
}
