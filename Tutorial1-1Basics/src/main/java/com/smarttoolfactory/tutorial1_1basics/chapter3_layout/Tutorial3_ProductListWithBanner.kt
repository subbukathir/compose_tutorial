package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.ui.ComposeTutorialsTheme
import com.smarttoolfactory.tutorial1_1basics.ui.Green400
import com.smarttoolfactory.tutorial1_1basics.ui.Orange400

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
    val listItems = createSampleData()
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        val processedItems = processItemsForDisplay(listItems)
        
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
        Product(6, "Orange Nagpur", "1 Kg", "₹66")
    )
    
    val banner = Banner(
        id = 1,
        title = "Just Arrived!",
        subtitle = "Badami",
        buttonText = "Shop Now",
        backgroundColor = Orange400
    )
    
    return listOf(
        ListItem.ProductItem(products[0]),
        ListItem.ProductItem(products[1]),
        ListItem.ProductItem(products[2]),
        ListItem.ProductItem(products[3]),
        ListItem.BannerItem(banner),
        ListItem.ProductItem(products[4]),
        ListItem.ProductItem(products[5])
    )
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