import java.util.*

data class Pair<T, K>(var first: T, var second: K)

class ParLinkedList<T>(val chunkSize: Int) {
    val chunks: ArrayList<LinkedList<T>> = ArrayList()
    private var openChunks: LinkedList<LinkedList<T>> = LinkedList()

    val size: Int
        get() {
            var sz = 0
            chunks.forEach { sz += it.size }
            return sz
        }

    private fun createNewChunk() {
        val newChunk = LinkedList<T>()
        openChunks.add(newChunk)
        chunks.add(newChunk)
    }

    fun add(value: T) {
        if (openChunks.size == 0) createNewChunk()
        val chunk = openChunks[0]

        chunk.add(value)
        if (chunk.size >= chunkSize) {
            openChunks.remove(chunk)
        }
    }

    fun sync() {
        openChunks = LinkedList()
        for (chunk in chunks) {
            if (chunk.size < chunkSize) {
                openChunks.add(chunk)
            }
        }
    }
}