package logic.domain

import play.api.libs.json.Json._
import java.util.Date
import play.api.Logger
import play.api.libs.json._
import play.api.libs.functional.syntax._
import logic.exception.JsonParamsException
import org.awsm.rscommons.{StatsResponse, AuthObject, StatsRequest}

/**
 * Class represents JSON request parsing logic and forming StatsRequest.
 * Throws exception in case of wrong or absent parameters set.
 */
object RequestBuilder{

  def buildRequestFromJson(json: JsValue): StatsRequest = {

    /*TEST WITH

    curl -v -H "Content-Type: application/json" -X GET -d '{"jsonrpc": "2.0", "method": "getGamesStats","params": {"application":"Cut The Rope", "store":"appstore", "dates":["2012-01-01","20120-01-02"],"rankType":"inapp", "countries":["USA","Canada"], "authObject":{"username":"anton", "password":"secret"}}, "id": "1"}' http://localhost:9000/rpc.json

    */

    val params = (json  \ "params" ).asOpt[JsValue] match {
      case Some(value) => value //validate for far future
      case None => throw JsonParamsException("No \"params\" field found in JSON request!")
    }

    val application = getParameterFromJson(params, "application")
    val store = getParameterFromJson(params, "store")
    val rankType = getParameterFromJson(params, "rankType")

    val dates = getListFromJson(params, "dates")
    val countries = getSetFromJson(params, "countries")

    val authObject = getAuthObjectFromJson(params)

    new StatsRequest(application, store, rankType, dates, countries, authObject)
  }


  def getParameterFromJson(json: JsValue, fieldName: String): String = {
    (json \ fieldName).asOpt[String] match {
      case Some(value) => value
      case None => throw JsonParamsException("Parameter \""+fieldName+"\"` not specified in request!")
    }
  }

  def getListFromJson(json: JsValue, fieldName: String): List[String] = {
    (json \ fieldName).asOpt[List[String]] match {
      case Some(value) => value
      case None => throw JsonParamsException("Parameters \""+fieldName+"\"` not specified in request!")
    }
  }

  def getSetFromJson(json: JsValue, fieldName: String): Set[String] = {
    (json \ fieldName).asOpt[Set[String]] match {
      case Some(value) => value
      case None => throw JsonParamsException("Parameters \""+fieldName+"\"` not specified in request!")
    }
  }

  def getAuthObjectFromJson(json: JsValue): AuthObject = {

    val auth: JsValue = (json \ "authObject").asOpt[JsValue] match {
      case Some(value) => value
      case None => throw JsonParamsException("Auth Object not specified in request!")
    }

    new AuthObject(getParameterFromJson(auth, "username"),getParameterFromJson(auth, "password"))
  }


  def buildJsonResponse(data: StatsResponse): JsValue = {
    JsObject(
      Seq(
        "application" -> JsString(data.application),
        "store" -> JsString(data.store),

            if(data.rankings != null) { "rankings" -> Json.toJson (data.rankings)} else {"rankings" -> JsNull},

        "error" -> JsString(data.error)
      ))
  }
  //todo: get and validate dates in extra method
}