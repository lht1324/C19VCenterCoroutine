package com.overeasy.c19vcentercoroutine.model.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Single

// Room의 Dao.
@Dao
interface VCenterDao {
    // CenterData의 리스트를 DB에서 가져온다.
    @Query("Select * from centerDataTable")
    fun getCenterDatas(): List<CenterData>

    // DB에 CenterData의 리스트를 삽입한다.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(centerDatas: List<CenterData>)
}