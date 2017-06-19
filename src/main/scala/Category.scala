/**
  * Created by ataman on 19.06.2017.
  *
  * These data types represents our labels in dataset.
  * We can take advantage of pattern matching etc.
  */

sealed trait Category {
  def name: String
  def label: Double
}

object Category {
  def fromString(value: String): Option[Category] = {
    Vector(Ekonomi, Magazin, Saglik, Siyasi, Spor).find(_.toString.toLowerCase == value)
  }
}

case object Ekonomi extends Category {
  val name = "ekonomi"
  val label = 0.0
}

case object Magazin extends Category {
  val name = "magazin"
  val label = 1.0
}

case object Saglik extends Category {
  val name = "saglik"
  val label = 2.0
}

case object Siyasi extends Category {
  val name = "siyasi"
  val label = 3.0
}

case object Spor extends Category {
  val name = "spor"
  val label = 4.0
}

case object InvalidCategory extends Category {
  val name = "Invalid"
  val label = 5.0
}

case class LabeledText(id: Long, category: String, text: String)