package com.llt.dragon.ui.main.tab

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.llt.baselibrary.base.BaseViewModel

/**
 * des tab
 * @date 2020/7/7
 * @author zs
 */
class TabVM :BaseViewModel(){

    private val repo by lazy { TabRepo(viewModelScope,errorLiveData) }
    /**
     * tab
     */
    val tabLiveData = MutableLiveData<MutableList<TabBean>>()

    fun getTab(type:Int){
        repo.getTab(type,tabLiveData)
    }
}