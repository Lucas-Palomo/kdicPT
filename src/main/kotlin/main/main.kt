package main

import crawler.CrawlAntonimos
import crawler.CrawlDicio
import crawler.CrawlPensador
import crawler.CrawlSinonimos
import org.json.simple.JSONArray
import org.json.simple.JSONObject

fun main(args: Array<String>) {

    val palavras = JSONArray()
    val crawlerDicio = CrawlDicio();
    val crawlerPensador = CrawlPensador()
    val crawlerSinonimos = CrawlSinonimos()
    val crawlAntonimos = CrawlAntonimos()

    for (word in listOf("esperdiçador","verborrágico","1","gostar")) {
//        val palavra = crawlerDicio.cat(word)
        val palavra = JSONObject()
        palavra.put("sinonimos",crawlerSinonimos.getSinonimos(word))
        palavra.put("antonimos",crawlAntonimos.getAntonimos(word))
//        palavra.put("frases",crawlerPensador.getFrases(word))
        println(palavra.toJSONString()+"\n")
    }


}