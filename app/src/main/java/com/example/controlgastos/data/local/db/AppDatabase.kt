package com.example.controlgastos.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.controlgastos.data.local.dao.CategoriaDao
import com.example.controlgastos.data.local.dao.GastoDao
import com.example.controlgastos.data.local.dao.UsuarioDao
import com.example.controlgastos.data.local.entity.CategoriaEntity
import com.example.controlgastos.data.local.entity.GastoEntity
import com.example.controlgastos.data.local.entity.UsuarioEntity

@Database(
    entities = [
        UsuarioEntity::class,
        CategoriaEntity::class,
        GastoEntity::class
    ],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun gastoDao(): GastoDao
}