package com.pdm.esas.ui.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument


enum class Role {
    admin,
    volunteer,
}


object Destination {
    data object Home : Screen("home", requiredRoles = emptyList())
    data object Login : Screen("login", requiredRoles = emptyList())
    data object Report : Screen("report", requiredRoles = listOf(Role.admin.name))
    //data object Visit : Screen("visit"/*, requiredRoles = listOf("visitor")*/)

    data object Calendar :
        Screen("calendar") {
        data object Task :
            DynamicScreen("calendar/task", "taskId")
    }



    data object TaskDetail : Screen("task") {
        data object Task :
            DynamicScreen("task", "taskId", requiredRoles = listOf("admin"))
    }

    data object Visit : Screen("visit"){
        data object Visit :
                DynamicScreen("visit","visitId", requiredRoles = listOf("admin"))
    }


    abstract class Screen(
        baseRoute: String, val requiredRoles: List<String> = emptyList()
    ) {
        companion object {
            const val BASE_DEEPLINK_URL = "app://esas"
        }

        open val route = baseRoute
        open val deeplink = "$BASE_DEEPLINK_URL/$baseRoute"
    }


    abstract class DynamicScreen(
        private val baseRoute: String,
        private val routeArgName: String,
        requiredRoles: List<String> = emptyList()
    ) : Screen(baseRoute, requiredRoles) {

        val navArguments = listOf(navArgument(routeArgName) { type = NavType.StringType })

        override val route = "$baseRoute/{$routeArgName}"
        override val deeplink = "$BASE_DEEPLINK_URL/$baseRoute/{$routeArgName}"

        fun dynamicRoute(param: String) = "$baseRoute/$param"

        fun dynamicDeeplink(param: String) = "$BASE_DEEPLINK_URL/$baseRoute/$param"
    }


    fun hasAccess(requiredRoles: List<String>, userRoles: List<String>): Boolean {
        return requiredRoles.isEmpty() || requiredRoles.any { it in userRoles }
    }

}
