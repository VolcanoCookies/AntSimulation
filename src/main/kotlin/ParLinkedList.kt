data class Node<T>(
    val value: T,
    val next: Node<T>
)

class ParLinkedList<T>(val chunkSize: Int) {
    val sections: ArrayList<Pair<Int, Node<T>>> = ArrayList()
    val head: Node<T> =
}