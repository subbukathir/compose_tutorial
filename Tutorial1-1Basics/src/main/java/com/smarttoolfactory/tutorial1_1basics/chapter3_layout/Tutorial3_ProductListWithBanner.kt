package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.model.PaginationState
import com.smarttoolfactory.tutorial1_1basics.ui.ComposeTutorialsTheme
import com.smarttoolfactory.tutorial1_1basics.ui.Green400
import com.smarttoolfactory.tutorial1_1basics.ui.Orange400
import com.smarttoolfactory.tutorial1_1basics.ui.components.JumpToTopButton
import com.smarttoolfactory.tutorial1_1basics.ui.components.PaginationControls
import kotlinx.coroutines.launch

// Data class for Product
data class Product(
    val id: Int,
    val name: String,
    val weight: String,
    val price: String,
    val imageRes: Int = android.R.drawable.ic_menu_gallery // placeholder
)

// Data class for Banner
data class Banner(
    val id: Int,
    val title: String,
    val subtitle: String,
    val buttonText: String,
    val backgroundColor: Color,
    val imageRes: Int = android.R.drawable.ic_menu_gallery // placeholder
)

// Sealed class to represent different types of items in the list
sealed class ListItem {
    data class ProductItem(val product: Product) : ListItem()
    data class BannerItem(val banner: Banner) : ListItem()
}

// Sealed class for display items (processed for UI)
sealed class DisplayItem {
    data class ProductRowItem(val leftProduct: Product, val rightProduct: Product?) : DisplayItem()
    data class BannerDisplayItem(val banner: Banner) : DisplayItem()
}

@Composable
fun ProductListWithBannerScreen() {
    ComposeTutorialsTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    backgroundColor = Color.White,
                    elevation = 0.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }
                        
                        Text(
                            text = "Exotics",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                        
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.Black
                            )
                        }
                    }
                }
            },
            backgroundColor = Color.White
        ) { paddingValues ->
            ProductListContent(
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun ProductListContent(modifier: Modifier = Modifier) {
    val allListItems = createSampleData()
    
    // Pagination state management
    var paginationState by remember { 
        mutableStateOf(PaginationState(pageSize = 8, totalItems = allListItems.size))
    }
    
    // Update pagination state when data changes
    LaunchedEffect(allListItems.size) {
        paginationState = paginationState.copy(totalItems = allListItems.size)
    }
    
    // Get paginated items
    val paginatedItems = remember(paginationState.currentPage, allListItems) {
        val startIndex = paginationState.startIndex
        val endIndex = paginationState.endIndex
        allListItems.subList(startIndex, endIndex)
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        val scrollState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()
        
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            val processedItems = processItemsForDisplay(paginatedItems)
            
            items(processedItems) { item ->
                when (item) {
                    is DisplayItem.ProductRowItem -> {
                        ProductRow(
                            leftProduct = item.leftProduct,
                            rightProduct = item.rightProduct
                        )
                    }
                    is DisplayItem.BannerDisplayItem -> {
                        BannerCard(banner = item.banner)
                    }
                }
            }
            
            // Add pagination controls at the bottom
            if (paginationState.totalPages > 1) {
                item {
                    PaginationControls(
                        paginationState = paginationState,
                        onPageChange = { newState ->
                            paginationState = newState
                            // Scroll to top when page changes
                            coroutineScope.launch {
                                scrollState.animateScrollToItem(0)
                            }
                        },
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
        
        // Jump to top button
        val jumpThreshold = with(LocalDensity.current) { 56.dp.toPx() }
        val jumpToTopButtonEnabled by remember {
            derivedStateOf {
                scrollState.firstVisibleItemIndex != 0 ||
                        scrollState.firstVisibleItemScrollOffset > jumpThreshold
            }
        }
        
        JumpToTopButton(
            enabled = jumpToTopButtonEnabled,
            onClicked = {
                coroutineScope.launch {
                    scrollState.animateScrollToItem(0)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}

@Composable
fun ProductRow(
    leftProduct: Product,
    rightProduct: Product?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ProductCard(
            product = leftProduct,
            modifier = Modifier.weight(1f)
        )
        
        if (rightProduct != null) {
            ProductCard(
                product = rightProduct,
                modifier = Modifier.weight(1f)
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = 2.dp,
        backgroundColor = Color.White
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Product Image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = product.name,
                    modifier = Modifier.size(60.dp),
                    contentScale = ContentScale.Fit
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Product Name
            Text(
                text = product.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            
            // Product Weight
            Text(
                text = product.weight,
                fontSize = 10.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Price and Add Button Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.price,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Green400
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.size(width = 50.dp, height = 24.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Add",
                        fontSize = 10.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun BannerCard(
    banner: Banner,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(banner.backgroundColor)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = banner.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    Text(
                        text = banner.subtitle,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = banner.buttonText,
                            fontSize = 12.sp,
                            color = banner.backgroundColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                // Banner Image
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = banner.imageRes),
                        contentDescription = banner.title,
                        modifier = Modifier.size(60.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

// Function to process list items for display (pairing products and inserting banners)
private fun processItemsForDisplay(listItems: List<ListItem>): List<DisplayItem> {
    val displayItems = mutableListOf<DisplayItem>()
    var productQueue = mutableListOf<Product>()
    
    listItems.forEach { item ->
        when (item) {
            is ListItem.ProductItem -> {
                productQueue.add(item.product)
                
                // If we have 2 products, create a row
                if (productQueue.size == 2) {
                    displayItems.add(
                        DisplayItem.ProductRowItem(
                            leftProduct = productQueue[0],
                            rightProduct = productQueue[1]
                        )
                    )
                    productQueue.clear()
                }
            }
            is ListItem.BannerItem -> {
                // If we have a pending product, create a row with just one product
                if (productQueue.isNotEmpty()) {
                    displayItems.add(
                        DisplayItem.ProductRowItem(
                            leftProduct = productQueue[0],
                            rightProduct = null
                        )
                    )
                    productQueue.clear()
                }
                
                // Add the banner
                displayItems.add(DisplayItem.BannerDisplayItem(item.banner))
            }
        }
    }
    
    // Handle any remaining product
    if (productQueue.isNotEmpty()) {
        displayItems.add(
            DisplayItem.ProductRowItem(
                leftProduct = productQueue[0],
                rightProduct = null
            )
        )
    }
    
    return displayItems
}

// Sample data creation
private fun createSampleData(): List<ListItem> {
    val products = listOf(
        Product(1, "All Vegetables", "1 Kg", "₹66"),
        Product(2, "Orange Nagpur", "1 Kg", "₹66"),
        Product(3, "Coriander & Onion", "500 g", "₹66"),
        Product(4, "Exotics", "1 Kg", "₹66"),
        Product(5, "Orange Nagpur", "500 g", "₹66"),
        Product(6, "Fresh Spinach", "1 Kg", "₹45"),
        Product(7, "Tomatoes", "500 g", "₹30"),
        Product(8, "Carrots", "1 Kg", "₹55"),
        Product(9, "Potatoes", "2 Kg", "₹80"),
        Product(10, "Onions", "1 Kg", "₹40"),
        Product(11, "Green Beans", "500 g", "₹70"),
        Product(12, "Bell Peppers", "250 g", "₹85"),
        Product(13, "Broccoli", "500 g", "₹95"),
        Product(14, "Cauliflower", "1 Pc", "₹50"),
        Product(15, "Fresh Mint", "100 g", "₹20"),
        Product(16, "Cucumber", "500 g", "₹35"),
        Product(17, "Lettuce", "200 g", "₹65"),
        Product(18, "Radish", "500 g", "₹25"),
        Product(19, "Cabbage", "1 Kg", "₹35"),
        Product(20, "Beetroot", "500 g", "₹45"),
        Product(21, "Sweet Potato", "1 Kg", "₹75"),
        Product(22, "Ginger", "200 g", "₹35"),
        Product(23, "Garlic", "250 g", "₹40"),
        Product(24, "Green Chili", "100 g", "₹15"),
        Product(25, "Eggplant", "500 g", "₹60"),
        Product(26, "Okra", "250 g", "₹55"),
        Product(27, "Pumpkin", "1 Kg", "₹45"),
        Product(28, "Bottle Gourd", "1 Kg", "₹40"),
        Product(29, "Ridge Gourd", "500 g", "₹50"),
        Product(30, "Bitter Gourd", "250 g", "₹70")
    )
    
    val banners = listOf(
        Banner(
            id = 1,
            title = "Just Arrived!",
            subtitle = "Fresh Badami",
            buttonText = "Shop Now",
            backgroundColor = Orange400
        ),
        Banner(
            id = 2,
            title = "Special Offer",
            subtitle = "50% Off on Organic",
            buttonText = "Get Deal",
            backgroundColor = Green400
        ),
        Banner(
            id = 3,
            title = "New Season",
            subtitle = "Winter Vegetables",
            buttonText = "Explore",
            backgroundColor = Color(0xFF9C27B0)
        )
    )
    
    val items = mutableListOf<ListItem>()
    
    // Add products with banners inserted strategically
    products.forEachIndexed { index, product ->
        items.add(ListItem.ProductItem(product))
        
        // Insert banners at strategic positions
        when (index) {
            7 -> items.add(ListItem.BannerItem(banners[0]))
            17 -> items.add(ListItem.BannerItem(banners[1]))
            27 -> items.add(ListItem.BannerItem(banners[2]))
        }
    }
    
    return items
}

@Preview(showBackground = true)
@Composable
fun ProductListWithBannerPreview() {
    ProductListWithBannerScreen()
}

@Preview(showBackground = true)
@Composable
fun ProductCardPreview() {
    ComposeTutorialsTheme {
        ProductCard(
            product = Product(1, "Orange Nagpur", "1 Kg", "₹66")
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BannerCardPreview() {
    ComposeTutorialsTheme {
        BannerCard(
            banner = Banner(
                id = 1,
                title = "Just Arrived!",
                subtitle = "Badami",
                buttonText = "Shop Now",
                backgroundColor = Orange400
            )
        )
    }
}