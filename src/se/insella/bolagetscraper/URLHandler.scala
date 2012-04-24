package se.insella.bolagetscraper

import java.net.URL

/**
 * Fetches a page of Systembolaget inventory with the given options as search query.
 */
class URLHandler(opts: Options*) {
	private val baseURL = "http://www.systembolaget.se/Ajax.aspx?sortfield=Default&sortdirection=Ascending&action=search&excludesvalues=False&groupfiltersheader=Default"
	private var hitoffset = 0
	private var page = 1
		
	/**
	 * Returns the page as an input stream. Each stream contains 25 items.
	 */
	def getConnectionStream = mkURL.openConnection.getInputStream
	
	/**
	 * Changes the page view offset by 25.
	 */
	def nextOffset {
		hitoffset += 25
		page += 1
	}
	
	/**
	 * Return the current offset of the URLHandler
	 */
	def getCurrentOffset = hitoffset
	
	def mkURL = new URL(baseURL + readOptions + pageQuery)
	
	private def pageQuery = "&hitsoffset=" + hitoffset + "&page=" + page
	
	private def readOptions = {
		(for (o <- opts) yield o match {
			case Butik(n) => "&searchview=Store&butik=" + n
			case Grupp(v) => "&varugrupp=" + v
			case Filters(g @ _*) => "&filters=" + (for (i <- g) yield i.toString).reduce(_+_)
			case Typer(t @ _*) => (for (i <- t) yield "&produkttyp=" + i.toString).reduce(_+_)
			case Varunummer(n) => "&varuNr=" + n
		}).reduce(_+_)
	}
	
}

object URLHandler {
	val itemURL = "http://www.systembolaget.se/Sok-dryck/Dryck/?varuNr="
}