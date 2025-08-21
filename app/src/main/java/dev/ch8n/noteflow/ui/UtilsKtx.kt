package dev.ch8n.noteflow.ui

fun String?.wordCount(): Int {
    return this?.split(" ")?.count { it.isNotEmpty() } ?: 0
}