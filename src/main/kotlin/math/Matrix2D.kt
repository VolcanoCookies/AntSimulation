package math

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Matrix2D<T>(width: Int, height: Int, init: (Int, Int) -> T) {

	private val list: MutableList<MutableList<T>> = ArrayList(width)

	init {
		repeat(width) { x ->
			list.add(ArrayList(height))
			repeat(height) { y ->
				list[x].add(init.invoke(x, y))
			}
		}
	}

	fun forEach(func: (T) -> Unit) {
		list.forEachIndexed { x, l ->
			l.forEachIndexed { y, t ->
				func.invoke(t)
			}
		}
	}

	fun forEachIndexed(func: (Int, Int, T) -> Unit) {
		list.forEachIndexed { x, l ->
			l.forEachIndexed { y, t ->
				func.invoke(x, y, t)
			}
		}
	}

	suspend fun forEachIndexedAsync(func: (Int, Int, T) -> Unit) {
		list.forEachIndexed { x, l ->
			GlobalScope.launch {
				l.forEachIndexed { y, t ->
					func.invoke(x, y, t)
				}
			}.join()
		}
	}

	fun map(func: (Int, Int, T) -> T) {
		repeat(list.size) { x ->
			val subList = list[x]
			repeat(subList.size) { y ->
				subList[y] = func.invoke(x, y, subList[y])
			}
		}
	}

	suspend fun mapAsync(func: (Int, Int, T) -> T) {
		repeat(list.size) { x ->
			GlobalScope.launch {
				val subList = list[x]
				repeat(subList.size) { y ->
					subList[y] = func.invoke(x, y, subList[y])
				}
			}.join()
		}
	}

	operator fun get(x: Int, y: Int): T {
		return list[x][y]
	}

	operator fun set(x: Int, y: Int, value: T) {
		list[x][y] = value
	}
}