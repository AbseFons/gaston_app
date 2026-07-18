package com.example.controlgastos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.controlgastos.data.local.entity.CategoriaEntity

@Dao
interface CategoriaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(categoria: CategoriaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(categorias: List<CategoriaEntity>)

    @Query("SELECT * FROM categorias ORDER BY nombre ASC")
    suspend fun obtenerCategorias(): List<CategoriaEntity>

    @Query("SELECT COUNT(*) FROM categorias")
    suspend fun contarCategorias(): Int
}