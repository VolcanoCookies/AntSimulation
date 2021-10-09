import java.util.concurrent.ConcurrentLinkedQueue

class RollingAverage(private val samples: Long) {

	private val queue = ConcurrentLinkedQueue<Long>()
	private var sum = 0L

	fun registerSample(sample: Long) {
		queue.add(sample)
		sum += sample - queue.poll()
	}

	val average: Long
		get() = sum / samples

	override fun toString(): String {
		return average.toString()
	}

	init {
		repeat(samples.toInt()) {
			queue.add(0)
		}
	}

}