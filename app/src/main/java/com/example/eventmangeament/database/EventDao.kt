package com.example.eventmangeament.database

import androidx.room.*
import com.example.eventmangeament.userinfo.*

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun eventEntryDetails(userEntryDetails: UserEntryDetails)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun syncEntryDetails(syncEntryDetails: SyncEntryDetails)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun TempData(syncEntryDetails: TempData)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun allScannedDetails(allScannedDetails: AllScannedDetails)

    @Query("SELECT * FROM all_scanned_details WHERE rfidNumber = :rfidTag")
    fun getAllScannedDetails(rfidTag: String):AllScannedDetails

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateVehicleImage(items: AllScannedDetails)

    @Query("SELECT * FROM sync_entry_details WHERE RfidNo = :rfidTag")
    fun getDataFromSyncTable(rfidTag: String):SyncEntryDetails

    @Query("SELECT * FROM all_scanned_details")
    fun getAllData(): List<AllScannedDetails>

    @Query("DELETE FROM  sync_entry_details ")
    fun deleteSync()

    @Query("DELETE FROM  all_scanned_details ")
    fun deleteAllScanned()

    @Query("SELECT COUNT(*) FROM sync_entry_details")
    suspend fun getRowCount(): Int
}