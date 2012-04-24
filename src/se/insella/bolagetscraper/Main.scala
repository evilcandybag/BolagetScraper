package se.insella.bolagetscraper
import se.insella.bolagetscraper.gui.DiffFrame

object Main {
	def run(query: Array[Options]) {	
		val handler = new QueryHandler(query:_*)
		val (alt,neu) = handler.updateSaved
		val diff = neu &~ alt
		if (!(diff).isEmpty){
			new DiffFrame(diff).visible = true
		} else {
			println("No diff.")
			System.exit(0)
		}
	}
	
}