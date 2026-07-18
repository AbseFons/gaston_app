package com.example.controlgastos.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.controlgastos.data.local.entity.GastoEntity

@Dao
interface GastoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(gasto: GastoEntity)

    @Update
    suspend fun actualizar(gasto: GastoEntity)

    @Delete
    suspend fun eliminar(gasto: GastoEntity)

    @Query("SELECT * FROM gastos ORDER BY fecha DESC")
    suspend fun obtenerGastos(): List<GastoEntity>

    @Query("SELECT * FROM gastos WHERE id = :id LIMIT 1")
    suspend fun obtenerGastoPorId(id: Int): GastoEntity?

    @Query("SELECT * FROM gastos WHERE categoriaId = :categoriaId ORDER BY fecha DESC")
    suspend fun obtenerGastosPorCategoria(categoriaId: Int): List<GastoEntity>

    @Query("SELECT SUM(monto) FROM gastos")
    suspend fun obtenerTotalGastado(): Double?

    @Query("SELECT SUM(monto) FROM gastos WHERE fecha LIKE :mes || '%'")
    suspend fun obtenerTotalPorMes(mes: String): Double?
}