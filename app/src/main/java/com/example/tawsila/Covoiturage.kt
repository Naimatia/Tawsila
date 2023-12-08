package com.example.tawsila

import android.os.Parcel
import android.os.Parcelable

data class Covoiturage(
    val id: Long,
    val depart: String,
    val destination: String,
    val phone: String,
    val price: Int,
    val place: Int,
    val bagage: String,
    val description: String,
    val date: String,
    val driver: Long
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(depart)
        parcel.writeString(destination)
        parcel.writeString(phone)
        parcel.writeInt(price)
        parcel.writeInt(place)
        parcel.writeString(bagage)
        parcel.writeString(description)
        parcel.writeString(date)
        parcel.writeLong(driver)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Covoiturage> {
        override fun createFromParcel(parcel: Parcel): Covoiturage {
            return Covoiturage(parcel)
        }

        override fun newArray(size: Int): Array<Covoiturage?> {
            return arrayOfNulls(size)
        }
    }
}
