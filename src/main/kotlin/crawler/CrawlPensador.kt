package crawler

import core.Utils
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Node
import java.io.IOException

class CrawlPensador {

    var utils = Utils()

    private fun makeurl(word: String, index: Int): String {
        return "https://www.pensador.com/$word/$index"
    }

    fun getFrases(word: String): JSONArray {
        val frases = JSONArray()
        try {
            var index = 1;
            while (index <= 10) {
                val doc = Jsoup.connect(makeurl(word, index)).get()
                val frasesColetadas = collect(doc)
                if (!frases.containsAll(frasesColetadas)) {
                    frases.addAll(frasesColetadas)
                } else {
                    break;
                }
                index++
            }
        } catch (e: HttpStatusException) {
            utils.getFile("logs","unknowPensador").appendText("$word\n")
        } catch (e: IOException) {
            utils.getFile("logs","errorPensador").appendText("$e/$word\n")
        }
        return frases
    }

    private fun collect(doc: Document): JSONArray {
        val frasesColetadas = JSONArray()
        for (card in doc.getElementsByClass("thought-card")) {
            val fraseObject = JSONObject()
            val fr0 = card.getElementsByClass("frase")[0]
            val autor = card.getElementsByClass("autor")[0].text()
            if (fr0.children().size == 0) {
                fraseObject.put(autor.toLowerCase(), utils.sentenceClear(fr0.text().toLowerCase()))
                frasesColetadas.add(fraseObject)
            }
        }
        return frasesColetadas
    }

}