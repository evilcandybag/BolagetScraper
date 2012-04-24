package se.insella.bolagetscraper

class Drink(val no:Int, val title:String, val subtitle:String, val kind:String, val country:String) extends Serializable {
	
	private val serialVersionUID = 217489240175L
	
	override def hashCode = {
		no
	}
	
	override def equals(that:Any) = that match {
		case that:Drink => this.no == that.no
		case _ => false
	}
	
	override def toString = "No: " + no + " Name: " + title + " " + subtitle + " Type/year: " + kind + " From: " + country
}