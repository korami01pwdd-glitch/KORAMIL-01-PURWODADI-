package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.example.ui.theme.ArmyContainer
import com.example.ui.theme.ArmyGreenDark
import com.example.ui.theme.ArmyGreenPrimary
import com.example.viewmodel.Screen

@Composable
fun PortalBottomNav(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = ArmyGreenDark
    ) {
        NavigationBarItem(
            selected = currentScreen == Screen.HOME,
            onClick = { onNavigate(Screen.HOME) },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dasbor") },
            label = { Text("Dasbor") },
            modifier = Modifier.testTag("nav_item_dashboard"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ArmyGreenDark,
                selectedTextColor = ArmyGreenDark,
                indicatorColor = ArmyContainer,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            selected = currentScreen == Screen.ACTIVITIES || currentScreen == Screen.ADD_ACTIVITY,
            onClick = { onNavigate(Screen.ACTIVITIES) },
            icon = { Icon(Icons.Default.Assignment, contentDescription = "Kegiatan") },
            label = { Text("Kegiatan") },
            modifier = Modifier.testTag("nav_item_activities"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ArmyGreenDark,
                selectedTextColor = ArmyGreenDark,
                indicatorColor = ArmyContainer,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            selected = currentScreen == Screen.SPREADSHEET,
            onClick = { onNavigate(Screen.SPREADSHEET) },
            icon = { Icon(Icons.Default.TableChart, contentDescription = "Spreadsheet") },
            label = { Text("Excel Sheet") },
            modifier = Modifier.testTag("nav_item_spreadsheet"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ArmyGreenDark,
                selectedTextColor = ArmyGreenDark,
                indicatorColor = ArmyContainer,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            selected = currentScreen == Screen.LESSONS || currentScreen == Screen.QUIZ,
            onClick = { onNavigate(Screen.LESSONS) },
            icon = { Icon(Icons.Default.School, contentDescription = "Pelajaran") },
            label = { Text("Pelajaran") },
            modifier = Modifier.testTag("nav_item_lessons"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ArmyGreenDark,
                selectedTextColor = ArmyGreenDark,
                indicatorColor = ArmyContainer,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
    }
}
