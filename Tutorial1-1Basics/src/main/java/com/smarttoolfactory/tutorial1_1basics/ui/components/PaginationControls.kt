package com.smarttoolfactory.tutorial1_1basics.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.model.PaginationState
import kotlin.math.max
import kotlin.math.min

@Composable
fun PaginationControls(
    paginationState: PaginationState,
    onPageChange: (PaginationState) -> Unit,
    modifier: Modifier = Modifier
) {
    if (paginationState.totalPages <= 1) return
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colors.surface,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Page info
        Text(
            text = "Showing ${paginationState.startIndex + 1}-${paginationState.endIndex} of ${paginationState.totalItems} tutorials",
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.size(12.dp))
        
        // Navigation controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous button
            Button(
                onClick = { onPageChange(paginationState.previousPage()) },
                enabled = paginationState.hasPreviousPage,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary,
                    disabledBackgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Previous page"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Previous")
            }
            
            // Page numbers
            PageNumbers(
                paginationState = paginationState,
                onPageClick = { page ->
                    onPageChange(paginationState.goToPage(page))
                },
                modifier = Modifier.weight(2f)
            )
            
            // Next button
            Button(
                onClick = { onPageChange(paginationState.nextPage()) },
                enabled = paginationState.hasNextPage,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary,
                    disabledBackgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Next")
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next page"
                )
            }
        }
    }
}

@Composable
private fun PageNumbers(
    paginationState: PaginationState,
    onPageClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val currentPage = paginationState.currentPage
        val totalPages = paginationState.totalPages
        
        // Calculate which pages to show
        val maxVisiblePages = 5
        val halfVisible = maxVisiblePages / 2
        
        val startPage = max(0, min(currentPage - halfVisible, totalPages - maxVisiblePages))
        val endPage = min(totalPages - 1, startPage + maxVisiblePages - 1)
        
        // Show first page if not in visible range
        if (startPage > 0) {
            PageNumber(
                page = 0,
                isSelected = false,
                onClick = onPageClick
            )
            if (startPage > 1) {
                Text(
                    text = "...",
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
        
        // Show visible pages
        for (page in startPage..endPage) {
            PageNumber(
                page = page,
                isSelected = page == currentPage,
                onClick = onPageClick
            )
        }
        
        // Show last page if not in visible range
        if (endPage < totalPages - 1) {
            if (endPage < totalPages - 2) {
                Text(
                    text = "...",
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
            PageNumber(
                page = totalPages - 1,
                isSelected = false,
                onClick = onPageClick
            )
        }
    }
}

@Composable
private fun PageNumber(
    page: Int,
    isSelected: Boolean,
    onClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(
                color = if (isSelected) MaterialTheme.colors.primary 
                       else Color.Transparent
            )
            .border(
                width = if (isSelected) 0.dp else 1.dp,
                color = if (isSelected) Color.Transparent 
                       else MaterialTheme.colors.onSurface.copy(alpha = 0.3f),
                shape = CircleShape
            )
            .clickable { onClick(page) }
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${page + 1}",
            color = if (isSelected) MaterialTheme.colors.onPrimary 
                   else MaterialTheme.colors.onSurface,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}