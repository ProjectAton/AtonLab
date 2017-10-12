package model

import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.language.implicitConversions

/**
  * Created by camilosampedro on 10/12/16.
  */
package object json {


  implicit val loginJsonReads: Reads[LoginJson] = Json.reads[LoginJson]
  implicit val computerJsonWrites: Writes[ComputerJson] = Json.writes[ComputerJson]
  implicit val computerJsonReads: Reads[ComputerJson] = Json.reads[ComputerJson]
  implicit val laboratoryWrites: Writes[LaboratoryJson] = Json.writes[LaboratoryJson]
  implicit val laboratoryReads: Reads[LaboratoryJson] = Json.reads[LaboratoryJson]
  implicit val extrasWrites: Writes[ResultMessageExtra] = Json.writes[ResultMessageExtra]
  implicit val resultMessageWrites: Writes[ResultMessage] = new Writes[ResultMessage] {
    override def writes(o: ResultMessage): JsObject = Json.obj(
      "result" -> o.result,
      "extras" -> Json.parse(o.extras.map(e => "\"" + e.key + "\": \"" + e.extra + "\"").mkString("{", ",", "}"))
    )
  }
  implicit val ResultMessageExtraF: Writes[ResultMessageExtra] = new Writes[ResultMessageExtra] {
    override def writes(o: ResultMessageExtra) = Json.obj(o.key -> o.extra)
  }
  /*implicit val resultMessageReads: Reads[ResultMessage] = (
    (__ \ "result").read[String] and
      (__ \ "extras").read[Seq[ResultMessageExtra]]
    ) (ResultMessage.apply _)*/

  implicit val sshOrderJsonReads: Reads[SSHOrderJson] = Json.reads[SSHOrderJson]

  implicit def convertToExtra(raw: (String, String)): ResultMessageExtra = ResultMessageExtra(raw._1, raw._2)
}
