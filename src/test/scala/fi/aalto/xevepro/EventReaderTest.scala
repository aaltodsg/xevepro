package fi.aalto.xevepro

/**
 * Created by mikkorinne on 4.3.2015.
 */
object EventReaderTest extends App {
  val myReader = new EventReader("event_test.xml")
  print("First: \n"+myReader.readNext())
  print("Second: \n"+myReader.readNext())
  myReader.closeER()
}
