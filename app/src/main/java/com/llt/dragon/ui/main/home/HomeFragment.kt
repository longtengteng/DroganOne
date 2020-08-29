package com.llt.dragon.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.SimpleItemAnimator
import cn.bingoogolapple.bgabanner.BGABanner
import com.chad.library.adapter.base.BaseQuickAdapter
import com.llt.baselibrary.base.DataBindingConfig
import com.llt.baselibrary.base.LazyVmFragment
import com.llt.baselibrary.common.*
import com.llt.dragon.BR
import com.llt.dragon.R
import com.llt.dragon.common.OnChildItemClickListener
import com.llt.dragon.utils.CacheUtil
import com.zs.wanandroid.entity.BannerBean
import kotlinx.android.synthetic.main.fragment_home.*


/**
 * des 首页
 */
class HomeFragment : LazyVmFragment(), BGABanner.Adapter<ImageView?, String?>
    , BGABanner.Delegate<ImageView?, String?> , OnChildItemClickListener {

    private var homeVm: HomeVM? = null
    private var bannerList: MutableList<BannerBean>? = null
    private val adapter by lazy { ArticleAdapter(mutableListOf()) }
    private val head by lazy {
        LayoutInflater.from(mActivity).inflate(R.layout.banner_head, null)
    }
    private val banner by lazy {
        head.findViewById(R.id.banner) as BGABanner
    }

    /**
     * 页码
     */
    private var page = 0
    override fun initViewModel() {
        homeVm = getActivityViewModel(HomeVM::class.java)
    }

    override fun observe() {
        //文章列表
        homeVm?.articleList?.observe(this, Observer {
            smartDismiss(smartRefresh)
            adapter.setNewData(it)
        })
        //banner
        homeVm?.banner?.observe(this, Observer {
            bannerList = it
            initBanner()
        })
        //收藏
        homeVm?.collectLiveData?.observe(this, Observer {
            adapter.collectNotifyById(it)
        })
        //取消收藏
        homeVm?.unCollectLiveData?.observe(this, Observer {
            adapter.unCollectNotifyById(it)
        })
        //请求错误
        homeVm?.errorLiveData?.observe(this, Observer {
            smartDismiss(smartRefresh)
        })
    }


    override fun lazyInit() {
        initView()
        loadData()
    }

    override fun initView() {
        //关闭更新动画
        (rvHomeList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        smartRefresh.setOnRefreshListener {
            page = 0
            homeVm?.getArticleList(true)
        }
        //上拉加载
        smartRefresh.setOnLoadMoreListener {
            page++
            homeVm?.getArticleList(false)
        }
        smartConfig(smartRefresh)
        adapter.apply {
            setOnChildItemClickListener(this@HomeFragment)
            //将banner添加至recyclerView
           // removeHeaderView(head)
            addHeaderView(head)
            rvHomeList.adapter = this
            //setDiffCallback(ArticleDiff())
        }
        setNoRepeatClick(ivAdd){
            when(it.id){
               // R.id.ivAdd ->nav().navigate(R.id.action_main_fragment_to_publish_fragment)
            }
        }
    }

    override fun loadData() {
        //自动刷新
        smartRefresh.autoRefresh()
    }

    override fun onClick() {
        setNoRepeatClick(clSearch) {
            when (it.id) {
             //   R.id.clSearch -> nav().navigate(R.id.action_main_fragment_to_search_fragment)
            }
        }
    }

    override fun getLayoutId() = R.layout.fragment_home

    override fun getDataBindingConfig(): DataBindingConfig? {
        return DataBindingConfig(R.layout.fragment_home, homeVm)
            .addBindingParam(BR.vm, homeVm)
    }

    /**
     * 填充banner
     */
    override fun fillBannerItem(
        banner: BGABanner?,
        itemView: ImageView?,
        model: String?,
        position: Int
    ) {
        itemView?.apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            loadUrl(mActivity, bannerList?.get(position)?.imagePath!!)
        }
    }

    /**
     * banner点击事件
     */
    override fun onBannerItemClick(
        banner: BGABanner?,
        itemView: ImageView?,
        model: String?,
        position: Int
    ) {
        nav().navigate(R.id.action_main_fragment_to_web_fragment
            ,Bundle().apply {
                bannerList?.get(position)?.let {
                    putString("loadUrl",it.url)
                    putString("title",it.title)
                    putInt("id",it.id)
                }
            })
    }

    /**
     * 初始化banner
     */
    private fun initBanner() {
        banner.apply {
            setAutoPlayAble(true)
            val views: MutableList<View> = ArrayList()
            bannerList?.forEach { _ ->
                views.add(ImageView(mActivity).apply {
                    setBackgroundResource(R.drawable.ripple_bg)
                })
            }
            setAdapter(this@HomeFragment)
            setDelegate(this@HomeFragment)
            setData(views)
        }
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        when(view.id){
            //item
            R.id.root->{
                nav().navigate(R.id.action_main_fragment_to_web_fragment
                    ,this@HomeFragment.adapter.getBundle(position))
            }
            //收藏
            R.id.ivCollect->{
                if (CacheUtil.isLogin()){
                    this@HomeFragment.adapter.data[position].apply {
                        //已收藏取消收藏
                        if (collect){
                            homeVm?.unCollect(id)
                        }else{
                            homeVm?.collect(id)
                        }
                    }
                }else{
               //     nav().navigate(R.id.action_main_fragment_to_login_fragment)
                }
            }
        }
    }
}
