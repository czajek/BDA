import scalikejdbc.ConnectionPool

trait DbSetup {

  // initialize JDBC driver & connection pool
  Class.forName("com.mysql.jdbc.Driver")
  private val URL = "jdbc:mysql://192.168.57.3/retail_db"
  private val USERNAME = "retail_dba"
  private val PASSWORD = "cloudera"
  ConnectionPool.singleton(URL, USERNAME, PASSWORD)

}
