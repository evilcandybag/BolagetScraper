package se.insella.bolagetscraper

object ConfigLoader {
	def defaultConfig:Array[Options] = Array(Butik(1410),Filters(Vara.asInstanceOf[Filter]),Grupp(Öl.asInstanceOf[Varugrupp]))
	
	private def parse (str: String):Options = {
		val butik = "Butik:(.*)".r
		val vara = "Varugrupp:(.*)".r
		str match {
			case butik(str) => Butik(str.toInt)
			case vara(str) => Grupp(parseGrupp(str))
			case x => println("Failed to match string: " + x); null
		}
	}
	
	def parseAndValidate(query:Array[String]):Array[Options] = {
		var res:List[Options] = List();
		for (q <- query) parse(q) match {
			case g @ Grupp(_) => res ::= Filters(Vara); res ::= g;
			case x => res ::= x
		}
		return res.toArray
	}
	
	private def parseGrupp(str:String):Varugrupp = {
		str.toLowerCase() match {
			case "öl" => Öl
			case "whisky" => Whisky
		}
	}
	
	
	
	def main(args:Array[String]) {
		println(parseAndValidate(Array("Butik:1410","Varugrupp:ÖL")))
	}
}