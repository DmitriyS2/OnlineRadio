package ru.netology.radiorecord.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import ru.netology.radiorecord.dto.Station
import ru.netology.radiorecord.model.DataModel
import ru.netology.radiorecord.repository.Repository
import ru.netology.radiorecord.repository.RepositoryImpl
import kotlin.concurrent.thread

class MainViewModel:ViewModel() {
    private val repository: Repository = RepositoryImpl()

    val dataModel: MutableLiveData<DataModel> = MutableLiveData<DataModel>()

    val listStations: MutableLiveData<List<Station>> =
        MutableLiveData<List<Station>>()

    val selectedTrack
        get() = listStations.map {
            it.firstOrNull { data ->
                data.isChecked
            }
        }

    init {
        getAlbum()
    }

    fun getAlbum() {
        thread {
            try {
                dataModel.postValue(DataModel(loading = true))
                val data = repository.getAlbum()?.result?.stations ?: emptyList()
                dataModel.postValue(DataModel(listRadio = data, error = data.isEmpty()))

            } catch (e: Exception) {
                e.printStackTrace()
                dataModel.postValue(DataModel(error = true))

            }
        }
    }

    fun highlight(station: Station) {
        //снимаем выделение
        if (station.isChecked) {
            listStations.value = listStations.value?.let {
                it.map { data ->
                    if (data.id == station.id) {
                        data.copy(isChecked = false)
                    } else {
                        data
                    }
                }
            }
        } else {
            //выделяем
            listStations.value = listStations.value?.let {
                it.map { data ->
                    if (data.id == station.id) {
                        data.copy(isChecked = true)
                    } else {
                        data.copy(isChecked = false)
                    }
                }
            }
        }
    }

//    fun changeImageTrack(name: String?, flag: Boolean) {
//        name?.let {
//            //играется
//            if (flag) {
//                listStations.value = listStations.value?.let {
//                    it.map { data ->
//                        if (data.name == name) {
//                            data.copy(isPlaying = true)
//                        } else {
//                            data.copy(isPlaying = false)
//                        }
//                    }
//                }
//                //не играется
//            } else {
//                listStations.value = listStations.value?.let {
//                    it.map { data ->
//                        if (data.name == name) {
//                            data.copy(isPlaying = false)
//                        } else {
//                            data
//                        }
//                    }
//                }
//            }
//        }
//    }

//    fun goToNextTrack() {
//        val maxId = listStations.value?.maxByOrNull {
//            it.id
//        }?.id ?: 0
//
//        val currentId = listStations.value?.filter {
//            it.name == selectedTrack.value
//        }?.map { data ->
//            data.id
//        }?.firstOrNull() ?: 0
//
//        val newId = if (currentId == maxId) 1 else currentId + 1
//
//        val newTrack = listStations.value?.firstOrNull {
//            it.id == newId
//        }
//
//        val newName = newTrack?.name
//
//        changeImageTrack(newName, true)
//
//        newTrack?.let {
//            highlight(it)
//        }
//    }

    fun changeListRadio() {
      listStations.value = dataModel.value?.listRadio
    }
}