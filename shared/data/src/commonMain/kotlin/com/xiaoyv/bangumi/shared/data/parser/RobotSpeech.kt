package com.xiaoyv.bangumi.shared.data.parser

class RobotSpeech private constructor() {
    private val speeches = mutableSetOf<String>()

    val random: String = if (speeches.isEmpty()) "" else speeches.random()

    fun add(speech: String) {
        if (speech.isNotBlank()) speeches.add(speech)
    }

    companion object {
        val instance by lazy { RobotSpeech() }
    }
}