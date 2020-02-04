package com.example.fincalc.ui

import android.graphics.PointF
import com.example.fincalc.R
import com.nightonke.boommenu.BoomButtons.*
import com.nightonke.boommenu.BoomMenuButton


enum class BMBTypes {
    LOAN, DEPOSIT, METALS, CRYPTO
}

fun BoomMenuButton.initialize(type: BMBTypes) {
    createPositions(this)

    when (type) {
        BMBTypes.LOAN -> {
            this.addBuilder(createHamButtonBuilder(R.drawable.ic_bag, R.string.loan, R.string.LoanType, R.color.LoansPrimaryLight))
            this.addBuilder(createHamButtonBuilder(R.drawable.ic_bag, R.string.loan, R.string.LoanType, R.color.LoansPrimaryLight))
            this.addBuilder(createHamButtonBuilder(R.drawable.ic_bag, R.string.loan, R.string.LoanType, R.color.LoansPrimaryLight))
            this.addBuilder(createHamButtonBuilder(R.drawable.ic_bag, R.string.loan, R.string.LoanType, R.color.LoansPrimaryLight))
        }
        BMBTypes.DEPOSIT -> {
            this.addBuilder(createHamButtonBuilder(R.drawable.ic_alert, R.string.deposit, R.string.deposits, R.color.DepPrimaryLight))
            this.addBuilder(createHamButtonBuilder(R.drawable.ic_alert, R.string.deposit, R.string.deposits, R.color.DepPrimaryLight))
            this.addBuilder(createHamButtonBuilder(R.drawable.ic_alert, R.string.deposit, R.string.deposits, R.color.DepPrimaryLight))
            this.addBuilder(createHamButtonBuilder(R.drawable.ic_alert, R.string.deposit, R.string.deposits, R.color.DepPrimaryLight))
        }
        BMBTypes.METALS -> {
            this.addBuilder(createHamButtonBuilder(R.drawable.ic_accounting, R.string.metals, R.string.precious_metals, R.color.MetalsPrimaryLight))
            this.addBuilder(createHamButtonBuilder(R.drawable.ic_accounting, R.string.metals, R.string.precious_metals, R.color.MetalsPrimaryLight))
            this.addBuilder(createHamButtonBuilder(R.drawable.ic_accounting, R.string.metals, R.string.precious_metals, R.color.MetalsPrimaryLight))
        }
        BMBTypes.CRYPTO -> {
            this.addBuilder(createHamButtonBuilder(R.drawable.ic_accounting, R.string.crypto, R.string.precious_metals, R.color.LoansPrimaryLight))
            this.addBuilder(createHamButtonBuilder(R.drawable.ic_accounting, R.string.crypto, R.string.precious_metals, R.color.MetalsPrimaryLight))
            this.addBuilder(createHamButtonBuilder(R.drawable.ic_accounting, R.string.crypto, R.string.precious_metals, R.color.MetalsPrimaryLight))
        }
    }

}

private fun createHamButtonBuilder(
    imageSource: Int, textRes: Int, subText: Int, color: Int
): HamButton.Builder {
    return HamButton.Builder()
        .normalImageRes(imageSource)
        .normalTextRes(textRes)
        .subNormalTextRes(subText)
        .normalColor(color)
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

