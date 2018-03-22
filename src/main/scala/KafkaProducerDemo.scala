import java.util.Properties

import org.apache.kafka.clients.producer.KafkaProducer
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write
import scalikejdbc.WrappedResultSet
import scalikejdbc._

case class Customer(id: Long, firstName: String, lastName: String, email: String, password: String, street: String, city: String, state: String, zipcode: String)

object Customer extends SQLSyntaxSupport[Customer] {
  override val tableName = "customers"

  def apply(rs: WrappedResultSet) = new Customer(
    rs.long("customer_id"), rs.string("customer_fname"), rs.string("customer_lname"), rs.string("customer_email"),
    rs.string("customer_password"), rs.string("customer_street"), rs.string("customer_city"), rs.string("customer_state"), rs.string("customer_zipcode"))
}

object KafkaProducerDemo extends App with DbSetup with KafkaSetup {

  // ad-hoc session provider on the REPL
  implicit val session = AutoSession

  val producer = new KafkaProducer[String, String](props)

  val customers: List[Customer] = sql"select * from customers".map(rs => Customer(rs)).list.apply()

  customers.foreach(c => sendCustomersToKafka(c))

  producer.close()

  def sendCustomersToKafka(customer: Customer) = {

    import org.apache.kafka.clients.producer._

    implicit val formats = Serialization.formats(NoTypeHints)

    val customerAsJson = write(customer)

    val TOPIC="customers"

    val record = new ProducerRecord(TOPIC, customer.id.toString, customerAsJson)
    producer.send(record)


  }

}
