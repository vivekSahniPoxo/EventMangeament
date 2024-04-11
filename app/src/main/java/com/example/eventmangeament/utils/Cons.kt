package com.example.eventmangeament.utils

import android.os.Handler
import android.os.Looper


class Cons {
    companion object {
        var BASE_URL = "http://164.52.223.163:4557/"
        const val NO_ACTION = -1
        const val Token = "token"
        const val ACCESSTOKEN = "access_token"
        const val REFRESHTOKEN = "refresh_token"
        const val USER_ROLE = "user_role"
        const val TOKEN_EXPIRE_TIME = "token_expire_time"
        private const val DEFAULT_BUFFER_SIZE = 4096
        private val handler = Handler(Looper.getMainLooper())
    }
}