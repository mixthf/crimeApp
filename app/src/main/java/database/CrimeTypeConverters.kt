package database

import androidx.room.TypeConverter
import java.util.Date
//type converters tell room the 2 functions that it would need to both input values into the database
//and the function to convert from the database into a format that the user would better understand
class CrimeTypeConverters {
    @TypeConverter
    fun fromDate(date : Date) : Long{
        return date.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch : Long) : Date{
        return Date(millisSinceEpoch)
    }
}