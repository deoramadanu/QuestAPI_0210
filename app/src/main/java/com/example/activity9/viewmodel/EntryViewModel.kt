package com.example.activity9.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activity9.modeldata.DataSiswa
import com.example.activity9.repositori.RepositoryDataSiswa
import kotlinx.coroutines.launch
import retrofit2.Response

class EntryViewModel(
    private val repositoryDataSiswa: RepositoryDataSiswa
) : ViewModel() {

    var uiStateSiswa by mutableStateOf(UIStateSiswa())
        private set

    private fun validasiInput(uiState: DetailSiswa = uiStateSiswa.detailSiswa): Boolean {
        return with(uiState) {
            nama.isNotBlank() && alamat.isNotBlank() && telfon.isNotBlank()
        }
    }

    fun updateUiState(detailSiswa: DetailSiswa) {
        val valid = validasiInput(detailSiswa)
        println("Cek validasi: $valid | nama='${detailSiswa.nama}' alamat='${detailSiswa.alamat}' telpon='${detailSiswa.telfon}'")
        uiStateSiswa = UIStateSiswa(
            detailSiswa = detailSiswa,
            isEntryValid = valid
        )
    }

    fun addSiswa(onSuccess: () -> Unit = {}) {
        if (validasiInput()) {
            viewModelScope.launch {
                try {
                    val sip: Response<Void> = repositoryDataSiswa.postDataSiswa(
                        uiStateSiswa.detailSiswa.toDataSiswa()
                    )
                    if (sip.isSuccessful) {
                        println("Sukses tambah Data : ${sip.code()} ${sip.message()}")
                        onSuccess()
                    } else {
                        val errorText = sip.errorBody()?.string()
                        println("Gagal tambah data : ${sip.code()} - $errorText")
                    }
                } catch (e: Exception) {
                    println("Error jaringan saat tambah data : ${e.localizedMessage}")
                }
            }
        }
    }
}

data class UIStateSiswa(
    val detailSiswa: DetailSiswa = DetailSiswa(),
    val isEntryValid: Boolean = false
)

data class DetailSiswa(
    val id: Int = 0,
    val nama: String = "",
    val alamat: String = "",
    val telfon: String = ""
)

fun DetailSiswa.toDataSiswa(): DataSiswa = DataSiswa(
    id = id,
    nama = nama,
    alamat = alamat,
    telfon = telfon
)

fun DataSiswa.toUiStateSiswa(isEntryValid: Boolean = false): UIStateSiswa = UIStateSiswa(
    detailSiswa = this.toDetailSiswa(),
    isEntryValid = isEntryValid
)

fun DataSiswa.toDetailSiswa(): DetailSiswa = DetailSiswa(
    id = id,
    nama = nama,
    alamat = alamat,
    telfon = telfon
)