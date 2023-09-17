package com.example.lsproject2.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FollowModel(
    var followerCount : Int = 0,
    var followers : MutableMap<String,String> = hashMapOf(),

    var followingCount : Int = 0,
    var followings : MutableMap<String,String> = hashMapOf()

) : Parcelable