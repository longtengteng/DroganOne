package com.llt.dragon.ui.main.home

import androidx.lifecycle.MutableLiveData
import com.llt.baselibrary.base.BaseRepository
import com.llt.baselibrary.http.ApiException
import com.llt.dragon.http.ApiService
import com.llt.dragon.http.RetrofitManager
import com.zs.wanandroid.entity.BannerBean
import kotlinx.coroutines.CoroutineScope

/**
 * des 首页
 * @date 2020/7/6
 * @author zs
 */
class HomeRepo(coroutineScope: CoroutineScope, errorLiveData: MutableLiveData<ApiException>) :
    BaseRepository(coroutineScope, errorLiveData) {

    private var page = 0

    /**
     * 获取首页文章列表， 包括banner
     */
    fun getArticleList(
        isRefresh: Boolean
        , articleLiveData: MutableLiveData<MutableList<ArticleBean.DatasBean>>
        , banner: MutableLiveData<MutableList<BannerBean>>
    ) {
        //仅在第一页或刷新时调用banner和置顶
        if (isRefresh) {
            page = 0
            getBanner(banner)
            getTopList(articleLiveData)
        } else {
            page++
            getHomeList(articleLiveData)
        }
    }

    /**
     * 获取置顶文章
     */
    private fun getTopList(articleLiveData: MutableLiveData<MutableList<ArticleBean.DatasBean>>) {
        launch(
            block = {
                RetrofitManager.getApiService(ApiService::class.java)
                    .getTopList()
                    .data()
            },
            success = {
                getHomeList(articleLiveData, it, true)
            }
        )
    }

    /**
     * 获取首页文章
     */
    private fun getHomeList(

        articleLiveData: MutableLiveData<MutableList<ArticleBean.DatasBean>>,
        list: MutableList<ArticleBean.DatasBean>? = null,
        isRefresh: Boolean = false
    ) {
        launch(
            block = {
                RetrofitManager.getApiService(ApiService::class.java)
                    .getHomeList(page)
                    .data()
            },
            success = {
                list?.let { list ->
                    it.datas?.addAll(0, list)
                }
                //做数据累加
                articleLiveData.value.apply {

                    //第一次加载 或 刷新 给 articleLiveData 赋予一个空集合
                    val currentList = if (isRefresh || this == null){
                        mutableListOf()
                    }else{
                        this
                    }
                    it.datas?.let { it1 -> currentList.addAll(it1) }
                    articleLiveData.postValue(currentList)
                }
            }
        )
    }

    /**
     * 获取banner
     */
    private fun getBanner(banner: MutableLiveData<MutableList<BannerBean>>) {
        launch(
            block = {
                RetrofitManager.getApiService(ApiService::class.java)
                    .getBanner()
                    .data()
            },
            success = {
                banner.postValue(it)
            }
        )
    }

    /**
     * 收藏
     */
    fun collect(id:Int,collectLiveData : MutableLiveData<Int>){
        launch(
            block = {
                RetrofitManager.getApiService(ApiService::class.java)
                    .collect(id)
                    .data(Any::class.java)
            },
            success = {
                collectLiveData.postValue(id)
            }
        )
    }

    /**
     * 收藏
     */
    fun unCollect(id:Int,unCollectLiveData : MutableLiveData<Int>){
        launch(
            block = {
                RetrofitManager.getApiService(ApiService::class.java)
                    .unCollect(id)
                    //如果data可能为空,可通过此方式通过反射生成对象,避免空判断
                    .data(Any::class.java)
            },
            success = {
                unCollectLiveData.postValue(id)
            }
        )
    }
}
