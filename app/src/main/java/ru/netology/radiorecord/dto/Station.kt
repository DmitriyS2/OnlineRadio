package ru.netology.radiorecord.dto

import ru.netology.radiorecord.dto.Genre

data class Station(
    val bg_image_mobile: String,
    val genre: List<Genre>,
    val icon_fill_colored: String,
    val id: Int,
 //   val prefix: String,
//    val shareUrl: String,
    val short_title: String,
//    val sort: Int,
    val stream_128: String,
//    val stream_320: String,
    val stream_64: String,
//    val stream_hls: String,
    val title: String,
    val tooltip: String,
    var isChecked:Boolean = false,
)