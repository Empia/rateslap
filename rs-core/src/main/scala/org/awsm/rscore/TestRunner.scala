package org.awsm.rscore

import appannie.{AppAnnieXMLParser, AppAnnieCrawler, AppAnnieDispatcher}
import org.awsm.rscommons.{AuthObject, StatsRequest}
import xml.XML


object TestRunner extends App {
  /*val dispatcher = new AppAnnieDispatcher

  val response = dispatcher.getData(StatsRequest("someMethod",  "MegaStore", "SouthAfrica", AuthObject("user", "secret")))

  println("Resp JSON: "+response.generateJson())*/


  //http://www.appannie.com/app/ios/cut-the-rope-hd/ranking/#view=ranks&date=2012-10-09

  val date = "2012-10-09"
  val appName = "Cut the rope"
  val store = "ios"
  val rankType = "ranks"
                                //http://www.appannie.com/app/ios/cut-the-rope/ranking/#view=ranks&date=2012-10-09



  val crawler = new AppAnnieCrawler(date, appName, store, rankType)

  val xml: String = crawler.crawl(new AuthObject("akirillov@zeptolab.com", "7ru57n01")) match {
    case None => "error"
    case Some(page) => page
  }

  println(xml)

  val source = XML.loadString(xml)
  val parser = new AppAnnieXMLParser()

  val result = parser.parse(source)

 /* val xml = crawler.crawl()


  val source = XML.loadString(xml)
  val parser = new AppAnnieXMLParser()

  val result = parser.parse(source)*/
  println(xml)
}
