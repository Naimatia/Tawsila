package com.example.tawsila

import android.os.Parcel
import android.os.Parcelable

data class Covoiturage(
    val id: Long,
    val driver: Long,
    val depart: String,
    val destination: String,
    val phone: String,
    val price: Double,
    val place: Int,
    val  date: String,
    val bagage: String,
    val  marque: String,
    val  heureDepart: String,
    val  heureArrive: String

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeLong(driver)
        parcel.writeString(depart)
        parcel.writeString(destination)
        parcel.writeString(phone)
        parcel.writeDouble(price)
        parcel.writeInt(place)
        parcel.writeString(date)
        parcel.writeString(bagage)
        parcel.writeString(marque)
        parcel.writeString(heureDepart)
        parcel.writeString(heureArrive)
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
