package com.llt.dragon.ui.main.tab

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.llt.baselibrary.base.BaseViewModel
import com.llt.dragon.ui.main.home.ArticleBean

/**
 * des 文章vm
 * @date 2020/7/8
 * @author zs
 */
class ArticleVM : BaseViewModel() {

    private val repo by lazy { ArticleRepo(viewModelScope, errorLiveData) }
    val articleLiveData = MutableLiveData<MutableList<ArticleBean.DatasBean>>()

    /**
     * 收藏
     */
    val collectLiveData = MutableLiveData<Int>()

    /**
     * 取消收藏
     */
    val unCollectLiveData = MutableLiveData<Int>()

    /**
     * 获取文章
     */
    fun getArticle(type: Int, tabId: Int, isRefresh: Boolean) {
        repo.getArticle(type, tabId, isRefresh, articleLiveData)
    }

    /**
     * 收藏
     */
    fun collect(id: Int) {
        repo.collect(id, collectLiveData)
    }

    /**
     * 取消收藏
     */
    fun unCollect(id: Int) {
        repo.unCollect(id, unCollectLiveData)
    }
}