package go.kejaksaannegeriluwutimur.model

class Model {

    data class Response(
        val success: Boolean,
        val message: String,
        val pengirim: PengirimResponse,
        val data: DataResponse,
    )

    data class DataArrayResponse(
        val success: Boolean,
        val message: String,
        val data: ArrayList<DataLoginResponse>,
        val chat: ArrayList<Chat>,
    )

    data class DataResponse(

        val id: Int,
        val created_at: String,
        val updated_at: String,

        // sisfo tilang
        val nama_operasi: String,
        val tempat_operasi: String,
        val waktu_operasi: String,

        // data perkara pidana
        val nama_penyidik: String,
        val nama_tersangka: String,
        val p16: String,
        val tgl_terimaBerkas: String,
        val tgl_spdp: String, // also in jaddwal pemeriksaan
        val pasal: String,
        val p16a: String,
        val keterangan: String,

        // jadwal pemeriksaan
        val perkara_pidana_id: Int,
        val tanggal: String,
        val kasus: String,
        val tahap_kasus: String,
        val status: String,

        // buat room
        val users: String,

        // ambil data chat
        val room_id: Int,
        val user_id: Int,
        val message: String,
        val read: Int,

        // list user chat
        val nama_kepaladesa: String,
        val nama_desa: String,
        val username: String,
        val token: String,

        // login
        val roles: String,

        )

    data class Chat(
        val tidak_dibaca: Int,
        val room_id: Int,
        val id_lawan_chat: Int,
    )

    data class PengirimResponse(
        val id: Int,
        val nama_kepaladesa: String,
        val nama_desa: String,
        val username: String,
        val token: String,
        val roles: String,
        val created_at: String,
        val updated_at: String,
    )

    data class DataLoginResponse(
        val id: Int,
        val nama_kepaladesa: String,
        val nama_desa: String,
        val username: String,
        val token: String,
        val roles: String,
        val created_at: String,
        val updated_at: String,
    )

}
