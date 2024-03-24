import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.*

fun main(args: Array<String>) {
    File("./songs").mkdirs()
    while (true) {
        val id = Scanner(System.`in`).nextLong()
        runBlocking {
            val info = CloudMusic.fetchInfo(id)
            var name = info.artists.first().name
            for (i in info.artists.drop(1)) {
                name += ", " + i.name
            }
            name += "-" + info.name
//            name = name.replace(".", "")
//            name = name.replace("/", "")
//            name = name.replace("\\", "")
//            name = name.replace("?", "")
//            name = name.replace("*", "")
//            name = name.replace("<", "")
//            name = name.replace(">", "")
//            name = name.replace("\"", "")
//            name = name.replace(":", "")
            name = name.replace("[./\\\\?*<>\":]".toRegex(), "")
            println("下载音频中...")
            CloudMusic.downloadSong(id, name)
            println("下载歌词中...")
            try {
                val lyrics = CloudMusic.fetchLyrics(id).lyric
                File("./songs/$name.lrc").writeText(lyrics)
            } catch (e: Exception) {
                println("歌词下载失败：$e")
            }
        }
    }

}