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

    fun changeListRadio() {
      listStations.value = dataModel.value?.listRadio
    }
}