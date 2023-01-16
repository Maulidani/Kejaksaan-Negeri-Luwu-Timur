package go.kejaksaannegeriluwutimur.util

object Constants {

    // api network
    const val BASE_URL = "http://192.168.205.86:8000/"
    const val WEBSOCKET_URL = "ws://192.168.205.86:6001/app/qwerty?protocol=7&client=js&version=4.3.1&flash=false"

    // response network
    const val RESPONSE_TOKEN_SALAH = "Token Kadaluwarsa"

    // role
    const val ROLE_KEPALA_DESA = "user"
    const val ROLE_ADMIN = "admin"

    // shared preferences name key
    const val PREF_USER_ROLE = "PREF_USER_ROLE"
    const val PREF_USER_ID = "PREF_USER_ID"
    const val PREF_USER_TOKEN = "PREF_USER_TOKEN"
    const val PREF_USER_IS_LOGIN = "PREF_USER_IS_LOGIN"

    // message
    const val MSG_TERJADI_KESALAHAN = "Terjadi kesalahan"
    const val MSG_GAGAL_LOGIN = "Gagal login"

    // type hukum
    const val HUKUM_GRATIS = "hukum gratis"
    const val HUKUM_LAIN = "hukum lain"

    // type layanan tindak pidana umum
    const val SISFO_TILANG = "sistem informasi tilang"
    const val PERKARA_PIDANA_UMUM = "perkara pidana umum"
    const val JADWAL_PEMERIKSAAN = "jadwal pemeriksaan"

}