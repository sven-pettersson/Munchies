package se.scomas.munchies.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import se.scomas.munchies.ui.screen.restaurantdetail.RestaurantDetailScreen
import se.scomas.munchies.ui.screen.restaurantlist.RestaurantListScreen

private const val ROUTE_LIST   = "restaurant_list"
private const val ROUTE_DETAIL = "restaurant_detail/{restaurantId}"
private const val ARG_ID       = "restaurantId"

fun navRouteDetail(restaurantId: String) = "restaurant_detail/$restaurantId"

@Composable
fun MunchiesNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = ROUTE_LIST,
        modifier = modifier
    ) {
        composable(ROUTE_LIST) {
            RestaurantListScreen(
                onRestaurantClick = { id -> navController.navigate(navRouteDetail(id)) }
            )
        }
        composable(
            route = ROUTE_DETAIL,
            arguments = listOf(navArgument(ARG_ID) { type = NavType.StringType })
        ) {
            RestaurantDetailScreen(onBack = { navController.popBackStack() })
        }
    }
}