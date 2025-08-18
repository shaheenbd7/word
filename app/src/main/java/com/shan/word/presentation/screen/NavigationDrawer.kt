package com.shan.word.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigationDrawer(
    drawerState: DrawerState,
    onSelectPdf: () -> Unit,
    onDeleteAll: () -> Unit,
    onShowAllWords: () -> Unit,
    onSelectFile: () -> Unit,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                onSelectPdf = onSelectPdf,
                onDeleteAll = onDeleteAll,
                onShowAllWords = onShowAllWords,
                onSelectFile = onSelectFile
            )
        }
    ) {
        content()
    }
}

@Composable
fun AppDrawerContent(
    onSelectPdf: () -> Unit,
    onDeleteAll: () -> Unit,
    onShowAllWords: () -> Unit,
    onSelectFile: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // User Profile Section
        UserProfileSection()
        
        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
        
        // Menu Items
        DrawerMenuItems(
            onSelectPdf = onSelectPdf,
            onDeleteAll = onDeleteAll,
            onShowAllWords = onShowAllWords,
            onSelectFile = onSelectFile
        )
    }
}

@Composable
fun UserProfileSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // User Name
        Text(
            text = "Word Parser",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Text(
            text = "Document Word Extractor",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun DrawerMenuItems(
    onSelectPdf: () -> Unit,
    onDeleteAll: () -> Unit,
    onShowAllWords: () -> Unit,
    onSelectFile: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        DrawerMenuItem(
            icon = Icons.Default.Face, //PictureAsPdf,
            title = "Select PDF",
            subtitle = "Extract words from PDF files",
            onClick = onSelectPdf
        )
        
        DrawerMenuItem(
            icon = Icons.Default.Face, // Description,
            title = "File Parser",
            subtitle = "Parse Word, PPT, SRT, TXT, CSV files",
            onClick = onSelectFile
        )
        
        Divider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
        
        DrawerMenuItem(
            icon = Icons.Default.List,
            title = "All Words",
            subtitle = "View all extracted words",
            onClick = onShowAllWords
        )
        
        Divider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
        
        DrawerMenuItem(
            icon = Icons.Default.Delete,
            title = "Delete All Words",
            subtitle = "Clear all extracted words",
            onClick = onDeleteAll
        )
    }
}

@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                subtitle?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
} 