package crawler

import core.Utils
import model.Palavra
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.parser.HtmlTreeBuilder
import org.jsoup.parser.Parser
import org.jsoup.select.Elements
import java.io.File
import java.io.IOException
import java.lang.StringBuilder

class CrawlDicio() {

    var utils = Utils();

    private fun makeurl(word: String): String {
        return "https://www.dicio.com.br/${utils.removerAcentos(word.toLowerCase(), "dicio")}"
    }

    fun cat(word: String): JSONObject {
        try {
            val doc = Jsoup.connect(makeurl(word)).get()
            return Classifier(doc)
        } catch (e: HttpStatusException) {
            utils.getFile("logs","unknowDicio").appendText("$word\n")
        } catch (e: IOException) {
            utils.getFile("logs","errorDicio").appendText("$e/$word\n")
        }
        return JSONObject()
    }

    private fun Classifier(doc: Document):JSONObject {
        val container = doc.getElementsByClass("container")[1]
        val content = container.getElementsByAttributeValue("itemprop","description")[0].allElements

        val palavra = JSONObject()
        val tipo = JSONArray()
        val indexes = arrayListOf<Int>()

        for ((index, tag) in content.withIndex()) {
            if (tag.attributes()["class"] == "cl") {
                indexes.add(index)
                tipo.add(tag.text())
            } else {
                if(tag.attributes()["class"] == "significado"){
                    palavra.put("significado",tag.text())
                }
                if (tag.attributes()["class"] == "etim") {
                    indexes.add(index)
                    val etim = tag.text().split(").")[1].toLowerCase().trimStart()
                    palavra.put("etimologia", etim)
                }
            }
        }

        palavra.put("tipo", tipo)
        palavra.put("significados", getDefinicoes(content, tipo, indexes))
        return palavra
    }


    private fun getDefinicoes(content: Elements, tipo: JSONArray, indexes: ArrayList<Int>): JSONObject {
        val significados = JSONObject()
        val regex = Regex("[\\[\\]]")
        for ((i, index) in indexes.withIndex()) {
            var num = index;
            val definicoes = JSONArray();
            if ((i + 1) < indexes.size) {
                num++
                while (num < indexes[i + 1]) {
                    val tag = content[num];
                    if (tag.text().contains(regex)) {
                        if (tag.attributes()["class"] == "tag") {
                            tag.text()
                            val sig = JSONObject()

                            sig.put(
                                regex.replace(tag.text(), ""),
                                tag.parent().text().replace(tag.text(), "").trimStart()
                            )
                            definicoes.add(sig)
                        }
                    } else {
                        if (tag.nodeName() != "br") {
                            definicoes.add(tag.text())
                        }
                    }
                    num++
                }
                significados.put(tipo[i], definicoes)
            }
        }
        return significados;
    }

    private fun getFrasesDicio(container: Element): JSONArray {
        val exemplosFrases = JSONArray()
        for (frases in container.getElementsByClass("frase")) {
            val fraseObj = JSONObject()
            for (tag in frases.allElements) {
                if (tag.nodeName() == "div") { // Trocar para div pega fontes do jornal
                    val autor = tag.getElementsByTag("em")[0].text()
                    val frase = tag.text().replace(autor, "").toLowerCase().trimStart()
                    fraseObj.put(autor, utils.sentenceClear(frase))
                    exemplosFrases.add(fraseObj)
                }
            }
        }
        return exemplosFrases
    }



}