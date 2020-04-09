package com.shimhg02.otpmessage.Retrofit



import com.shimhg02.otpmessage.Data.Login
import com.shimhg02.otpmessage.Data.Message
import retrofit2.Call
import retrofit2.http.*
interface API {


    @POST("/signin")
    @FormUrlEncoded
    fun logIn(@Field("id") id : String, @Field("passwd") pw : String) :  Call<Login>

    @POST("/signup")
    @FormUrlEncoded
    fun logUp(@Field("name") name: String?, @Field("id") id: String?, @Field("passwd") pw: String?) :  Call<Void>

    @POST("/message/search")
    @FormUrlEncoded
    fun getMessage(@Field("userToken") token : String) :  Call<Message>


    @POST("/message/write")
    @FormUrlEncoded
    fun sendMessage(@Field("phone") phone : String, @Field("data") data : String, @Field("userToken") userToken : String) :  Call<Message>

    @POST("/message/searchPhone")
    @FormUrlEncoded
    fun searchMessage(@Field("userToken") token : String, @Field("phone") phone : String) :  Call<Message>

    @GET("/auto/")
    @FormUrlEncoded
    fun autoLogin() :  Call<Login>

}