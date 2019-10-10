package core

import java.io.File
import java.lang.StringBuilder
import java.util.HashMap
import java.nio.charset.StandardCharsets


class Utils {

    var especiaisUnicos = object : LinkedHashMap<String, List<String>>() {
        init {
            put("a", listOf("á", "à", "â", "ã"))
            put("e", listOf("é", "ê"))
            put("i", listOf("í"))
            put("o", listOf("ó", "ô", "õ"))
            put("u", listOf("ú", "ü"))
        }
    }

    var dicioEspeciais = object : HashMap<String, List<String>>() {
        init {
            put("beca-3", listOf("beca"))
        }
    }

    var dicioUnico = object : LinkedHashMap<String, List<String>>() {
        init {
            put("a-2", listOf("á"))
            put("a-3", listOf("à"))
            put("e-4", listOf("é"))
            put("o-4", listOf("ó"))
            put("c-2", listOf("ç"))
        }
    }

    fun removerAcentos(palavra: String, tipo: String): String {
        when (tipo) {
            "unico" -> {
                if (palavra.length > 1) {
                    return replaceWord(palavra.toLowerCase(), especiaisUnicos)
                } else {
                    return palavra.toLowerCase()
                }
            }
            "dicio" -> {
                if (palavra.contains(" ")) {
                    return replaceWord(
                        palavra.toLowerCase(),
                        dicioEspeciais
                    ).replace(" ", "-")
                } else {
                    if (palavra.length > 1) {
                        especiaisUnicos.put("c", listOf("ç"))
                        dicioEspeciais.putAll(especiaisUnicos)
                        return replaceWord(palavra.toLowerCase(), dicioEspeciais)
                    } else {
                        return replaceWord(palavra.toLowerCase(), dicioUnico)
                    }
                }
            }
        }
        return palavra;
    }

    private fun replaceWord(palavra: String, mapa: HashMap<String, List<String>>): String {
        var novaPalavra = palavra;
        for ((semAcento, listaAcentos) in mapa) {
            for (acento in listaAcentos) {
                if (novaPalavra.contains(acento)) {
                    novaPalavra = novaPalavra.replace(acento, semAcento);
                }
            }
        }
        return novaPalavra;
    }

    fun sentenceClear(sentence: String): String {
        val outputString = StringBuilder()
        val re = Regex("[-;\\/:*?\"<>|&']")
        for (word in sentence.split(" ")) {
            outputString.append(re.replace(decode(word), " ").trim())
            outputString.append(" ")
        }
        return outputString.toString().trimEnd().toLowerCase().trimStart()
    }

    private fun decode(input: String): String {
        return input.replace("\u2013","")
                    .replace("\u201C", "")
                    .replace("\u201D", "")
    }

    fun getFile(dir:String,name: String): File {
        val file = File("$dir/$name.log")
        if (!file.exists()) {
            file.createNewFile()
        }
        return file;
    }

    fun removerNumeros(word:String):String{
        return Regex("[0-9]|[.]").replace(word,"").trim().toLowerCase();
    }


}


/*
private fun replaceWord(palavra: String, mapa: HashMap<String, List<String>>): String {
    var novaPalavra = palavra;
    for (char in palavra) {
        for ((semAcento, listaAcentos) in mapa) {
            if (listaAcentos.contains(char.toString())) {
                novaPalavra = novaPalavra.replace(char.toString(), semAcento)
            }
        }
    }
    return novaPalavra;
}
*/
