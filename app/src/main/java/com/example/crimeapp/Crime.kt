package com.example.crimeapp


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

//tell room that this class would be it own entity in the database
@Entity
data class Crime (
    @PrimaryKey val id: UUID, //tell room that this is the primary key for this entity
    var title: String,
    val date : Date,
    val isSolved : Boolean,
    val suspect : String = ""
)


