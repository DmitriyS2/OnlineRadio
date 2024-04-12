package ru.netology.radiorecord.dto

data class Station(
    val bg_image_mobile: String,
    val genre: List<Genre>,
    val icon_fill_colored: String,
    val id: Int,
    val short_title: String,
    val stream_128: String,
    val stream_64: String,
    val title: String,
    val tooltip: String,
    var isChecked:Boolean = false,
)