package ru.netology.radiorecord.model

import ru.netology.radiorecord.dto.Station

data class DataModel(
    val listRadio: List<Station> = emptyList(),
    val loading: Boolean = false,
    val error: Boolean = false,
)