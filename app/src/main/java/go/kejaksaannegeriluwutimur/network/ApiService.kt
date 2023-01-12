package go.kejaksaannegeriluwutimur.network

import go.kejaksaannegeriluwutimur.model.Model
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @Multipart
    @POST("pakem")
    fun pakem(
        @Part("nama") nama: RequestBody,
        @Part("nomor") nomor: RequestBody,
        @Part("email") email: RequestBody,
        @Part("kegiatan") kegiatan: RequestBody,
        @Part("tempat") tempat: RequestBody,
        @Part("alamat") alamat: RequestBody,
        @Part("keterangan") keterangan: RequestBody,
        @Part dokumen: MultipartBody.Part,
        @Part("token") token: RequestBody,
    ): Call<Model.Response>

    @Multipart
    @POST("pengawasan")
    fun pengawasan(
        @Part("nama") nama: RequestBody,
        @Part("nomor") nomor: RequestBody,
        @Part("email") email: RequestBody,
        @Part("alamat") alamat: RequestBody,
        @Part("judul") judul: RequestBody,
        @Part("penulis") penulis: RequestBody,
        @Part("bentuk") bentuk: RequestBody,
        @Part("tanggal") tanggal: RequestBody,
        @Part("isi") isi: RequestBody,
        @Part dokumen: MultipartBody.Part,
        @Part("token") token: RequestBody,
    ): Call<Model.Response>

    @Multipart
    @POST("permohonan")
    fun permohonan(
        @Part("instansi") instansi: RequestBody,
        @Part("nama_kegiatan") nama_kegiatan: RequestBody,
        @Part("nilai_kegiatan") nilai_kegiatan: RequestBody,
        @Part("jadwal_sosialisasi") jadwal_sosialisasi: RequestBody,
        @Part("nama_aliran") nama_aliran: RequestBody,
        @Part("nama_penanggung") nama_penanggung: RequestBody,
        @Part("telepon_instansi") telepon_instansi: RequestBody,
        @Part("email_instansi") email_instansi: RequestBody,
        @Part("user_id") user_id: RequestBody,
        @Part file_permohonan: MultipartBody.Part,
        @Part ktp: MultipartBody.Part,
        @Part("token") token: RequestBody,
    ): Call<Model.Response>

    @Multipart
    @POST("pelayanan")
    fun pelayanan(
        @Part("nama_lengkap") nama_lengkap: RequestBody,
        @Part("alamat") alamat: RequestBody,
        @Part("nomor") nomor: RequestBody,
        @Part("email") email: RequestBody,
        @Part("kategori") kategori: RequestBody,
        @Part("bentuk_permasalahan") bentuk_permasalahan: RequestBody,
        @Part("detail_permasalahan") detail_permasalahan: RequestBody,
        @Part dokumen: MultipartBody.Part,
        @Part ktp: MultipartBody.Part,
        @Part("user_id") user_id: RequestBody,
        @Part("token") token: RequestBody,
    ): Call<Model.Response>

    @Multipart
    @POST("tindakan")
    fun tindakan(
        @Part("nama_lengkap") nama_lengkap: RequestBody,
        @Part("alamat") alamat: RequestBody,
        @Part("nomor") nomor: RequestBody,
        @Part("email") email: RequestBody,
        @Part("kategori") kategori: RequestBody,
        @Part("bentuk_permasalahan") bentuk_permasalahan: RequestBody,
        @Part("detail_permasalahan") detail_permasalahan: RequestBody,
        @Part dokumen: MultipartBody.Part,
        @Part ktp: MultipartBody.Part,
        @Part("user_id") user_id: RequestBody,
        @Part("token") token: RequestBody,
    ): Call<Model.Response>

    @FormUrlEncoded
    @POST("loginUser")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String,
    ): Call<Model.Response>

    @GET("sisfotilang/{token}")
    fun sisfoTilang(
        @Path("token") token: String
    ): Call<Model.DataArrayResponse>

    @GET("perkarapidana/{token}")
    fun perkaraPidana(
        @Path("token") token: String
    ): Call<Model.DataArrayResponse>

    @GET("jadwalpemeriksaan/{token}")
    fun jadwalPemeriksaan(
        @Path("token") token: String
    ): Call<Model.DataArrayResponse>

    @FormUrlEncoded
    @POST("room")
    fun room(
        @Field("token") token: String,
        @Field("idKepalaDesa") idKepalaDesa: Int?, // admin
    ): Call<Model.Response>

    @FormUrlEncoded
    @POST("chat")
    fun chat(
        @Field("roomId") roomId: Int,
        @Field("message") message: String,
        @Field("token") token: String,
    ): Call<Model.Response>

    @GET("chat/load/{token}/{room-id}")
    fun loadChat(
        @Path("token") token: String,
        @Path("room-id") roomId: String
    ): Call<Model.DataArrayResponse>

    @GET("chat/listAdmin/{token}")
    fun listAdmin(
        @Path("token") token: String
    ): Call<Model.DataArrayResponse>

    @GET("chat/listUser/{token}")
    fun listUser(
        @Path("token") token: String
    ): Call<Model.DataArrayResponse>

}
