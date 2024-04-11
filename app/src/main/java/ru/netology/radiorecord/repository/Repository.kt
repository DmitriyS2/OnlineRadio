package ru.netology.radiorecord.repository

import ru.netology.radiorecord.dto.RadioStream

interface Repository {
    fun getAlbum(): RadioStream?
}