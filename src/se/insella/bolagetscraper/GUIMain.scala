package se.insella.bolagetscraper

import java.net.URLConnection
import java.net.URL
import scala.swing._
import se.insella.utils._
import se.insella.bolagetscraper.gui._
import java.awt.BorderLayout
import scala.swing.event._
import javax.swing.UIManager

class GUIMain(query: Array[Options]) {
	
	val handler = new QueryHandler(query:_*)
	implicit val intermediator = Intermediator(handler)
	handler.intermediator_=(intermediator)
	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
		
	val t = top
	t.visible = true
	t.pack

	
	def top = new MainFrame {
		title = "BolagetScraper"
			
		val view = new InventoryView
		val updateButton = new Button("Update")
		val updateProgress = new ProgressBar
		val updatePercent = new Label {
			visible = false
		}
		
		val updatePanel = new FlowPanel {
			contents += updateButton
			contents += updateProgress
		}
		
		contents = new BorderPanel {
			add(view,BorderPanel.Position.Center)
			add(updatePanel, BorderPanel.Position.South)
			view.updateContents(handler.loadOld)
		}
		
		listenTo(updateButton, intermediator.asInstanceOf[Publisher])
		reactions += {
			case ButtonClicked(`updateButton`) => {
				updateButton.enabled = false
				Send("updateSaved")
			}
			case Receive(("update", (alt:Set[Drink],neu:Set[Drink]))) => {
				updateButton.enabled = true
				
				view.updateContents(neu)
				val diff = neu &~ alt
				if (!diff.isEmpty)
					new DiffFrame(diff).visible = true
				updatePercent.visible = false
				pack
			}
			case Receive(Progress(x)) => {
				updateProgress.value = x
				updatePercent.visible = true
				updatePercent.text = x.toString
			}
		}
	}
}