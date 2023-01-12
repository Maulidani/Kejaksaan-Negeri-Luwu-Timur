package go.kejaksaannegeriluwutimur.repository

import go.kejaksaannegeriluwutimur.network.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class Repository @Inject constructor(
    private val apiService: ApiService
) {
    fun postPakem(
        nama: RequestBody,
        nomor: RequestBody,
        email: RequestBody,
        kegiatan: RequestBody,
        tempat: RequestBody,
        alamat: RequestBody,
        keterangan: RequestBody,
        dokumen: MultipartBody.Part,
        token: RequestBody,
    ) = apiService.pakem(
        nama, nomor, email, kegiatan, tempat, alamat, keterangan, dokumen, token
    )

    fun postPengawas(
        nama: RequestBody,
        nomor: RequestBody,
        email: RequestBody,
        alamat: RequestBody,
        judul: RequestBody,
        penulis: RequestBody,
        bentuk: RequestBody,
        tanggal: RequestBody,
        isi: RequestBody,
        dokumen: MultipartBody.Part,
        token: RequestBody,
    ) = apiService.pengawasan(
        nama, nomor, email, alamat, judul, penulis, bentuk, tanggal, isi, dokumen, token
    )

    fun postPermohonan(
        instansi: RequestBody,
        nama_kegiatan: RequestBody,
        nilai_kegiatan: RequestBody,
        jadwal_sosialisasi: RequestBody,
        nama_aliran: RequestBody,
        nama_penanggung: RequestBody,
        telepon_instansi: RequestBody,
        email_instansi: RequestBody,
        user_id: RequestBody,
        file_permohonan: MultipartBody.Part,
        ktp: MultipartBody.Part,
        token: RequestBody,
    ) = apiService.permohonan(
        instansi,
        nama_kegiatan,
        nilai_kegiatan,
        jadwal_sosialisasi,
        nama_aliran,
        nama_penanggung,
        telepon_instansi,
        email_instansi,
        user_id,
        file_permohonan,
        ktp,
        token
    )

    fun postPelayanan(
        user_id: RequestBody,
        nama_lengkap: RequestBody,
        alamat: RequestBody,
        nomor: RequestBody,
        email: RequestBody,
        kategori: RequestBody,
        bentuk_permasalahan: RequestBody,
        detail_permasalahan: RequestBody,
        dokumen: MultipartBody.Part,
        ktp: MultipartBody.Part,
        token: RequestBody,
    ) = apiService.pelayanan(
        user_id,
        nama_lengkap,
        alamat,
        nomor,
        email,
        kategori,
        bentuk_permasalahan,
        detail_permasalahan,
        dokumen,
        ktp,
        token
    )

    fun postTindakan(
        nama_lengkap: RequestBody,
        nomor: RequestBody,
        email: RequestBody,
        alamat: RequestBody,
        kategori: RequestBody,
        bentuk_permasalahan: RequestBody,
        detail_permasalahan: RequestBody,
        dokumen: MultipartBody.Part,
        user_id: RequestBody,
        ktp: MultipartBody.Part,
        token: RequestBody,
    ) = apiService.tindakan(
        nama_lengkap,
        nomor,
        email,
        alamat,
        kategori,
        bentuk_permasalahan,
        detail_permasalahan,
        dokumen,
        user_id,
        ktp,
        token
    )

    fun postLoginUser(
        username: String,
        password: String,
    ) = apiService.login(
        username, password
    )

    fun getSisfoTilang(
        token: String,
    ) = apiService.sisfoTilang(
        token
    )

    fun getPerkaraPidana(
        token: String,
    ) = apiService.perkaraPidana(
        token
    )

    fun getJadwalPemeriksaan(
        token: String,
    ) = apiService.jadwalPemeriksaan(
        token
    )

    fun postRoom(
        token: String,
        idKepalaDesa: Int?,
    ) = apiService.room(
        token, idKepalaDesa = null
    )

    fun postChat(
        roomId: Int,
        message: String,
        token: String,
    ) = apiService.chat(
        roomId, message, token
    )

    fun getLoadChat(
        token: String,
        roomId: String,
    ) = apiService.loadChat(
        token, roomId
    )

    fun getListInAdmin(
        token: String,
    ) = apiService.listAdmin(
        token
    )

    fun getListInKepalaDesa(
        token: String,
    ) = apiService.listUser(
        token
    )
}