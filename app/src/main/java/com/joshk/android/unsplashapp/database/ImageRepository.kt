package com.joshk.android.unsplashapp.database


import android.util.Log
import com.joshk.android.unsplashapp.Photo
import com.joshk.android.unsplashapp.domain.Image
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

interface ImageRepository {

    fun getImage(): Observable<NetworkResponse<Image>>

    fun handleLikedPhoto(image: Image): Single<Boolean>

}