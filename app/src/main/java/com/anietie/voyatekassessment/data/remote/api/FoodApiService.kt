package com.anietie.voyatekassessment.data.remote.api

import com.anietie.voyatekassessment.data.remote.dto.CategoryDto
import com.anietie.voyatekassessment.data.remote.dto.CategoryResponse
import com.anietie.voyatekassessment.data.remote.dto.FetchFoodResponse
import com.anietie.voyatekassessment.data.remote.dto.FoodApiResponse
import com.anietie.voyatekassessment.data.remote.dto.FoodDto
import com.anietie.voyatekassessment.data.remote.dto.TagDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path



interface FoodApiService {

    @GET("foods")
    suspend fun fetchAllFoods(): Response<FoodApiResponse<List<FoodDto>>> // ✅ Fixed: Made `suspend` and correct response type

//    @GET("foods/{id}")
//    suspend fun fetchFoodById(@Path("id") foodId: String): Response<FoodApiResponse<FoodDto>> // ✅ Added missing API

    @Multipart
    @POST("foods")
    suspend fun addFood(
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("category_id") categoryId: RequestBody,
        @Part("calories") calories: RequestBody,
        @Part images: List<MultipartBody.Part>,
        @Part("tags[]") tags: List<RequestBody>
    ): Response<FoodApiResponse<Unit>> // ✅ Fixed: Response<FoodApiResponse<Unit>>

    @DELETE("foods/{id}")
    suspend fun removeFood(@Path("id") foodId: String)

    @Multipart
    @POST("foods/{id}")
    suspend fun updateFood(
        @Path("id") foodId: String,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("category_id") categoryId: RequestBody,
        @Part("calories") calories: RequestBody,
        @Part("tags[]") tags: List<RequestBody>, // ✅ Fixed: Used List instead of @PartMap
        @Part images: List<MultipartBody.Part>
    ): Response<FoodApiResponse<Unit>> // ✅ Fixed: Response<FoodApiResponse<Unit>>

    @GET("categories")
    suspend fun getCategories(): Response<FoodApiResponse<List<CategoryDto>>> // ✅ Fixed: Correct response type

    @GET("tags")
    suspend fun getTags(): Response<FoodApiResponse<List<TagDto>>> // ✅ Fixed: Correct response type
}
