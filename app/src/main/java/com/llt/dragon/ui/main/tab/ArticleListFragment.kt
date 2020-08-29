package com.llt.dragon.ui.main.tab

import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.SimpleItemAnimator
import com.chad.library.adapter.base.BaseQuickAdapter
import com.llt.baselibrary.base.DataBindingConfig
import com.llt.baselibrary.base.LazyVmFragment
import com.llt.baselibrary.common.smartConfig
import com.llt.baselibrary.common.smartDismiss
import com.llt.dragon.BR
import com.llt.dragon.R
import com.llt.dragon.common.OnChildItemClickListener
import com.llt.dragon.ui.main.home.ArticleAdapter
import com.llt.dragon.ui.main.home.ArticleBean
import com.llt.dragon.utils.CacheUtil
import kotlinx.android.synthetic.main.fragment_article.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.smartRefresh

/**
 * des 文章列表fragment
 */
class ArticleListFragment : LazyVmFragment() , OnChildItemClickListener {

    private var articleVM: ArticleVM? = null

    /**
     * fragment类型，项目或公号
     */
    private var type = 0

    /**
     * tab的id
     */
    private var tabId = 0

    /**
     * 文章适配器
     */
    private val adapter by lazy { ArticleAdapter(mutableListOf()) }

    override fun initViewModel() {
        articleVM = getFragmentViewModel(ArticleVM::class.java)
    }

    override fun observe() {
        articleVM?.articleLiveData?.observe(this, Observer {
            smartDismiss(smartRefresh)
            adapter.setNewData(it as List<ArticleBean.DatasBean>?)
        })
        //收藏
        articleVM?.collectLiveData?.observe(this, Observer {
            adapter.collectNotifyById(it)
        })
        //取消收藏
        articleVM?.unCollectLiveData?.observe(this, Observer {
            adapter.unCollectNotifyById(it)
        })
        articleVM?.errorLiveData?.observe(this, Observer {

        })
    }

    override fun lazyInit() {
        type = arguments?.getInt("type") ?: 0
        tabId = arguments?.getInt("tabId") ?: 0
        initView()
        loadData()
    }

    override fun initView() {
        //关闭更新动画
        (rvArticleList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        //下拉刷新
        smartRefresh.setOnRefreshListener {
            articleVM?.getArticle(type, tabId, true)
        }
        //上拉加载
        smartRefresh.setOnLoadMoreListener {
            articleVM?.getArticle(type,tabId,false)
        }
        smartConfig(smartRefresh)
        adapter.apply {
            setOnChildItemClickListener(this@ArticleListFragment)
            rvArticleList.adapter = this
            //setDiffCallback(ArticleDiff())
        }
    }

    override fun loadData() {
        smartRefresh.autoRefresh()
    }

    override fun getLayoutId() = R.layout.fragment_article

    override fun getDataBindingConfig(): DataBindingConfig? {
        return DataBindingConfig(R.layout.fragment_article, articleVM)
            .addBindingParam(BR.vm, articleVM)
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        when(view.id){
            //item
            R.id.root->{
                nav().navigate(R.id.action_main_fragment_to_web_fragment
                    ,this@ArticleListFragment.adapter.getBundle(position))
            }
            //收藏
            R.id.ivCollect->{
                //已登陆
                if (CacheUtil.isLogin()){
                    this@ArticleListFragment.adapter.data[position].apply {
                        //已收藏取消收藏
                        if (collect){
                            articleVM?.unCollect(id)
                        }else{
                            articleVM?.collect(id)
                        }
                    }
                }else{
                    //未登陆跳登陆页
                  //  nav().navigate(R.id.action_main_fragment_to_login_fragment)
                }
            }
        }
    }
}