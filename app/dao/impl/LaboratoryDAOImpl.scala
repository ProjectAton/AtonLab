package dao.impl

import javax.inject.Inject

import com.google.inject.Singleton
import dao.{LaboratoryDAO, RoomDAO}
import model.table._
import model._
import play.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits._
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
  * Se encarga de implementar las acciones sobre la base de datos
  *
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  * @param dbConfigProvider Inyección del gestor de la base de datos
  */
@Singleton
class LaboratoryDAOImpl @Inject()
(dbConfigProvider: DatabaseConfigProvider, salaDAO: RoomDAO) extends LaboratoryDAO {
  /**
    * Configuración de la base de datos
    */
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._


  /**
    * Tabla con "todos los laboratorios", similar a select * from laboratory
    */
  implicit val laboratories = TableQuery[LaboratoryTable]
  implicit val rooms = TableQuery[RoomTable]
  implicit val computers = TableQuery[ComputerTable]
  implicit val computerStates = TableQuery[ComputerStateTable]
  implicit val connectedUsers = TableQuery[ConnectedUserTable]
  implicit val computersAndRoomsPentaJoin = {
    laboratories joinLeft rooms on {_.id === _.laboratoryId } joinLeft computers  on {(x,y)=>x._2.map(_.id)===y.roomId} joinLeft computerStates on {(x,y)=>x._2.map(_.ip)===y.computerIp} joinLeft connectedUsers on {(x,y)=>x._2.map(_.computerIp)===y.computerStateComputerIp && x._2.map(_.registeredDate) === y.computerStateRegisteredDate}
  }

  /**
    * Adiciona un laboratory
    *
    * @param laboratory Laboratory a agregar
    * @return String con el mensaje del result
    */
  override def add(laboratory: Laboratory): Future[String] = {
    // Se realiza un insert y por cada insert se crea un String
    Logger.debug(s"""Adding to database: $laboratory""")
    db.run(laboratories += laboratory).map(res => "Laboratory agregado correctamente").recover {
      case ex: Exception =>
        Logger.error("Ocurrió un error al adicionar en la base de datos", ex)
        ex.getCause.getMessage
    }
  }

  /**
    * Elimina un laboratory de la base de datos
    *
    * @param id Identificador del laboratory
    * @return Resultado de la operación
    */
  override def delete(id: Long): Future[Int] = {
    db.run(search(id).delete)
  }

  private def search(id: Long) = laboratories.filter(_.id === id)

  /**
    * Lista todas los laboratorios en la base de datos
    *
    * @return Todos los laboratorios
    */
  override def listAll: Future[Seq[Laboratory]] = {
    db.run(laboratories.result)
  }

  /**
    * Obtiene el laboratory con todos las rooms y PC asociadas
    *
    * @param id
    * @return
    */
  override def getWithChildren(id: Long): Future[Seq[(Laboratory, Option[Room], (Option[Computer],Option[ComputerState],Option[ConnectedUser]))]] = {
    db.run {
      computersAndRoomsPentaJoin
        .filter(_._1._1._1._1.id === id)
        .map(x => (x._1._1._1._1, x._1._1._1._2, (x._1._1._2,x._1._2,x._2)))
        .result
    }
  }

  /**
    * Obtiene un laboratory según el id
    *
    * @param id Identificador del laboratory
    * @return Laboratory encontrado o None si no se encontró
    */
  override def get(id: Long): Future[Option[Laboratory]] = {
    // Se realiza un select * from laboratory where id = $id
    db.run(search(id).result.headOption)
  }
}
