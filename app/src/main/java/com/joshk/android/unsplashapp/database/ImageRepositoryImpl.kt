package com.joshk.android.unsplashapp.database

import android.util.Log
import com.joshk.android.unsplashapp.GalleryDao
import com.joshk.android.unsplashapp.ImageResponse
import com.joshk.android.unsplashapp.Photo
import com.joshk.android.unsplashapp.domain.Image
import com.joshk.android.unsplashapp.network.UnsplashService
import com.joshk.android.unsplashapp.database.NetworkResponse.Loading
import com.joshk.android.unsplashapp.domain.mapper.ResponseMapper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class ImageRepositoryImpl(
    private val unsplashService: UnsplashService,
    private val galleryDao: GalleryDao,
) : ImageRepository {

    override fun getImage(): Observable<NetworkResponse<Image>> {
        return Observable.create { emitter ->
            emitter.onNext(Loading)
            unsplashService.getImage()
                .subscribe({ response ->
                    emitter.onNext(
                        ResponseMapper.mapToDomainOnSuccess(response)
                    )
                    emitter.onComplete()
                }, { error ->
                    emitter.onNext(NetworkResponse.Error(error.message ?: "Unknown Error"))
                    emitter.onComplete()
                })
        }
    }


    override fun handleLikedPhoto(image: Image): Single<Boolean> {
        return Single.create { emitter ->
            galleryDao.exists(image.id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.d("HandlePhoto", "$it")
                    if (it) {
                        deletePhoto(image.id)
                        emitter.onSuccess(false)
                    } else {
                        insertPhoto(image)
                        emitter.onSuccess(true)
                    }
                }, { error ->
                    Log.e("HandlePhoto", "Failed Insertion/Deletion")
                })
        }
    }


    private fun insertPhoto(image: Image){
        galleryDao.exists(image.id)
        galleryDao.insertPhoto(Photo(image.id, image.url))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                Log.d("Insertion", "Insert Success!")
            }, { error ->
                Log.d("Insertion", "Insert Error!")
            })
    }

    private fun deletePhoto(imageId: String){
        galleryDao.deletePhoto(imageId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                Log.d("Deletion", "Delete Success!")
            }, { error ->
                Log.d("Deletion", "Delete Failed.")
            })
    }

}
