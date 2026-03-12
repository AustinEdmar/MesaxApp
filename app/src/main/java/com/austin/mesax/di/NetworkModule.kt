package com.austin.mesax.di

import com.austin.mesax.data.api.AuthApi
import com.austin.mesax.data.api.CoreUrl.BASE_URL
import com.austin.mesax.data.api.OrdersApi
import com.austin.mesax.data.api.ProductApi
import com.austin.mesax.data.api.ShiftApi
import com.austin.mesax.data.api.TablesApi
import com.austin.mesax.data.datastore.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideOkHttp(
        tokenManager: TokenManager
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL) // salo meu pc
            .client(okHttpClient) // 🔥 ISTO É O QUE FALTAVA
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    fun provideAuthApi(retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)
    
    @Provides
    fun provideTablesApi(retrofit: Retrofit): TablesApi =
        retrofit.create(TablesApi::class.java)

    @Provides
    fun provideShiftApi(retrofit: Retrofit): ShiftApi =
        retrofit.create(ShiftApi::class.java)

    @Provides
    fun provideProductApi(retrofit: Retrofit): ProductApi =
        retrofit.create(ProductApi::class.java)


    @Provides
    fun provideOrdersApi(retrofit: Retrofit): OrdersApi =
        retrofit.create(OrdersApi::class.java)


}
