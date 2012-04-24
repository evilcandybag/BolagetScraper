package se.insella.bolagetscraper

import scala.xml.{Elem, XML}
import scala.xml.factory.XMLLoader
import org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl
import scala.xml.NodeSeq
import java.io.InputStream
  
/**
 * Handles the parsing of the page in the InputStream.
 */
class XMLHandler(private var in: InputStream) {
 
    private val factory = new SAXFactoryImpl()
    private val parser = XML.withSAXParser(factory.newSAXParser())
    private var xml = parser.load(in)
    
    //Public members
    /**
     * Set a new input stream and parse its contents.
     */
    def setInputStream(in: InputStream) {
    	this.in = in
    	xml = parser.load(in)
    }
    
    /**
     * Parses inventory table of the current page.
     */
    def parseTable = {
    	(for (e <- getTableContents) yield parseItem(e.asInstanceOf[Elem]))
    }
    
    /**
     * Extracts the total number of items in a query.
     */
    def getTableSize = {
    	val some = (xml\\"div").find(e => if (e.attributes != null && e.attributes.value != null) e.attributes.value.text == "tab selected" else false)
    	
    	(some.get\\"span")(0).child(0).text.init.tail.toInt
    }
    
    //Private members
    private def getTableContents = {
    	xml\\"table"\"tbody"\"tr"
    }
    
    private def parseItem(e: Elem) = {
    	val tds = e.child
    	val col0 = tds(0).child(2)
    	val no = col0.attributes.value.text.toInt
    	val title = col0.child(0).child(0).text
    	val subtitle = if (col0.child.length > 2) col0.child(2).text else ""
    	val kind = tds(1).child(1).child(0).text
    	val land = tds(2).child(1).child(0).text
    	new Drink(no,title,subtitle,kind,land)
    } 
}

