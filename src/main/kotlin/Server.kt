import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

fun main() {
    val server = UmutServer(5547)
        server.startServer()

}


class UmutServer {
    private var serverSocket: ServerSocket?
    private lateinit var lock: Any

    constructor(port: Int) {
        try {
            serverSocket = ServerSocket(port)
            lock = Object()
        } catch (ioe: IOException) {
            println(ioe)
            serverSocket = null
        }
    }

    fun startServer() {
        println("The server has started ")
        println("Listening on port 5547" )
        try {
            while (true) {
                val socket = serverSocket!!.accept()
                synchronized(lock) {
                    val client: SokobanClient = SokobanClient(socket)
                    client.letSGo()
                }
            }
        } catch (ioe: IOException) {
            println(ioe)
        }

    }
}
class SokobanClient : Runnable {
    private val file: FileResource
    private val socket: Socket
    private val thread: Thread

    constructor(socket: Socket) {
        this.socket = socket
        thread = Thread(this)
        file = FileResource()
    }

    fun letSGo() {
        thread.start()
    }

    override fun run() {
        try {

            val inputStream = socket.getInputStream()
            val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
            val input = BufferedReader(inputStreamReader)
            val level = input.readLine()

            val outputStream: OutputStream = socket.getOutputStream()
            val out: PrintWriter = PrintWriter(outputStream)
            val answer =
                file.loadLevelFromFile("/home/developer/Umut_Arpidinov/Sokoban_Server/src/main/kotlin/levels/level"+level+".sok")
            if (answer != null){
                out.println(answer)
		println("level"+level+ ".sok was sent")
                println("I sent a message to client")
            }else{
                println("Android_iOS")
            }
            out.flush()
            inputStreamReader.close()
            input.close()
            out.close()
            outputStream.close()
            socket.close()

        } catch (e: IOException) {
            println(e)
        }

    }


}

class FileResource {
    fun loadLevelFromFile(filename: String): String? {
        var text = ""
        var file: File = File(filename)
        var size = file.length().toInt()
        var array: CharArray? = CharArray(size)
        val iN: FileInputStream = FileInputStream(filename)
        try {
            var unicode: Int
            var index = 0
            while (iN.read().also { unicode = it } != -1) {
                val symbol = unicode.toChar()
                if ('0' <= symbol && symbol <= '4') {
                    array!![index] = symbol
                    index = index + 1
                } else if (symbol == '\n') {
                    array!![index] = 'A'
                    index += 1
                }
            }
            if (array!![index] != '\n') {
                array[index] = 'A'
            }
            text = String(array, 0, index)
            iN.close()
            return text

        } catch (e: FileNotFoundException) {
            println(e)
        }

        return text
    }
}



