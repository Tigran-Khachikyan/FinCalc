package com.example.fincalc.data.network.firebase

import com.google.firebase.firestore.DocumentSnapshot
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs


private const val SERVER_TIME_ZONE = "Etc/GMT-4"
private const val FORMAT = "yyyy-MM-dd HH:mm:ss"


fun duration(serverTime: Date, curTime: Date): Long {

    val differenceInSeconds = abs(curTime.time - serverTime.time)
    return TimeUnit.SECONDS.convert(differenceInSeconds, TimeUnit.MILLISECONDS)
}

fun String.getDateWithServerTimeStamp(): Date? {
    val dateFormat = SimpleDateFormat(FORMAT, Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone(SERVER_TIME_ZONE)
    return try {
        dateFormat.parse(this)
    } catch (e: ParseException) {
        null
    }
}

/** Converting from Date to String**/
fun Date.getStringTimeStampWithDate(): String {

    val dateFormat = SimpleDateFormat(FORMAT, Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone(SERVER_TIME_ZONE)
    return dateFormat.format(this)
}

fun DocumentSnapshot.getTime(): Date? {

    val value = this[DATE_TIME] as String?
    return value?.getDateWithServerTimeStamp()
}