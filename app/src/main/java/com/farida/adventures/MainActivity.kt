
package com.farida.adventures

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import android.speech.tts.TextToSpeech
import java.util.Locale

class MainActivity : Activity() {

    private lateinit var box: LinearLayout
    private lateinit var tts: TextToSpeech
    private var stars = 0
    private var index = 0
    private var world = 0
    private var answered = false

    data class Question(
        val title: String,
        val choices: List<String>,
        val answer: Int
    )

    private val worlds = listOf(
        "🎨 عالم الألوان",
        "🔢 عالم الأرقام",
        "🐾 عالم الحيوانات",
        "🔷 عالم الأشكال",
        "🍎 عالم الفواكه",
        "🧠 عالم الذكاء"
    )

    private val questions = listOf(
        listOf(
            Question("أين اللون الأحمر؟", listOf("🔴","🔵","🟡"),0),
            Question("أين اللون الأخضر؟", listOf("🔴","🟢","🔵"),1),
            Question("أين اللون الأصفر؟", listOf("🟣","🟡","⚫"),1)
        ),
        listOf(
            Question("كم نجمة هنا؟ ⭐⭐⭐", listOf("1","2","3"),2),
            Question("كم تفاحة هنا؟ 🍎🍎", listOf("1","2","3"),1)
        ),
        listOf(
            Question("أين القطة؟", listOf("🐶","🐱","🐰"),1),
            Question("أين الفيل؟", listOf("🐘","🐦","🐟"),0)
        ),
        listOf(
            Question("أين المثلث؟", listOf("🟦","⚪","🔺"),2),
            Question("أين الدائرة؟", listOf("🔺","⭕","🟦"),1)
        ),
        listOf(
            Question("أين التفاحة؟", listOf("🍌","🍎","🍇"),1),
            Question("أين الموز؟", listOf("🍌","🍓","🍊"),0)
        ),
        listOf(
            Question("ما الحيوان الذي له خرطوم؟", listOf("🐘","🐱","🐰"),0),
            Question("ما الشكل المستدير؟", listOf("🔺","🟦","⭕"),2)
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        stars = getPreferences(0).getInt("stars", 0)

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale("ar")
                tts.setSpeechRate(0.85f)
            }
        }

        home()
    }

    private fun setup(title: String) {
        val scroll = ScrollView(this)
        scroll.setBackgroundColor(Color.rgb(255,237,247))

        box = LinearLayout(this)
        box.orientation = LinearLayout.VERTICAL
        box.gravity = Gravity.CENTER
        box.setPadding(22,35,22,30)

        scroll.addView(box)
        setContentView(scroll)

        addText(title,31,Color.rgb(195,64,135))
    }

    private fun addText(
        value: String,
        size: Int,
        color: Int = Color.rgb(100,55,90)
    ) {
        val text = TextView(this)
        text.text = value
        text.textSize = size.toFloat()
        text.gravity = Gravity.CENTER
        text.setTextColor(color)
        text.setPadding(8,15,8,15)
        box.addView(text)
    }

    private fun button(
        label: String,
        action: () -> Unit
    ) {
        val b = Button(this)
        b.text = label
        b.textSize = 19f
        b.isAllCaps = false
        b.setTextColor(Color.rgb(110,45,85))
        b.setBackgroundColor(Color.rgb(255,190,220))

        val params = LinearLayout.LayoutParams(-1,75)
        params.setMargins(4,7,4,7)

        box.addView(b,params)
        b.setOnClickListener { action() }
    }

    private fun say(message: String) {
        if (::tts.isInitialized) {
            tts.speak(
                message,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "farida"
            )
        }
    }

    private fun home() {
        setup("🌸 مغامرات فريدة 🌸")
        addText("أهلاً بك يا بطل! ❤️",25)
        addText("نتعلم ونلعب ونكتشف مع فريدة",19)
        addText("⭐ نجومك: $stars",24)

        button("🚀 ابدأ المغامرة") { showWorlds() }

        button("🔊 تجربة الصوت العربي") {
            say("أهلاً يا أصدقائي! هيا نلعب ونتعلم!")
        }
    }

    private fun showWorlds() {
        setup("🌈 اختار عالمك الجميل")

        worlds.forEachIndexed { i, name ->
            button(name) {
                world = i
                index = 0
                showQuestion()
            }
        }

        button("🏠 الرئيسية") { home() }
    }

    private fun showQuestion() {
        answered = false
        val list = questions[world]

        if (index >= list.size) {
            finishWorld()
            return
        }

        val q = list[index]
        setup(worlds[world])

        addText("السؤال ${index+1} من ${list.size}",18)
        addText(q.title,25)
        say(q.title)

        q.choices.forEachIndexed { i, choice ->
            button(choice) {
                if (!answered) {
                    answered = true
                    checkAnswer(i,q.answer)
                }
            }
        }

        button("🏠 الرئيسية") { home() }
    }

    private fun checkAnswer(selected: Int, correct: Int) {
        setup(worlds[world])

        if (selected == correct) {
            stars++
            getPreferences(0).edit()
                .putInt("stars",stars).apply()

            addText("🎉 أحسنت! إجابة صحيحة!",25)
            addText("⭐ نجمة جديدة لفريدة!",22)
            say("برافو! إجابة صحيحة!")
        } else {
            addText("💗 محاولة جميلة!",25)
            addText(
                "الإجابة الصحيحة: ${questions[world][index].choices[correct]}",
                22
            )
            say("محاولة جميلة! هيا نتعلم معاً")
        }

        button("التالي ⭐") {
            index++
            showQuestion()
        }
    }

    private fun finishWorld() {
        setup("🏆 مبروك يا بطل!")
        addText("أنهيت ${worlds[world]}",23)
        addText("⭐ مجموع النجوم: $stars",25)
        say("رائع! لقد أنهيت المغامرة")

        button("🗺️ العوالم") { showWorlds() }
        button("🏠 الرئيسية") { home() }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}
