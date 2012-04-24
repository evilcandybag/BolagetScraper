package se.insella.bolagetscraper

import scala.swing.Publisher
import scala.actors.Actor



class QueryHandler(var query: Options*) extends Actor {
	import se.insella.utils._
	
	private var _intermediator:Intermediator = null
	
	val io = new FileIO("inventory.bin")
	start()
	
	def act() {
		loop {
			react {
				case "getInventory" => _intermediator ! getInventory
				case "loadOld" => _intermediator ! loadOld
				case "updateSaved" => {
					_intermediator ! ("update", updateSaved)
				}
			}
		}
	}
	def intermediator_= (value:Intermediator):Unit = _intermediator = value 
	
	private def sendIf(evt:Any) {
		if (_intermediator != null) {
			_intermediator ! evt
		}
	}
	
	/**
	 * Gets entire Systembolaget inventory corresponding to options.
	 * @param query - the query to send to Systembolaget
	 * @return all drinks in query with a unique Varunummer
	 */
	def getInventory:Set[Drink] = {
		var all:Set[Drink] = Set()
		
		val url = new URLHandler(query:_*)
		val xml = new XMLHandler(url.getConnectionStream)
		val size = xml.getTableSize
		
		do {
			val table = xml.parseTable
			all = all ++ table
			//println((url.getCurrentOffset / size.asInstanceOf[Double] * 100).asInstanceOf[Int])
			sendIf(Progress((url.getCurrentOffset / size.asInstanceOf[Double] * 100).asInstanceOf[Int]))
			url.nextOffset
			xml.setInputStream(url.getConnectionStream)
		} while (url.getCurrentOffset <= size)
		sendIf(ProgressEvent(100))
		all
	}
	
	/**
	 * Loads the old inventory from file.
	 * @return set with inventory.
	 */
	def loadOld: Set[Drink] = {
		val read = io.read
		if(read.isDefined) {
			val n = io.deserialize[Array[Drink]](read.get)
			if (n.isDefined) {
				n.get.toSet
			} else
				Set()
		}
		else
			Set()
	}
	
	/**
	 * Read the inventory and save it to file.
	 * @return - the read inventory.
	 */
	def updateSaved: (Set[Drink],Set[Drink]) = {
		val alt = loadOld
		val neu = getInventory
		io.write(io.serialize(neu.toArray[Drink]))
		(alt,neu)
	}
}

case class Progress(i:Int)
case class Done

case class Varunummer(n:Int) extends Options

abstract class Options
case class Butik(number:Int) extends Options
case class Grupp(v:Varugrupp) extends Options
case class Filters(g:Filter*) extends Options
case class Typer(l: Produkttyp*) extends Options

abstract class Filter
case object Vara extends Filter { override def toString = "varugrupp," }
case object Produkt extends Filter { override def toString = "produkttyp," }

abstract class Varugrupp
case object Öl extends Varugrupp { override def toString = "%C3%96l" }
case object Rött_vin extends Varugrupp { override def toString = "Rött+vin" }
case object Vitt_vin extends Varugrupp { override def toString = "Vitt+vin" }
case object Whisky extends Varugrupp { override def toString = "Whisky" }
case object Alkoholfritt extends Varugrupp { override def toString = "Alkoholfritt" }
case object Cider extends Varugrupp { override def toString = "Cider" }
case object Fruktvin extends Varugrupp { override def toString = "Fruktvin" }
case object Sherry extends Varugrupp { override def toString = "Sherry" }
case object Brandy extends Varugrupp { override def toString = "Brandy+och+Vinsprit" }
case object Rosévin extends Varugrupp { override def toString = "Rosévin" }
case object Mousserande_vin extends Varugrupp { override def toString = "Mousserande+vin" }
case object Grappa extends Varugrupp { override def toString = "Grappa+och+Marc" }
case object Likör extends Varugrupp { override def toString = "Likör" }
case object Rom extends Varugrupp { override def toString = "Rom" }


abstract class Produkttyp
case object Ljus_lager extends Produkttyp { override def toString = "Ljus+lager" }
case object Mörk_lager extends Produkttyp { override def toString = "Mörk+lager" }
case object Porter_stout extends Produkttyp { override def toString = "Porter+och+Stout" }
case object Ale extends Produkttyp { override def toString = "Ale" }
case object Veteöl extends Produkttyp { override def toString = "Veteöl" }
case object Spontanjäst extends Produkttyp { override def toString = "Spontanjäst+öl" }
case object Specialöl extends Produkttyp { override def toString = "Specialöl" }
case object Flera extends Produkttyp { override def toString = "Flera+typer" }
