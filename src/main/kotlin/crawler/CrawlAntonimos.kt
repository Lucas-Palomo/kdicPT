package crawler

import core.Utils
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import javax.lang.model.element.Element

class CrawlAntonimos() {

    var utils = Utils();

    private fun makeurl(word: String): String {
        return "https://www.antonimos.com.br/${utils.removerAcentos(word.toLowerCase(), "dicio")}"
    }

    fun getAntonimos(word: String): JSONArray {
        val sinonimos = JSONArray()
        try {
            val doc = Jsoup.connect(makeurl(word)).get()
            sinonimos.addAll(collect(doc))
        } catch (e: HttpStatusException) {
            utils.getFile("logs", "unknowAntonimos").appendText("$word\n")
        } catch (e: IOException) {
            utils.getFile("logs", "errorAntonimos").appendText("$e/$word\n")
        }
        return sinonimos
    }

    private fun collect(document: Document): JSONArray {
        val antonimosColetados = JSONArray()

        val tags = document.getElementsByClass("s-wrapper")

        for (tag in tags) {
            if (tag.childNodeSize() > 1) {
                val definicao = JSONObject()
                val antonimos = JSONArray()
                var sentido = ""

                val tagSentido = tag.children()[0]
                if (tagSentido.hasClass("sentido")) {
                    sentido = tagSentido.text().toLowerCase().trim();
                }

                val tagAntonimos = tag.children()[1]
                if (tagAntonimos.hasClass("antonimos")) {
                    for (antonimo in tagAntonimos.text().split(",")) {
                        antonimos.add(utils.removerNumeros(antonimo))
                    }
                }

                definicao.put(sentido, antonimos)
                antonimosColetados.add(definicao)
            } else {
                for (antonimo in tag.children()) {
                    for (ant in antonimo.text().split(",")) {
                        antonimosColetados.add(utils.removerNumeros(ant))
                    }
                }
            }
        }
        return antonimosColetados;
    }

}