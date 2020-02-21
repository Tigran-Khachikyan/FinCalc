package com.example.fincalc.ui

import android.content.Context
import android.graphics.PointF
import com.example.fincalc.R
import com.nightonke.boommenu.BoomButtons.*
import com.nightonke.boommenu.BoomMenuButton


fun BoomMenuButton.initialize(context: Context, isLoan: Boolean) {
    createPositions(this)

    val normTextType =
        context.getString(R.string.Filtering) + " " + if (isLoan) context.getString(R.string.filteredByType) else context.getString(
            R.string.filteredByFreq
        )
    val normTextCur =
        context.getString(R.string.Filtering) + " " + context.getString(R.string.filteredByCur)
    val sortOptions = context.getString(R.string.sortingOptions)
    val deleteAll = context.getString(R.string.remove)

    val subTextType =
        if (isLoan) context.getString(R.string.youWantToFilterWithLoanType)
        else context.getString(R.string.youWantToFilterWithFreq)

    val subTextCur = context.getString(R.string.youWantToFilterWithCur)

    val subSortText = context.getString(R.string.sortingOptionsExtended)

    val subRemoveText = "${context.getString(R.string.remove)} ${
    if (isLoan) context.getString(R.string.allTheLoans)
    else context.getString(R.string.allTheDeposits)}"


    this.addBuilder(createHamButtonBuilder(R.drawable.ic_filter, normTextType, subTextType))
    this.addBuilder(createHamButtonBuilder(R.drawable.ic_base_cur, normTextCur, subTextCur))
    this.addBuilder(createHamButtonBuilder(R.drawable.ic_sort, sortOptions, subSortText))
    this.addBuilder(createHamButtonBuilder(R.drawable.ic_alert, deleteAll, subRemoveText))

}

private fun createHamButtonBuilder(
    imageSource: Int, text: String, textSub: String
): HamButton.Builder {
    return HamButton.Builder()
        .normalImageRes(imageSource)
        .normalText(text)
        .subNormalText(textSub)
        .normalColor(R.color.PortPrimaryDarkVery)
}

private fun createPositions(bmb: BoomMenuButton) {
    val w05 = bmb.hamWidth / 2
    val h05 = bmb.hamHeight / 2
    val hm05 = bmb.pieceHorizontalMargin / 2
    val vm05 = bmb.pieceVerticalMargin / 2

    bmb.customPiecePlacePositions.add(PointF(-w05 - hm05, -h05 - vm05))
    bmb.customPiecePlacePositions.add(PointF(+w05 + hm05, -h05 - vm05))
    bmb.customPiecePlacePositions.add(PointF(-w05 - hm05, +h05 + vm05))
    bmb.customPiecePlacePositions.add(PointF(-w05 - hm05, +h05 + vm05))
}

