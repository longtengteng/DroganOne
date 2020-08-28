package com.zs.zs_jetpack.http

import retrofit2.http.*

/**
 * @date 2020/5/9
 * @author zs
 */
interface ApiService {
    /**
     * 分享文章
     */
    @POST("/lg/user_article/add/json")
    suspend fun publishArticle(@Query("title") title: String, @Query("link") link: String)
            : ApiResponse<Any>

    /**
     * 注册
     */
    @POST("/user/register")
    suspend fun register(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("repassword") repassword: String
    ): ApiResponse<Any>
}