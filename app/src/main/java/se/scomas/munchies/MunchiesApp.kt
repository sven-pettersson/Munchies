package se.scomas.munchies

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import se.scomas.munchies.shared.di.sharedModule
import se.scomas.munchies.ui.screen.restaurantdetail.RestaurantDetailViewModel
import se.scomas.munchies.ui.screen.restaurantlist.RestaurantListViewModel

class MunchiesApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MunchiesApp)
            modules(sharedModule, appModule)
        }
    }
}

private val appModule = module {
    viewModel { RestaurantListViewModel(get(), get()) }
    viewModel { params -> RestaurantDetailViewModel(params.get(), get()) }
}
