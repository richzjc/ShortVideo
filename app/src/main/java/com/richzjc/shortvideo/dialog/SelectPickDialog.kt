package com.richzjc.shortvideo.dialog

import android.Manifest
import android.Manifest.permission
import android.app.Dialog
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.huantansheng.easyphotos.EasyPhotos
import com.huantansheng.easyphotos.engine.GlideEngine
import com.huantansheng.easyphotos.listener.FinishSelectListener
import com.huantansheng.easyphotos.models.album.entity.Photo
import com.richzjc.dialoglib.base.BaseDialogFragment
import com.richzjc.shortvideo.R
import com.richzjc.shortvideo.util.MToastHelper
import com.richzjc.shortvideo.util.ResourceUtils
import com.richzjc.shortvideo.util.ScreenUtils
import com.tbruyelle.rxpermissions3.RxPermissionsNew
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.ObservableTransformer

class SelectPickDialog : BaseDialogFragment(), ObservableOnSubscribe<Int> {

    private var emmiter: ObservableEmitter<Int>? = null

    override fun doGetContentViewId() = R.layout.global_dialog_select_pic

    override fun doInitData() {
        view?.findViewById<View>(R.id.take_photo)?.setOnClickListener {
            dismiss()
            emmiter!!.onNext(1)
            emmiter!!.onComplete()
        }

        view?.findViewById<View>(R.id.get_from_pic)?.setOnClickListener {
            dismiss()
            val isPic = arguments?.getBoolean("isPic", true) ?: true
            if(isPic) {
                emmiter!!.onNext(2)
                emmiter!!.onComplete()
            }else{
                emmiter!!.onNext(3)
                emmiter!!.onComplete()
            }
        }

        view?.findViewById<View>(R.id.cancle)?.setOnClickListener {
            dismiss()
            emmiter!!.onNext(0)
            emmiter!!.onComplete()
        }
    }

    override fun getStyle(): Int {
        return R.style.DefaultDialog
    }

    override fun getDialogWidth(): Int {
        return ScreenUtils.getScreenWidth()
    }

    override fun getGravity(): Int {
        return Gravity.BOTTOM
    }

    override fun getDialog(): Dialog? {
        val dialog = super.getDialog()
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        return dialog
    }

    override fun subscribe(emitter: ObservableEmitter<Int>) {
        this.emmiter = emitter
    }
}

fun selectPicFromCameraOrPic(
        activity: FragmentActivity,
        count: Int,
        isPic : Boolean,
        selectList: ArrayList<Photo>?,
        finishSelectListener: FinishSelectListener
) {
    val dialog = SelectPickDialog()
    val bundle = Bundle()
    bundle.putBoolean("isPic", isPic)
    dialog.arguments = bundle

    Observable.create<Int>(dialog)
            .filter { it > 0 }
            .compose(ensure(activity, count, selectList, finishSelectListener))
            .subscribe()

    dialog?.show(activity.supportFragmentManager, "selectPic")
}

fun selectPicFromCamera(
        activity: FragmentActivity,
        finishSelectListener: FinishSelectListener
) {
    Observable.just(1)
            .filter { it > 0 }
            .compose(ensure(activity, 1, null, finishSelectListener))
            .subscribe()
}

fun selectPicFromPic(
        activity: FragmentActivity,
        count: Int,
        selectList: ArrayList<Photo>?,
        finishSelectListener: FinishSelectListener
) {
    Observable.just(2)
            .filter { it > 0 }
            .compose(ensure(activity, count, selectList, finishSelectListener))
            .subscribe()
}


private fun <Int> ensure(
        activity: FragmentActivity,
        count: kotlin.Int,
        selectList: ArrayList<Photo>?,
        finishSelectListener: FinishSelectListener
): ObservableTransformer<Int, Boolean?>? {

    var realCount = count
    if (count <= 0)
        realCount = 1

    return ObservableTransformer { o ->
        o.map {
            if (it == 1) {
                RxPermissionsNew.requestPermissions(
                        activity,
                        "拍摄功能需获取本地相机权限，请确认下一步操作",
                        Manifest.permission.CAMERA
                ).map {
                    if (!it) {
                        MToastHelper.showToast(ResourceUtils.getResStringFromId(R.string.discuss_post_permission))
                    } else {
                        EasyPhotos.createCamera(
                                activity,
                                true
                        )
                                .setFileProviderAuthority(activity.getPackageName() + ".fileProvider")
                                .setCount(1)
                                .setPuzzleMenu(false)
                                .setCleanMenu(false)
                                .setFinishSelectListener(finishSelectListener)
                                .start(101)
                    }
                    it
                }
            } else if (it == 2) {
                val ps: Array<String> = if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
                    arrayOf(
                            permission.READ_MEDIA_IMAGES,
                            permission.READ_MEDIA_VIDEO
                    )
                } else {
                    arrayOf(permission.READ_EXTERNAL_STORAGE)
                }

                RxPermissionsNew.requestPermissions(
                        activity,
                        "使用图片需获取本地相册权限，请确认下一步操作",
                        *ps
                ).map {
                    if (!it) {
                        MToastHelper.showToast(ResourceUtils.getResStringFromId(R.string.discuss_post_permission))
                    } else {
                        EasyPhotos.createAlbum(
                                activity,
                                false,
                                false,
                                GlideEngine.getInstance()
                        ).setFileProviderAuthority(activity.getPackageName() + ".fileProvider")
                                .setSelectedPhotos(selectList ?: ArrayList())
                                .setCount(realCount)
                                .setPuzzleMenu(false)
                                .setCleanMenu(false)
                                .setFinishSelectListener(finishSelectListener)
                                .start(101)
                    }
                    it
                }
            }else if(it== 3){
                val ps: Array<String> = if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
                    arrayOf(
                        permission.READ_MEDIA_VIDEO,
                        permission.READ_MEDIA_VIDEO
                    )
                } else {
                    arrayOf(permission.READ_EXTERNAL_STORAGE)
                }

                RxPermissionsNew.requestPermissions(
                    activity,
                    "使用图片需获取本地相册权限，请确认下一步操作",
                    *ps
                ).map {
                    if (!it) {
                        MToastHelper.showToast(ResourceUtils.getResStringFromId(R.string.discuss_post_permission))
                    } else {
                        EasyPhotos.createAlbum(
                            activity,
                            false,
                            false,
                            GlideEngine.getInstance()
                        ).setFileProviderAuthority(activity.getPackageName() + ".fileProvider")
                            .setSelectedPhotos(selectList ?: ArrayList())
                            .setVideo(true)
                            .setCount(realCount)
                            .setPuzzleMenu(false)
                            .setCleanMenu(false)
                            .setFinishSelectListener(finishSelectListener)
                            .start(101)
                    }
                    it
                }
            } else {
                Observable.just(false)
            }
        }.flatMap {
            it
        }
    }
}