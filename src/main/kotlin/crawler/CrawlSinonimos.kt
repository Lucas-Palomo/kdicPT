package crawler

import core.Utils
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import javax.lang.model.element.Element

class CrawlSinonimos() {

    var utils = Utils();

    private fun makeurl(word: String): String {
        return "https://www.sinonimos.com.br/${utils.removerAcentos(word.toLowerCase(), "dicio")}"
    }

    fun getSinonimos(word: String): JSONArray {
        val sinonimos = JSONArray()
        try {
            val doc = Jsoup.connect(makeurl(word)).get()
            sinonimos.addAll(collect(doc))
        } catch (e: HttpStatusException) {
            utils.getFile("logs", "unknowSinonimos").appendText("$word\n")
        } catch (e: IOException) {
            utils.getFile("logs", "errorSinonimos").appendText("$e/$word\n")
        }
        return sinonimos
    }

    private fun collect(document: Document): JSONArray {
        val sinonimosColetados = JSONArray()

        val tags = document.getElementsByClass("s-wrapper")

        for (tag in tags) {

            if (tag.childNodeSize() > 1) {
                val definicao = JSONObject()
                val sinonimos = JSONArray()
                var sentido = ""

                val tagSentido = tag.children()[0]
                if (tagSentido.hasClass("sentido")) {
                    sentido = tagSentido.text().toLowerCase().trim();
                }

                val tagSinonimos = tag.children()[1]
                if (tagSinonimos.hasClass("sinonimos")) {
                    for (sinonimo in tagSinonimos.text().split(",")) {
                        sinonimos.add(utils.replaceNumber(sinonimo))
                    }
                }

                definicao.put(sentido, sinonimos)
                sinonimosColetados.add(definicao)
            } else {
                for (sinonimo in tag.children()) {
                    for (sin in sinonimo.text().split(",")) {
                        sinonimosColetados.add(utils.replaceNumber(sin))
                    }
                }
            }
        }
        return sinonimosColetados;
    }

}