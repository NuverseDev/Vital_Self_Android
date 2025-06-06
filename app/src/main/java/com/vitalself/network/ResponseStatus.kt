package com.vitalself.network

import android.os.Parcel
import android.os.Parcelable

open class ResponseStatus() : Parcelable {

//    var statusCode = 0
    var message: String? = null

    constructor(parcel: Parcel) : this() {
//        statusCode = parcel.readInt()
        message = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeInt(statusCode)
        parcel.writeString(message)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ResponseStatus> {
        const val STATUS_CODE_SUCCESS = 200
        const val STATUS_CODE_CREATED = 201
        const val STATUS_CODE_ERROR_OAUTH = 401
        const val STATUS_CODE_ERROR_400 = 400
        const val STATUS_CODE_ERROR_409 = 409
        const val STATUS_CODE_ERROR_SERVER = 500
        const val STATUS_CODE_ERROR_404 = 404
        const val STATUS_CODE_CONFLICT = 409

        // This is custom code for handling timeout error
        const val STATUS_CODE_ERROR_TIMEOUT = 5002
        override fun createFromParcel(parcel: Parcel): ResponseStatus {
            return ResponseStatus(parcel)
        }

        override fun newArray(size: Int): Array<ResponseStatus?> {
            return arrayOfNulls(size)
        }
    }
}