package crawler

import core.Utils
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException

class CrawlInformal {

    var utils = Utils();

    var informal = JSONObject();
    private val definicoesArray = JSONArray()
    private val exemplosArray = JSONArray()
    private val sinonimosArray = JSONArray()
    private val antonimosArray = JSONArray()
    private val relacionadasArray = JSONArray()

    private fun makeurl(word: String): String {
        return "https://www.dicionarioinformal.com.br/${utils.removerAcentos(word.toLowerCase(), "unico")}"
    }

    private fun save(){
        informal.put("sinonimos", sinonimosArray)
        informal.put("antonimos", antonimosArray)
        informal.put("relacionados", relacionadasArray)
        informal.put("definicoes", definicoesArray)
        informal.put("exemplos", exemplosArray)
    }

    private fun reset(){
        sinonimosArray.clear()
        antonimosArray.clear()
        relacionadasArray.clear()
        definicoesArray.clear()
        exemplosArray.clear()
    }

    fun cat(word: String): JSONObject {
        reset()
        try {
//            for(type in listOf("","sinonimos","antonimos","relacionadas","exemplos","flexoes","rimas","reversa")){
            for (type in listOf("", "sinonimos", "antonimos","relacionadas")) {
                var contains = false
                var index = 1
                while (!contains) {
                    if (index <= 10) {
                        val doc = Jsoup.connect(makeurl("$type/$word/$index")).get()
                        contains = collect(doc, type)
                        index++
                    } else {
                        contains = true
                    }
                }
            }

            sinonimosArray.remove("opa!")
            sinonimosArray.remove(word)
            antonimosArray.remove("opa!")
            antonimosArray.remove(word)
            relacionadasArray.remove("opa!")
            relacionadasArray.remove(word)
            save()

        } catch (e: HttpStatusException) {
            utils.getFile("logs", "unknowInformal").appendText("$word\n")
        } catch (e: IOException) {
            utils.getFile("logs", "errorInformal").appendText("$e/$word\n")
        }
        return informal
    }

    private fun collect(doc: Document, type: String): Boolean {
        when (type) {
            "" -> {
                val contents = doc.getElementsByAttributeValue("itemtype", "http://schema.org/CreativeWork")
                return definicoes(contents)

            }
            "sinonimos" -> {
                val contents = doc.getElementsByTag("h3")
                return antSinRel(contents, type)
            }
            "antonimos" -> {
                val contents = doc.getElementsByTag("h3")
                return antSinRel(contents, type)
            }
            "relacionadas" -> {
                val contents = doc.getElementsByTag("h3")
                return antSinRel(contents, type)
            }
        }
        return true
    }

    private fun antSinRel(elements: Elements, type: String): Boolean {
        val coletados = JSONArray()
        for (tag in elements) {
            if (tag.hasClass("di-blue")) {
                val sin = utils.sentenceClear(utils.removerNumeros(tag.text()))
                coletados.add(sin)
            }
        }

        when (type) {
            "sinonimos" -> if (!sinonimosArray.containsAll(coletados)) {
                sinonimosArray.addAll(coletados)
                return false
            }
            "antonimos" -> if (!antonimosArray.containsAll(coletados)) {
                antonimosArray.addAll(coletados)
                return false
            }
            "relacionadas" -> if (!relacionadasArray.containsAll(coletados)) {
                relacionadasArray.addAll(coletados)
                return false
            }
        }
        return true
    }

/*
    private fun sinonimos(elements: Elements): Boolean {
        val sinonimosColetados = JSONArray()
        for (tag in elements) {
            if (tag.hasClass("di-blue")) {
                val sin = utils.sentenceClear(utils.removerNumeros(tag.text()))
                sinonimosColetados.add(sin)
            }
        }

        if (!sinonimosArray.containsAll(sinonimosColetados)) {
            sinonimosArray.addAll(sinonimosColetados)
            return false
        }
        return true
    }
*/

    private fun definicoes(elements: Elements): Boolean {
        val exemplosColetados = JSONArray()
        val definicoesColetadas = JSONArray()
        for (tags in elements) {
            var definicao = ""
            var exemplo = ""
            for (tagCard in tags.children()) {
                if (tagCard.hasClass("card-body")) {
                    for (tag in tagCard.children()) {
                        if (tag.nodeName() == "p") {
                            definicao = utils.sentenceClear(utils.removerNumeros(tag.text()))
                        }
                        if (tag.nodeName() == "blockquote") {
                            exemplo = utils.sentenceClear(utils.removerNumeros(tag.text()))
                        }
                    }
                }
                if (tagCard.hasClass("card-action")) {
                    for (tag in tagCard.children()) {

                        val votes = tag.getElementsByClass("di_card_action link_cinza")
                        val liked = votes[0].text()
                        val unliked = votes[1].text()
                        if (unliked.toInt() < liked.toInt()) {
                            exemplosColetados.add(exemplo)
                            definicoesColetadas.add(definicao)
                        }
                    }
                }
            }
        }
        if (!definicoesArray.containsAll(definicoesColetadas) && !exemplosArray.containsAll(exemplosColetados)) {
            definicoesArray.addAll(definicoesColetadas)
            exemplosArray.addAll(exemplosColetados)
            return false
        }
        return true
    }

}