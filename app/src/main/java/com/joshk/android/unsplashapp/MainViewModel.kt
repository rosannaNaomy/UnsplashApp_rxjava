package com.joshk.android.unsplashapp


import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joshk.android.unsplashapp.database.ImageRepository
import com.joshk.android.unsplashapp.database.ImageRepositoryImpl
import com.joshk.android.unsplashapp.database.NetworkResponse
import com.joshk.android.unsplashapp.domain.Image
import com.joshk.android.unsplashapp.network.NetworkLayer
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainViewModel(application: Application) : AndroidViewModel(application) {

    var galleryDao: GalleryDao

    init {
        galleryDao = GalleryDatabase.getDatabase(application).galleryDao()
    }

    private val imageRepository: ImageRepository =
        ImageRepositoryImpl(NetworkLayer.unsplashService, galleryDao)

    private val _image = MutableLiveData<Image>()
    val image: LiveData<Image>
        get() = _image

    private val loadingLiveData = MutableLiveData<Boolean>()
    private val errorLiveData = MutableLiveData<String>()

    private var _isLiked = MutableLiveData<Boolean>()
    val isLiked: LiveData<Boolean>
        get() = _isLiked

    private val compositeDisposable = CompositeDisposable()


    fun getImage() {
        loadingLiveData.value = true

        imageRepository.getImage()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        val imageData = response.data
                        if (imageData != null) {
                            _image.value = imageData
                            loadingLiveData.value = false
                        }
                    }

                    is NetworkResponse.Error -> {
                        errorLiveData.value = response.message
                        loadingLiveData.value = false
                    }

                    NetworkResponse.Loading -> {
                        loadingLiveData.value = true
                    }
                }
            }
    }

    fun isLiked() {
        val imageData = image.value
        if (imageData != null) {
            imageRepository.handleLikedPhoto(imageData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    _isLiked.value = response
                    Log.d("ISLIKED", "Liked response: $response")

                }, { error ->
                    // Handle error case
                })
                .addTo(compositeDisposable)
        }
    }

    private fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

    fun getImageLiveData(): LiveData<Image?> = _image
    fun getLoadingLiveData(): LiveData<Boolean> = loadingLiveData
    fun geterrorLiveData(): LiveData<String> = errorLiveData


}