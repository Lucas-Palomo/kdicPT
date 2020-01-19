package main

import crawler.*
import org.json.simple.JSONArray
import org.json.simple.JSONObject

fun main(args: Array<String>) {

//    val palavras = JSONArray()
    val crawlerDicio = CrawlDicio();
    val crawlerPensador = CrawlPensador()
    val crawlerSinonimos = CrawlSinonimos()
    val crawlAntonimos = CrawlAntonimos()
    val crawlNomes = CrawlNomes()
    val crawlInformal = CrawlInformal()

    for (word in listOf("chuca","xuca","pau cagado")) {

        val palavra = crawlerDicio.cat(word)
//        val palavra = JSONObject()

        palavra.put("informal",crawlInformal.cat(word))
        palavra.put("pessoa",crawlNomes.getNome(word))
        palavra.put("sinonimos",crawlerSinonimos.getSinonimos(word))
        palavra.put("antonimos",crawlAntonimos.getAntonimos(word))
        palavra.put("frases",crawlerPensador.getFrases(word))
        println(palavra.toJSONString() + "\n")
    }


}
