package org.awsm.rscore.appannie

import org.awsm.rscore.{XmlParser}
import org.awsm.rscommons.StatsResponse
import org.awsm.rscore.exception.ParsingException
import xml.{XML, Node, NodeSeq, Elem}

/**
 * Created by: akirillov
 * Date: 10/23/12
 */

class AppAnnieXMLParser extends XmlParser{
  override def parse(input: String) = {
    val source = XML.loadString(input)

    val countries = (source \\ "tr" filter(p  =>  (p \ "@class").text.equals("ranks") && !((p \\ "a" text).equals("")))).toList

    val ranks = (source \\ "tr" filter(p  =>  (p \ "@class").text.equals("ranks")))
      .filter (n => ((n \\ "td").size > 0) && ((n \\ "td").head \ "@title" text).equals("Rank #"))
      .map(r => (r \\ "td").head)
    
    if ((ranks.size <= 0)||(countries.size <= 0)||(ranks.size != countries.size)){
      throw ParsingException("Internal parser error! Ranks and Countries sizes are invalid, please check "+getClass.getName+" parser!")
    }

    def createPair(country: Node, rank: Node): Pair[String, String] = {
      val result = ( (country \\ "a" text).replaceAll("\\n", "").trim(), rank.text.replaceAll("\\n", "").trim() )
      //todo: validate values and throw exception
      result
    }

    def createRanks(countriesList: List[Node], ranksList: List[Node]): List[Pair[String, String]] = (countriesList, ranksList) match {
      case (List(), _) | (_, List()) => List()
      case (_ , _) => {
        try{
          createPair(countriesList.head, ranksList.head) :: createRanks(countriesList.tail,ranksList.tail)
        } catch {
          case t :Throwable => t.printStackTrace(); throw ParsingException("Internal parser error. AppAnnie HTML markup might have been changed and parsers were not able to parse it. Please check new markup and update corresponding parsers.")
        }
      }
    }


    try{
      val pairs = createRanks(countries, ranks.toList)
      pairs
    } catch {
      case t :Throwable => t.printStackTrace(); throw ParsingException("Internal parser error. AppAnnie HTML markup might have been changed and parsers were not able to parse it. Please check new markup and update corresponding parsers.")
    }
  }
}
