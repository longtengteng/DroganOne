package com.llt.dragon.ui.main.tab

import androidx.lifecycle.MutableLiveData
import com.llt.baselibrary.base.BaseRepository
import com.llt.baselibrary.http.ApiException
import com.llt.dragon.constants.Constants
import com.llt.dragon.http.ApiService
import com.llt.dragon.http.RetrofitManager
import kotlinx.coroutines.CoroutineScope

/**
 * des tab
 * @date 2020/7/7
 * @author zs
 */
class TabRepo(coroutineScope: CoroutineScope, errorLiveData: MutableLiveData<ApiException>) :
    BaseRepository(coroutineScope, errorLiveData) {


    fun getTab(type: Int, tabLiveData: MutableLiveData<MutableList<TabBean>>) {
        launch(
            block = {
                if (type == Constants.PROJECT_TYPE) {
                    RetrofitManager.getApiService(ApiService::class.java)
                        .getProjectTabList()
                        .data()
                } else {
                    RetrofitManager.getApiService(ApiService::class.java)
                        .getAccountTabList()
                        .data()
                }
            },
            success = {
                tabLiveData.postValue(it)
            })

    }
}
