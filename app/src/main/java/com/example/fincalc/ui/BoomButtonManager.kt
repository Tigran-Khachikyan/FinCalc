package com.example.fincalc.ui

import android.graphics.PointF
import com.example.fincalc.R
import com.nightonke.boommenu.BoomButtons.*
import com.nightonke.boommenu.BoomMenuButton


fun BoomMenuButton.initialize() {
    for (i in 0 until this.buttonPlaceEnum.buttonNumber()) this.addBuilder(hamButtonBuilder)
    val w05 = this.hamWidth / 2
    val h05 = this.hamHeight / 2
    val hm05 = this.pieceHorizontalMargin / 2
    val vm05 = this.pieceVerticalMargin / 2
    this.customPiecePlacePositions.add(PointF(-w05 - hm05, -h05 - vm05))
    this.customPiecePlacePositions.add(PointF(+w05 + hm05, -h05 - vm05))
    this.customPiecePlacePositions.add(PointF(-w05 - hm05, +h05 + vm05))
    this.customPiecePlacePositions.add(PointF(-w05 - hm05, +h05 + vm05))
}

private val imageResources: IntArray = intArrayOf(
    R.drawable.ic_accounting,
    R.drawable.ic_accounting,
    R.drawable.ic_expand_less_black_24dp,
    R.drawable.ic_expand_less_black_24dp
)

private var imageResourceIndex: Int = 0

private val imageResource: Int
    get() {
        if (imageResourceIndex >= imageResources.size)
            imageResourceIndex = 0
        return imageResources[imageResourceIndex++]
    }

val hamButtonBuilder: HamButton.Builder
    get() {
        return HamButton.Builder()
            .normalImageRes(imageResource)
            .normalTextRes(R.string.app_name)
            .subNormalTextRes(R.string.app_name)
            .normalColor(R.color.colorAccent)
    }
