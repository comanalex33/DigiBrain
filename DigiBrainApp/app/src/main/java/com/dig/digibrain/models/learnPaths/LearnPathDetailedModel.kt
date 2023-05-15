package com.dig.digibrain.models.learnPaths

import android.os.Parcel
import android.os.Parcelable

class LearnPathDetailedModel(
    var id: Long,
    var title: String,
    var description: String,
    var author: String,
    var date: String,
    var started: Long,
    var subject: String?,
    var classNumber: Long,
    var imageName: String?
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(author)
        parcel.writeString(date)
        parcel.writeLong(started)
        parcel.writeString(subject)
        parcel.writeLong(classNumber)
        parcel.writeString(imageName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LearnPathDetailedModel> {
        override fun createFromParcel(parcel: Parcel): LearnPathDetailedModel {
            return LearnPathDetailedModel(parcel)
        }

        override fun newArray(size: Int): Array<LearnPathDetailedModel?> {
            return arrayOfNulls(size)
        }
    }
}