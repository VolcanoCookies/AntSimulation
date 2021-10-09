import entity.Ant
import entity.Food
import entity.Nest
import entity.interfaces.Pheromone
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import math.Matrix2D
import math.vec
import java.awt.Color
import java.awt.Graphics2D
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.math.max
import kotlin.math.min

object World {

	private val random = Random()
	private val chunks: Array<Array<Chunk>>
	private val ants: LinkedList<Ant> = LinkedList()

	private val chunksX = Settings.width / Settings.chunkSize
	private val chunksY = Settings.height / Settings.chunkSize

	val toFood: Matrix2D<Pheromone?>
	val toFoodIterable: Deque<Pheromone>

	val toHome: Matrix2D<Pheromone?>
	val toHomeIterable: Deque<Pheromone>

	val food: Matrix2D<Food?>
	val foodIterable: MutableList<Food>

	val nests: Deque<Nest>

	fun spawnAnt(x: Int, y: Int) {
		val a = Ant(
			vec(x.toDouble(), y.toDouble()),
			vec(random.nextDouble() - 0.5, random.nextDouble() - 0.5)
		)
		a.chunkCoords().let { (x, y) ->
			chunks[x][y].ants.add(a)
		}
	}

	init {

		// Check that the size has the correct dimensions
		check(Settings.width % Settings.chunkSize == 0)
		check(Settings.height % Settings.chunkSize == 0)

		toFood = Matrix2D(Settings.width, Settings.height) { _, _ -> null }
		toFoodIterable = ConcurrentLinkedDeque()

		toHome = Matrix2D(Settings.width, Settings.height) { _, _ -> null }
		toHomeIterable = ConcurrentLinkedDeque()

		foodIterable = LinkedList()
		food = Matrix2D(Settings.width, Settings.height) { x, y ->
			if (x in (Settings.height / 8)..(Settings.height * 2 / 8) && (Settings.height / 2) - 60 < y && (Settings.height / 2) + 60 > y) {
				val f = Food(x, y, 50)
				foodIterable.add(f)
				f
			} else null
		}

		nests = ConcurrentLinkedDeque()
		nests.add(Nest(Settings.width / 2, Settings.height / 2))

		chunks = Array(chunksX) { x ->
			Array(chunksY) { y ->
				val c = Chunk(x, y)
				if (x == chunksX / 2 && y == chunksY / 2)
					repeat(Settings.antCount) {
						c.ants.add(
							Ant(
								vec(x.toDouble(), y.toDouble()) * Settings.chunkSize.toDouble(),
								vec(random.nextDouble() - 0.5, random.nextDouble() - 0.5)
							)
						)
					}
				c
			}
		}

	}

	suspend fun tick() {
		GlobalScope.launch {
			val addBuffer: Array<Array<MutableList<Ant>>> = Array(chunksX) {
				Array(chunksY) {
					LinkedList()
				}
			}
			launch {
				chunks.forEach { col ->
					launch {
						col.forEach { chunk ->
							launch {
								chunk.mutex.withLock {
									chunk.ants.forEach { ant ->
										ant.tick()
										ant.chunkCoords().let { (x, y) ->
											addBuffer[x][y].add(ant)
										}
									}
								}
							}.join()
						}
					}.join()
				}
				addBuffer.forEachIndexed { x, arr ->
					launch {
						arr.forEachIndexed { y, list ->
							launch {
								val c = chunks[x][y]
								c.mutex.withLock {
									c.ants = list
								}
							}.join()
						}
					}.join()
				}
			}.join()
			launch {
				toFoodIterable.removeIf {
					it.tick()
					if (it.intensity <= Settings.pheromoneCutoffIntensity) {
						toFood[it.x, it.y] = null
						true
					} else false
				}
			}
			launch {
				toHomeIterable.removeIf {
					it.tick()
					if (it.intensity <= Settings.pheromoneCutoffIntensity) {
						toHome[it.x, it.y] = null
						true
					} else false
				}
			}
		}.join()
	}

	suspend fun render(g: Graphics2D) {
		GlobalScope.launch {

			// Draw food pheromones
			launch {
				if (Settings.drawToFoodPheromone) {
					synchronized(toFoodIterable) {
						toFoodIterable.forEach { p -> p.render(g) }
					}
				}
			}

			// Draw home pheromones
			launch {
				if (Settings.drawToHomePheromone) {
					synchronized(toHomeIterable) {
						toHomeIterable.forEach { p -> p.render(g) }
					}
				}
			}

			// Draw food
			launch {
				g.new {
					it.color = Color(100, 255, 40)
					foodIterable.forEach { f -> it.drawRect(f.x, f.y, 1, 1) }
				}
			}

			// Draw nests
			launch {
				g.new {
					it.color = Color.CYAN
					nests.forEach { n -> n.render(it) }
				}
			}

			chunks.forEach { col ->
				launch {
					col.forEach { chunk ->
						if (Settings.drawChunks) {
							g.new {
								if (chunk.ants.isEmpty())
									it.color = Color.RED
								else
									it.color = Color.GREEN
								it.drawRect(
									chunk.x * Settings.chunkSize,
									chunk.y * Settings.chunkSize,
									Settings.chunkSize - 1,
									Settings.chunkSize - 1
								)
							}
						}
						chunk.mutex.withLock {
							chunk.ants.forEach { ant ->
								ant.render(g)
							}
						}
					}
				}.join()
			}
		}.join()
	}

	fun getChunk(ant: Ant): Chunk {
		return ant.chunkCoords().let { (x, y) -> chunks[x][y] }
	}

	class Chunk(
		val x: Int,
		val y: Int
	) {

		val mutex: Mutex = Mutex()
		var ants: MutableList<Ant> = LinkedList()
		val pheromones: LinkedList<Pheromone> = LinkedList()

		fun forNeighbours(r: Int, func: (Chunk) -> Unit) {

			val sx = max(x - r, 0)
			val sy = max(y - r, 0)

			val mx = min(x + r, chunksX - 1)
			val my = min(y + r, chunksY - 1)

			(sx..mx).forEach { x ->
				(sy..my).forEach { y ->
					func.invoke(chunks[x][y])
				}
			}

		}

	}

}