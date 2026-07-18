package com.example.controlgastos.di

import android.content.Context
import androidx.room.Room
import com.example.controlgastos.data.local.db.AppDatabase
import com.example.controlgastos.data.repository.CategoriaRepositoryImpl
import com.example.controlgastos.data.repository.GastoRepositoryImpl
import com.example.controlgastos.data.repository.UsuarioRepositoryImpl
import com.example.controlgastos.domain.repository.CategoriaRepository
import com.example.controlgastos.domain.repository.GastoRepository
import com.example.controlgastos.domain.repository.UsuarioRepository
import com.example.controlgastos.domain.usecase.ActualizarGastoUseCase
import com.example.controlgastos.domain.usecase.AgregarGastoUseCase
import com.example.controlgastos.domain.usecase.CategoriaUseCase
import com.example.controlgastos.domain.usecase.EliminarGastoUseCase
import com.example.controlgastos.domain.usecase.GastoUseCase
import com.example.controlgastos.domain.usecase.GuardarPerfilUseCase
import com.example.controlgastos.domain.usecase.InsertarCategoriasInicialesUseCase
import com.example.controlgastos.domain.usecase.ObtenerCategoriasUseCase
import com.example.controlgastos.domain.usecase.ObtenerGastosUseCase
import com.example.controlgastos.domain.usecase.ObtenerPerfilUseCase
import com.example.controlgastos.domain.usecase.ObtenerTotalGastadoUseCase
import com.example.controlgastos.domain.usecase.ObtenerTotalPorMesUseCase
import com.example.controlgastos.domain.usecase.UsuarioUseCase
import com.example.controlgastos.presentation.viewmodel.UsuarioViewModel
import com.example.controlgastos.presentation.viewmodel.GastoViewModel
import com.example.controlgastos.data.local.preferences.ConfiguracionDataStore
import com.example.controlgastos.data.repository.ConfiguracionRepositoryImpl
import com.example.controlgastos.domain.repository.ConfiguracionRepository
import com.example.controlgastos.domain.usecase.ConfiguracionUseCase
import com.example.controlgastos.domain.usecase.GuardarConfiguracionUseCase
import com.example.controlgastos.domain.usecase.ObtenerConfiguracionUseCase
import com.example.controlgastos.presentation.viewmodel.ConfiguracionViewModel

object AppModule {

    private var database: AppDatabase? = null

    private fun provideDatabase(context: Context): AppDatabase {
        return database ?: synchronized(this) {
            database ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "control_gastos_db"
            )
                .fallbackToDestructiveMigration()
                .build()
                .also { database = it }
        }
    }

    private fun provideUsuarioDao(context: Context) =
        provideDatabase(context).usuarioDao()

    private fun provideCategoriaDao(context: Context) =
        provideDatabase(context).categoriaDao()

    private fun provideGastoDao(context: Context) =
        provideDatabase(context).gastoDao()

    private fun provideUsuarioRepository(context: Context): UsuarioRepository {
        return UsuarioRepositoryImpl(provideUsuarioDao(context))
    }

    private fun provideCategoriaRepository(context: Context): CategoriaRepository {
        return CategoriaRepositoryImpl(provideCategoriaDao(context))
    }

    private fun provideGastoRepository(context: Context): GastoRepository {
        return GastoRepositoryImpl(provideGastoDao(context))
    }

    private var configuracionDataStore:
            ConfiguracionDataStore? = null

    private fun provideConfiguracionDataStore(
        context: Context
    ): ConfiguracionDataStore {
        return configuracionDataStore
            ?: ConfiguracionDataStore(
                context.applicationContext
            ).also {
                configuracionDataStore = it
            }
    }

    private fun provideConfiguracionRepository(
        context: Context
    ): ConfiguracionRepository {
        return ConfiguracionRepositoryImpl(
            provideConfiguracionDataStore(context)
        )
    }

    fun provideConfiguracionUseCases(
        context: Context
    ): ConfiguracionUseCase {
        val repository =
            provideConfiguracionRepository(context)

        return ConfiguracionUseCase(
            obtenerConfiguracion =
                ObtenerConfiguracionUseCase(repository),
            guardarConfiguracion =
                GuardarConfiguracionUseCase(repository)
        )
    }

    fun provideConfiguracionViewModel(
        context: Context
    ): ConfiguracionViewModel {
        return ConfiguracionViewModel(
            usuarioUseCase =
                provideUsuarioUseCases(context),
            configuracionUseCase =
                provideConfiguracionUseCases(context)
        )
    }

    fun provideUsuarioUseCases(context: Context): UsuarioUseCase {
        val repository = provideUsuarioRepository(context)

        return UsuarioUseCase(
            guardarPerfil = GuardarPerfilUseCase(repository),
            obtenerPerfil = ObtenerPerfilUseCase(repository)
        )
    }

    fun provideCategoriaUseCases(context: Context): CategoriaUseCase {
        val repository = provideCategoriaRepository(context)

        return CategoriaUseCase(
            insertarIniciales = InsertarCategoriasInicialesUseCase(repository),
            obtenerCategorias = ObtenerCategoriasUseCase(repository)
        )
    }

    fun provideGastoUseCases(context: Context): GastoUseCase {
        val repository = provideGastoRepository(context)

        return GastoUseCase(
            agregarGasto = AgregarGastoUseCase(repository),
            obtenerGastos = ObtenerGastosUseCase(repository),
            actualizarGasto = ActualizarGastoUseCase(repository),
            eliminarGasto = EliminarGastoUseCase(repository),
            obtenerTotalGastado = ObtenerTotalGastadoUseCase(repository),
            obtenerTotalPorMes = ObtenerTotalPorMesUseCase(repository)
        )
    }

    fun provideUsuarioViewModel(context: Context): UsuarioViewModel {
        return UsuarioViewModel(provideUsuarioUseCases(context))
    }

    fun provideGastoViewModel(context: Context): GastoViewModel {
        return GastoViewModel(
            gastoUseCase = provideGastoUseCases(context),
            categoriaUseCase = provideCategoriaUseCases(context)
        )
    }
}