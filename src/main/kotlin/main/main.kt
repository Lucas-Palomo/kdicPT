package main

import crawler.*
import org.json.simple.JSONArray
import org.json.simple.JSONObject

fun main(args: Array<String>) {

    val palavras = JSONArray()
    val crawlerDicio = CrawlDicio();
    val crawlerPensador = CrawlPensador()
    val crawlerSinonimos = CrawlSinonimos()
    val crawlAntonimos = CrawlAntonimos()
    val crawlNomes = CrawlNomes()

    for (word in listOf("saul","fsadasda")) {
        val palavra = JSONObject()
        palavra.put("pessoa",crawlNomes.getNome(word))
//        val palavra = crawlerDicio.cat(word)
//        palavra.put("sinonimos",crawlerSinonimos.getSinonimos(word))
//        palavra.put("antonimos",crawlAntonimos.getAntonimos(word))
//        palavra.put("frases",crawlerPensador.getFrases(word))
        println(palavra.toJSONString()+"\n")
    }


}