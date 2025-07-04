package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.text.style.TextDecoration
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
    val originalPrice: String? = null, // For showing crossed-out original price
    val discountPercentage: Int? = null, // For discount badge (e.g., 4% OFF)
    val imageRes: Int = android.R.drawable.ic_menu_gallery // placeholder
)

// Data class for Frequently Bought Together Product
data class FrequentlyBoughtProduct(
    val id: Int,
    val name: String,
    val weight: String,
    val price: String,
    val originalPrice: String? = null,
    val discountPercentage: Int? = null,
    val imageRes: Int = android.R.drawable.ic_menu_gallery
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
    
    // Frequently bought together state
    var showFrequentlyBought by remember { mutableStateOf(false) }
    var frequentlyBoughtProducts by remember { mutableStateOf<List<FrequentlyBoughtProduct>>(emptyList()) }
    var addedProductId by remember { mutableStateOf<Int?>(null) }
    
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
            
            processedItems.forEachIndexed { index, displayItem ->
                when (displayItem) {
                    is DisplayItem.ProductRowItem -> {
                        item(key = "product_row_$index") {
                            ProductRow(
                                leftProduct = displayItem.leftProduct,
                                rightProduct = displayItem.rightProduct,
                                onAddToCart = { product ->
                                    // Simulate API call to get frequently bought together products
                                    frequentlyBoughtProducts = getFrequentlyBoughtTogether(product.id)
                                    addedProductId = product.id
                                    showFrequentlyBought = true
                                }
                            )
                        }
                        
                        // Add frequently bought together section right after the product that was added
                        if (showFrequentlyBought && frequentlyBoughtProducts.isNotEmpty() && 
                            (displayItem.leftProduct.id == addedProductId || displayItem.rightProduct?.id == addedProductId)) {
                            item(key = "frequently_bought_$index") {
                                FrequentlyBoughtTogetherSection(
                                    products = frequentlyBoughtProducts,
                                    onDismiss = { 
                                        showFrequentlyBought = false
                                        addedProductId = null
                                    },
                                    onAddToCart = { product ->
                                        // Handle adding frequently bought product to cart
                                    },
                                    modifier = Modifier.padding(top = 16.dp)
                                )
                            }
                        }
                    }
                    is DisplayItem.BannerDisplayItem -> {
                        item(key = "banner_$index") {
                            BannerCard(banner = displayItem.banner)
                        }
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
    rightProduct: Product?,
    onAddToCart: (Product) -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ProductCard(
            product = leftProduct,
            onAddToCart = onAddToCart,
            modifier = Modifier.weight(1f)
        )
        
        if (rightProduct != null) {
            ProductCard(
                product = rightProduct,
                onAddToCart = onAddToCart,
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
    onAddToCart: (Product) -> Unit = {},
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
            // Product Image with discount badge
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
                
                // Discount badge
                product.discountPercentage?.let { discount ->
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-4).dp)
                            .background(
                                color = Color(0xFFE91E63),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${discount}%\nOFF",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 10.sp
                        )
                    }
                }
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
                // Price with crossed out original price if discount exists
                Column {
                    Text(
                        text = product.price,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    product.originalPrice?.let { originalPrice ->
                        Text(
                            text = originalPrice,
                            fontSize = 10.sp,
                            color = Color.Gray,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }
                
                Button(
                    onClick = { onAddToCart(product) },
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

@Composable
fun FrequentlyBoughtTogetherSection(
    products: List<FrequentlyBoughtProduct>,
    onDismiss: () -> Unit,
    onAddToCart: (FrequentlyBoughtProduct) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp,
        backgroundColor = Color.White
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with title and close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Frequently bought together",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Horizontal scrolling list of products
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(products) { product ->
                    FrequentlyBoughtProductCard(
                        product = product,
                        onAddToCart = { onAddToCart(product) }
                    )
                }
            }
        }
    }
}

@Composable
fun FrequentlyBoughtProductCard(
    product: FrequentlyBoughtProduct,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(120.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 2.dp,
        backgroundColor = Color.White
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Product Image with discount badge
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = product.name,
                    modifier = Modifier.size(45.dp),
                    contentScale = ContentScale.Fit
                )
                
                // Discount badge
                product.discountPercentage?.let { discount ->
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 3.dp, y = (-3).dp)
                            .background(
                                color = Color(0xFFE91E63),
                                shape = RoundedCornerShape(3.dp)
                            )
                            .padding(horizontal = 3.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "${discount}%\nOFF",
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 8.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(6.dp))
            
            // Product Name
            Text(
                text = product.name,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                maxLines = 2,
                lineHeight = 12.sp
            )
            
            // Product Weight
            Text(
                text = product.weight,
                fontSize = 8.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            // Price
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = product.price,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                product.originalPrice?.let { originalPrice ->
                    Text(
                        text = originalPrice,
                        fontSize = 9.sp,
                        color = Color.Gray,
                        textDecoration = TextDecoration.LineThrough
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(6.dp))
            
            // Add Button
            Button(
                onClick = onAddToCart,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Green400
                ),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Add",
                    fontSize = 9.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Function to process list items for display (pairing products and inserting banners)
private fun processItemsForDisplay(listItems: List<ListItem>): List<DisplayItem> {
    val displayItems = mutableListOf<DisplayItem>()
    val products = mutableListOf<Product>()
    val banners = mutableListOf<IndexedValue<Banner>>()
    
    // First, separate products and banners with their intended positions
    listItems.forEachIndexed { index, item ->
        when (item) {
            is ListItem.ProductItem -> products.add(item.product)
            is ListItem.BannerItem -> banners.add(IndexedValue(index, item.banner))
        }
    }
    
    // Create product rows (2 products per row)
    val productRows = mutableListOf<DisplayItem.ProductRowItem>()
    for (i in products.indices step 2) {
        val leftProduct = products[i]
        val rightProduct = if (i + 1 < products.size) products[i + 1] else null
        productRows.add(DisplayItem.ProductRowItem(leftProduct, rightProduct))
    }
    
    // Insert banners at appropriate positions
    // Each banner should appear after every 4 product rows (8 products)
    var bannerIndex = 0
    for (i in productRows.indices) {
        displayItems.add(productRows[i])
        
        // Insert banner after every 4 rows (8 products) if we have banners available
        if ((i + 1) % 4 == 0 && bannerIndex < banners.size) {
            displayItems.add(DisplayItem.BannerDisplayItem(banners[bannerIndex].value))
            bannerIndex++
        }
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
    
    // Add all products first
    products.forEach { product ->
        items.add(ListItem.ProductItem(product))
    }
    
    // Add banners (they will be positioned automatically by processItemsForDisplay)
    banners.forEach { banner ->
        items.add(ListItem.BannerItem(banner))
    }
    
    return items
}

// Function to simulate API call for frequently bought together products
private fun getFrequentlyBoughtTogether(productId: Int): List<FrequentlyBoughtProduct> {
    // Simulate different frequently bought products based on the added product
    return when (productId % 5) {
        0 -> listOf(
            FrequentlyBoughtProduct(101, "Brinjal", "500 g", "₹66"),
            FrequentlyBoughtProduct(102, "Capsicum Green", "500 g", "₹66"),
            FrequentlyBoughtProduct(103, "Tomato", "500 g", "₹66", "₹69", 4),
            FrequentlyBoughtProduct(104, "Onion Red", "1 Kg", "₹45")
        )
        1 -> listOf(
            FrequentlyBoughtProduct(105, "Kellogg's Original Special Breakfast Cereals Box", "750 g", "₹315"),
            FrequentlyBoughtProduct(106, "Amul Garlic & Herbs Buttery Spread Carton", "100 g", "₹80"),
            FrequentlyBoughtProduct(107, "Fresh Milk", "1 L", "₹60"),
            FrequentlyBoughtProduct(108, "Organic Honey", "250 g", "₹180")
        )
        2 -> listOf(
            FrequentlyBoughtProduct(109, "Basmati Rice", "1 Kg", "₹120"),
            FrequentlyBoughtProduct(110, "Cooking Oil", "1 L", "₹150"),
            FrequentlyBoughtProduct(111, "Turmeric Powder", "100 g", "₹45"),
            FrequentlyBoughtProduct(112, "Red Chili", "200 g", "₹85", "₹95", 10)
        )
        3 -> listOf(
            FrequentlyBoughtProduct(113, "Greek Yogurt", "400 g", "₹95"),
            FrequentlyBoughtProduct(114, "Whole Wheat Bread", "400 g", "₹35"),
            FrequentlyBoughtProduct(115, "Organic Eggs", "12 Pcs", "₹84", "₹90", 7),
            FrequentlyBoughtProduct(116, "Fresh Spinach", "250 g", "₹25")
        )
        else -> listOf(
            FrequentlyBoughtProduct(117, "Coconut Oil", "500 ml", "₹180"),
            FrequentlyBoughtProduct(118, "Almonds", "200 g", "₹220"),
            FrequentlyBoughtProduct(119, "Green Tea", "100 g", "₹125", "₹140", 12),
            FrequentlyBoughtProduct(120, "Dark Chocolate", "100 g", "₹150")
        )
    }
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

@Preview(showBackground = true)
@Composable
fun FrequentlyBoughtTogetherPreview() {
    ComposeTutorialsTheme {
        FrequentlyBoughtTogetherSection(
            products = listOf(
                FrequentlyBoughtProduct(101, "Brinjal", "500 g", "₹66"),
                FrequentlyBoughtProduct(102, "Capsicum Green", "500 g", "₹66"),
                FrequentlyBoughtProduct(103, "Tomato", "500 g", "₹66", "₹69", 4),
                FrequentlyBoughtProduct(104, "Onion Red", "1 Kg", "₹45")
            ),
            onDismiss = { },
            onAddToCart = { }
        )
    }
}