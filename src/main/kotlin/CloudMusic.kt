import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class SongResponse(
    val songs: List<Song>
)

@Serializable
data class Song(
    val id: Long,
    val name: String,
    val artists: List<Artist>
)

@Serializable
data class Artist(
    val name: String
)

@Serializable
data class Lyric(
    val songStatus: Int,
    val lyricVersion: Int,
    val lyric: String,
//    val code: Int
)

/**
 * @author Takeoff0518
 */
object CloudMusic {

    private suspend fun getRedirectUrl(url: String): String {

        val client = HttpClient(CIO)

        return try {
            val response = client.get(url)
            println("重定向后的 URL：$response")
            response.call.request.url.toString()
        } catch (e: Exception) {
            println("请求失败：${e.message}")
            url
        } finally {
            client.close()
        }
    }

    suspend fun downloadSong(songId: Long, name: String) {
        val url = getRedirectUrl("http://music.163.com/song/media/outer/url?id=$songId")
        val httpClient = HttpClient()
        try {
            val response: HttpResponse = httpClient.get(url)
            val fileBytes: ByteArray = response.readBytes()
            val outputFile = File("./songs/$name.mp3")
            outputFile.writeBytes(fileBytes)
            println("文件已下载到 ${outputFile.absolutePath}")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            httpClient.close()
        }
    }

    suspend fun fetchLyrics(songId: Long): Lyric {
        val client = HttpClient()
        val response: HttpResponse = client.get("https://music.163.com/api/song/media/") {
            parameter("id", songId)
//            parameter("ids","%5B$songId%5D")
            parameter("encode", "json")
            parameter("charset", "utf-8")
        }
        val responseBody = response.bodyAsText()
        client.close()
        val json = Json { ignoreUnknownKeys = true }
        return json.decodeFromString(Lyric.serializer(), responseBody)
    }

    suspend fun fetchInfo(songId: Long): Song {
        val client = HttpClient()
        val response: HttpResponse = client.get("http://music.163.com/api/song/detail/") {
//            parameter("id", songId)
            parameter("ids", "[$songId]")
            parameter("encode", "json")
            parameter("charset", "utf-8")
        }
        val responseBody = response.bodyAsText()
        client.close()
        val json = Json { ignoreUnknownKeys = true }
        val songResponse = json.decodeFromString(SongResponse.serializer(), responseBody)
        return songResponse.songs.first()
    }

}


