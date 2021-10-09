import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.jvm.isAccessible

object Settings {

	var tickRate = 128
	var tickPaused by AttachableObservable(true)
	var running = true

	val width = 800
	val height = 800

	// Rendering
	var scale = 1.0
	var xOffset = 0
	var yOffset = 0

	val chunkSize = 20

	// Render
	var drawChunks by AttachableObservable(false)
	var drawAntHeading by AttachableObservable(false)
	var drawAntSense by AttachableObservable(false)
	var drawToFoodPheromone by AttachableObservable(false)
	var drawToHomePheromone by AttachableObservable(false)
	var drawStatistics by AttachableObservable(false)

	val antCount = 200

	// Movement
	val speed = 1.0
	val randomnessFactor = 0.02

	// Sense
	const val senseRadius = 4
	const val senseDistance = 8.0
	const val senseAngle = 0.45
	const val senseTurnFactor = 0.02

	// Pheromones
	const val pheromoneInitialIntensity = 2048.0
	const val pheromoneWalkoverIntensityBoost = 512.0
	const val pheromoneCutoffIntensity = 1.0
	const val pheromoneInfrequency = 4
	const val pheromoneDegradeFactor = 0.001

	fun <T> listen(property: KMutableProperty1<Settings, T>, consumer: (T) -> Unit) {
		property.isAccessible = true
		property.getDelegate(Settings)?.let {
			if (it is AttachableObservable<*>) {
				it.attach(consumer as (Any?) -> Unit)
			}
		}
	}

	fun <T> listen(property: KMutableProperty0<T>, consumer: (T) -> Unit) {
		property.isAccessible = true
		property.getDelegate()?.let {
			if (it is AttachableObservable<*>) {
				it.attach(consumer as (Any?) -> Unit)
			}
		}
	}

}