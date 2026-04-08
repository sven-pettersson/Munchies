package se.scomas.munchies.shared.di

import org.koin.dsl.module
import se.scomas.munchies.shared.network.HttpClientFactory
import se.scomas.munchies.shared.network.MunchiesApi
import se.scomas.munchies.shared.network.MunchiesApiImpl
import se.scomas.munchies.shared.repository.MunchiesRepository
import se.scomas.munchies.shared.repository.MunchiesRepositoryImpl

val sharedModule = module {
    single { HttpClientFactory.create() }
    single<MunchiesApi> { MunchiesApiImpl(get()) }
    single<MunchiesRepository> { MunchiesRepositoryImpl(get()) }
}
