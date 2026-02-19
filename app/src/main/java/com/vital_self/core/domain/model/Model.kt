package com.vital_self.core.domain.model

import android.os.Parcelable
import com.biosensesignal.sdk.api.session.demographics.Sex
import com.biosensesignal.sdk.api.session.user_info.SmokingStatus
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Model(
    val subject : SubjectDetails
):Parcelable{
    data class ProfileDetails(
        val profileId: Int?,
        val profile : SubjectDetails
    )
    @Parcelize
    data class SubjectDetails(
        val name:String?,
        val sex : Sex?,
        val age : Double?,
        val weight:Double?,
        val height : Double?,
        val heightUnit : String? = null,
        val weightUnit : String? = null,
        val isSmoker : SmokingStatus = SmokingStatus.UNSPECIFIED
    ):Parcelable

    @Parcelize
    data class VitalsData(
        val id : Int? = 1,
        val vitalName : String,
        val vitalValue : String?,
        val vitalUnit : String?,
        val vitalStatus : Int?,
        val vitalDetail : String?,
        val vitalIcon : Int?,
        val confidenceLevel : String? = null,
        var isExpanded : Boolean? = false,
        var dialogType: String?,
        var category: String?,
        var no_of_state : Int? = 0,
        var range: ArrayList<String>? = arrayListOf<String>(),
        var emojiStatus : Int? = 0,
    ):Parcelable
}
