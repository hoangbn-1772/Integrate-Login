package com.example.integratelogin.twitter

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var name: String = "",
    var screenName: String = "",
    var profileImageUrl: String = ""
) : Parcelable
