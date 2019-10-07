package crawler

import core.Utils
import org.json.simple.JSONObject
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.IOException

class CrawlNomes {

    var utils = Utils();

    private fun makeurl(word: String): String {
        return "https://www.dicionariodenomesproprios.com.br/${utils.removerAcentos(word.toLowerCase(), "dicio")}"
    }

    fun getNome(word: String): JSONObject {
        try {
            val doc = Jsoup.connect(makeurl(word)).get()
            return collect(doc)
        } catch (e: HttpStatusException) {
            utils.getFile("logs", "unknowNomes").appendText("$word\n")
        } catch (e: IOException) {
            utils.getFile("logs", "errorNomes").appendText("$e/$word\n")
        }
        return JSONObject()
    }

    private fun collect(doc: Document): JSONObject {
        val persona = JSONObject()

        val titles = doc.getElementsByTag("h1")
        for (title in titles) {
            if (title.children()[0].hasClass("fa fa-male")) {
                persona.put("sexo", "masculino")
            }
            if (title.children()[0].hasClass("fa fa-female")) {
                persona.put("sexo", "feminino")
            }
        }

        return persona
    }

}