import java.util.*
import kotlin.reflect.KProperty

class AttachableObservable<T>(initial: T) : kotlin.properties.ReadWriteProperty<Any?, T> {

	var value: T = initial
	private val listeners: MutableList<(T) -> Unit> = LinkedList()

	fun attach(consumable: (T) -> Unit) {
		this.listeners += consumable
	}

	override fun getValue(thisRef: Any?, property: KProperty<*>): T {
		return value
	}

	override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
		if (value != this.value)
			listeners.forEach { it.invoke(value) }
		this.value = value
	}

}