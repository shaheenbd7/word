package com.shan.word.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import com.shan.word.presentation.ui.GradientTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileParserScreen(
    onBack: () -> Unit,
    onSelectFile: (String) -> Unit,
    onSelectImage: () -> Unit,
    parsingInProgress: Boolean = false,
    wordsFound: Int = 0
) {
    Column(modifier = Modifier.fillMaxSize()) {
        GradientTopAppBar(
            title = { Text("File Parser") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Select File Type",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Document files
            item {
                FileTypeSection(
                    title = "Documents",
                    fileTypes = listOf(
                        FileType("PDF", "application/pdf", Icons.Default.ShoppingCart),// FileType("PDF", "application/pdf", Icons.Default.PictureAsPdf),
                        FileType("Word", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", Icons.Default.AccountBox),// Description
                        FileType("PowerPoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", Icons.Default.Email), // Slideshow
                        FileType("Text", "text/plain", Icons.Default.AccountBox),// FileType("Text", "text/plain", Icons.Default.TextFields),
                        FileType("CSV", "text/csv", Icons.Default.Email),// FileType("CSV", "text/csv", Icons.Default.TableChart),
                        FileType("SRT", "application/x-subrip", Icons.Default.AccountBox)// FileType("SRT", "application/x-subrip", Icons.Default.Subtitles)
                    ),
                    onFileTypeSelected = onSelectFile
                )
            }
            
            // Image files
            item {
                ImageParserSection(
                    onSelectImage = onSelectImage
                )
            }
            
            // Parsing progress
            if (parsingInProgress) {
                item {
                    FileParsingProgressSection(wordsFound = wordsFound)
                }
            }
        }
    }
}

@Composable
fun FileTypeSection(
    title: String,
    fileTypes: List<FileType>,
    onFileTypeSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            fileTypes.forEach { fileType ->
                FileTypeItem(
                    fileType = fileType,
                    onClick = { onFileTypeSelected(fileType.mimeType) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun FileTypeItem(
    fileType: FileType,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = fileType.icon,
                contentDescription = fileType.name,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = fileType.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Select",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ImageParserSection(
    onSelectImage: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Image Parser",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Extract text from images using OCR (Optical Character Recognition)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = onSelectImage,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Face, // Image
                    contentDescription = "Select Image",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Select Image")
            }
        }
    }
}

@Composable
fun FileParsingProgressSection(wordsFound: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 5.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Parsing file...",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$wordsFound words found",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

data class FileType(
    val name: String,
    val mimeType: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
