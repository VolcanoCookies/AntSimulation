import com.formdev.flatlaf.FlatDarkLaf
import java.awt.GridLayout
import java.awt.Label
import javax.swing.JFrame
import javax.swing.JSlider
import javax.swing.JToggleButton
import javax.swing.UIManager
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

class SettingsWindow {
	init {
		UIManager.setLookAndFeel(FlatDarkLaf())

		val frame = JFrame("Settings")
		frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
		frame.isResizable = false

		kotlin.run {
			val label = Label("Tick Rate")
			frame.add(label)

			val slider = JSlider()
			slider.snapToTicks = true
			slider.majorTickSpacing = 8
			slider.minimum = 0
			slider.maximum = 512
			slider.snapToTicks = true

			slider.addChangeListener {
				Settings.tickRate = slider.value
			}

			frame.add(slider)
		}

		Settings::class.declaredMemberProperties.forEach {
			it.isAccessible = true
			if (it.getDelegate(Settings) != null &&
				it.returnType == Boolean::class.createType()
			) {

				it as KMutableProperty1<Settings, Boolean>

				frame.add(Label(it.name))

				val button = JToggleButton("Toggle", it.getter.invoke(Settings))
				button.addChangeListener { e ->
					val t = (e.source as JToggleButton).isSelected
					it.set(Settings, t)
				}
				Settings.listen(it) { v ->
					button.isSelected = v
				}

				frame.add(button)
			}
		}

		val layout = GridLayout()
		layout.columns = 2
		layout.rows = frame.componentCount / 2
		frame.layout = layout

		frame.pack()
		frame.isVisible = true
	}

}