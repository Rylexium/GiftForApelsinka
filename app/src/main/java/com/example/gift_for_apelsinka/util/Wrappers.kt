package com.example.gift_for_apelsinka.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.example.gift_for_apelsinka.activity.util.ShowPictureActivity
import com.example.gift_for_apelsinka.activity.main.adapter.ImageViewPageAdapter
import com.example.gift_for_apelsinka.db.model.FieldPhoto
import com.example.gift_for_apelsinka.db.savePicturesToDB
import com.example.gift_for_apelsinka.util.dialogs.DialogEditText
import com.example.gift_for_apelsinka.util.dialogs.ShowToast
import com.example.gift_for_apelsinka.util.listener.DoubleClickListener
import com.example.gift_for_apelsinka.util.views.ImageViewPager
import kotlinx.coroutines.launch

suspend fun wrapperNextPictures(getPicturesFromNetwork : suspend (page : Int) -> List<FieldPhoto>,
                                getPicturesFromBD  : suspend () -> List<FieldPhoto>,
                                pageOf : Int,
                                liveData : MutableLiveData<List<Any>>,
                                defaultList : List<Any>, context: Context) : Pair<Int, Boolean> {

    if(IP.isInternetAvailable(context)) return Pair(pageOf, false)

    var picturesFromNetwork : List<FieldPhoto>

    var picturesFromBD : List<FieldPhoto>
    var page = pageOf
    while(true) {
        picturesFromNetwork = getPicturesFromNetwork(page)
        val previosSize = getPicturesFromBD().size
        savePicturesToDB(picturesFromNetwork.shuffled())
        picturesFromBD = getPicturesFromBD()

        if(picturesFromNetwork.size == 10)
            page += 1

        if(previosSize != picturesFromBD.size) break //таких нет, надо отобразить
        if(picturesFromNetwork.size < 10) break //пришло меньше 10 -> это конец
    }
    if(picturesFromNetwork.isEmpty()) return Pair(page, false)

    val result = liveData.value as MutableList

    if(picturesFromNetwork.map { it.id }.subtract(picturesFromBD.map { it.id }.toSet()).isNotEmpty() ||
        result.subtract(picturesFromNetwork.toSet()).size == defaultList.size) {
        result.addAll(picturesFromBD)
        liveData.value = result.distinct()
    }
    if(picturesFromNetwork.size < 10) return Pair(page, false)
    return Pair(page, true)
}


fun liveDataObserveTextWrapper(liveData: MutableLiveData<String>, textView: TextView, lifecycleOwner: LifecycleOwner) {
    liveData.observe(lifecycleOwner) {
        textView.text = it
    }
}
fun liveDataObserveViewPagerWrapper(liveData: MutableLiveData<List<Any>>, viewPager: ViewPager, lifecycleOwner: LifecycleOwner, activity: Activity) {
    liveData.observe(lifecycleOwner) {
        InitView.initViewPager(viewPager, 0, 0, ImageViewPageAdapter(activity, it))
    }
}

fun wrapperForSwipeOutViewPager(viewPager: ImageViewPager, nextPicture: suspend () -> Boolean,
                                progressBar : ProgressBar, finish: () -> Unit, viewModel : ViewModel, context: Context) {
    viewPager.setOnSwipeOutListener(object : ImageViewPager.OnSwipeOutListener {
        override fun onSwipeOutAtStart() {}

        var isUpdate = false
        override fun onSwipeOutAtEnd() {
            if(!IP.isInternetAvailable(context)) return
            wrapperNothingHappen(context) {
                isUpdate = false
                progressBar.visibility = View.GONE
            }
            
            if(isUpdate) return
            progressBar.visibility = View.VISIBLE
            viewModel.viewModelScope.launch { //долистали до ласт элемента
                val flag : Boolean = nextPicture()
                Handler(Looper.getMainLooper()).postDelayed({ isUpdate = false }, 2_000)
                Handler(Looper.getMainLooper()).post { progressBar.visibility = View.GONE }
                if(!flag) Handler(Looper.getMainLooper()).post { finish() }
            }
        }
    })
}
fun wrapperDisableSwitchLayout(viewPager: ViewPager, swipeRefreshLayout: SwipeRefreshLayout) {
    viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, v: Float, i1: Int) {}
        override fun onPageSelected(position: Int) {}
        override fun onPageScrollStateChanged(state: Int) {
            InitView.enableDisableSwipeRefresh(
                swipeRefreshLayout,
                state == ViewPager.SCROLL_STATE_IDLE
            )
        }
    })
}
fun wrapperEditTextView(textView: TextView, function: () -> Unit, context: Context) {
    textView.setOnClickListener(object : DoubleClickListener(){
        override fun onDoubleClick() {
            DialogEditText.editTextView(textView, context) {
                function()
            }
        }
    })
}
fun wrapperOpenShowPictureActivity(view : View, activity: Activity, image : String) {
    view.setOnClickListener {
        ShowPictureActivity.image = image
        activity.startActivity(Intent(activity, ShowPictureActivity::class.java))
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
fun wrapperNothingHappen(context: Context, runnable: Runnable) {
    Handler(Looper.getMainLooper()).postDelayed({ //если через 10 секунд ничего не происходит
        IP.isInternetAvailable(context)
        runnable.run()
    }, 10_000)
}