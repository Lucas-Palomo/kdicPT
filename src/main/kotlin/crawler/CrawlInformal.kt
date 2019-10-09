package crawler

import core.Utils
import org.json.simple.JSONObject
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException

class CrawlInformal {

    var utils = Utils();

    private fun makeurl(word: String): String {
        return "https://www.dicionarioinformal.com.br/${utils.removerAcentos(word.toLowerCase(), "unico")}"
    }

    fun cat(word: String): JSONObject {
        try {
            val doc = Jsoup.connect(makeurl(word)).get()
            return collect(doc)
        } catch (e: HttpStatusException) {
            utils.getFile("logs", "unknowInformal").appendText("$word\n")
        } catch (e: IOException) {
            utils.getFile("logs", "errorInformal").appendText("$e/$word\n")
        }
        return JSONObject()
    }

    private fun collect(doc: Document): JSONObject {
        return JSONObject()
    }

}