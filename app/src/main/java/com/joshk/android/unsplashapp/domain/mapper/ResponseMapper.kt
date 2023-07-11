package com.joshk.android.unsplashapp.domain.mapper

import com.joshk.android.unsplashapp.ImageResponse
import com.joshk.android.unsplashapp.database.NetworkResponse
import com.joshk.android.unsplashapp.domain.Image

object ResponseMapper {

    fun mapToDomainOnSuccess(response: ImageResponse): NetworkResponse<Image> {
        return NetworkResponse.Success(
            Image(
                id = "YIN0c490p_w",
                color = "",
                url = "https://images.unsplash.com/photo-1687677945732-1897c6347203?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w0NTYyMjl8MHwxfHJhbmRvbXx8fHx8fHx8fDE2ODkxMTExNTZ8&ixlib=rb-4.0.3&q=80&w=1080"
//                response.imageId,
//                response.color,
//                url = response.imageUrl.regular ?: ""
            )
        )
    }


}