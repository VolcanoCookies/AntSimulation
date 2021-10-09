object Statistics {

	var framesPerSecond: Int = 0
	var ticksPerSecond: Int = 0

	val renderTime = RollingAverage(100)
	val tickTime = RollingAverage(100)

}