package database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.crimeapp.Crime
import kotlinx.coroutines.flow.Flow
import java.util.UUID

//this is where the query commands reside
//a coroutine would allow these functions to be called asynchronously
@Dao
interface CrimeDao {
    //flow used to have constant data stream on both sides
    @Query("SELECT * FROM crime") //query to get the table
    fun getCrimes() : Flow<List<Crime>>

    @Query("SELECT * FROM crime WHERE id = (:id)") //query to get a single entry in the table
    suspend fun getCrime(id: UUID) : Crime

    @Update
    suspend fun updateCrime(crime : Crime)

    @Insert
    suspend fun addCrime(crime: Crime)

    @Delete
    suspend fun deleteCrime(crime: Crime)
}