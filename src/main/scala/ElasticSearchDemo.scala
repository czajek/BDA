import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.HttpClient
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import scalikejdbc.WrappedResultSet
import scalikejdbc._

case class Product(productId: Long, price: BigDecimal, description: String, categoryName: String)

object Product extends SQLSyntaxSupport[Product] {
  override val tableName = "products"

  def apply(rs: WrappedResultSet) = new Product(
    rs.long("product_id"), rs.bigDecimal("product_price"), rs.string("product_description"), rs.string("category_name"))
}

object ElasticSearchDemo extends App with DbSetup {

  val elasticClient = HttpClient(ElasticsearchClientUri("localhost", 9200))

  // ad-hoc session provider on the REPL
  implicit val session = AutoSession

  val products: List[Product] = sql"select * from products join categories on category_id=product_category_id".map(rs => Product(rs)).list.apply()

  products.foreach(p => createProductInElastic(p))

  elasticClient.close()

  def createProductInElastic(p: Product) = {

    import com.sksamuel.elastic4s.http.ElasticDsl._
    import org.json4s.jackson.Serialization.write

    implicit val formats = Serialization.formats(NoTypeHints)

    val productAsJson = write(p)


    elasticClient.execute {
      indexInto("products" / p.categoryName.replace("&", "and").replace(" ", "")).doc(productAsJson.replace("&", "and").trim).refresh(RefreshPolicy.IMMEDIATE)
    }.await

  }

}
