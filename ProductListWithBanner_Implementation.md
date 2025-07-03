# Product List with Banner - Jetpack Compose Implementation

## Overview
This implementation creates a Compose UI that replicates the Figma design showing a grocery/food app interface with products listed in a grid layout and promotional banners inserted between product rows.

## File Location
`Tutorial1-1Basics/src/main/java/com/smarttoolfactory/tutorial1_1basics/chapter3_layout/Tutorial3_ProductListWithBanner.kt`

## Key Features

### 🎯 Design Elements
- **Top App Bar**: Back button, "Exotics" title, and search icon
- **Product Grid**: Products displayed in 2-column grid layout
- **Product Cards**: Each showing image, name, weight, price, and "Add" button
- **Banner Cards**: Promotional banners with title, subtitle, CTA button, and image
- **Responsive Layout**: Handles odd numbers of products gracefully

### 🏗️ Architecture

#### Data Models
```kotlin
data class Product(
    val id: Int,
    val name: String,
    val weight: String,
    val price: String,
    val imageRes: Int
)

data class Banner(
    val id: Int,
    val title: String,
    val subtitle: String,
    val buttonText: String,
    val backgroundColor: Color,
    val imageRes: Int
)
```

#### List Item Types
```kotlin
sealed class ListItem {
    data class ProductItem(val product: Product) : ListItem()
    data class BannerItem(val banner: Banner) : ListItem()
}

sealed class DisplayItem {
    data class ProductRowItem(val leftProduct: Product, val rightProduct: Product?) : DisplayItem()
    data class BannerDisplayItem(val banner: Banner) : DisplayItem()
}
```

### 🎨 UI Components

#### 1. ProductListWithBannerScreen()
- Main screen with Scaffold and TopAppBar
- Matches the Figma design with proper colors and layout

#### 2. ProductListContent()
- LazyColumn implementation for efficient scrolling
- Processes list items to create proper product pairs and banner placement

#### 3. ProductCard()
- Individual product display component
- Features:
  - Image placeholder with rounded corners
  - Product name and weight
  - Price and "Add" button in bottom row
  - Card elevation and rounded corners

#### 4. BannerCard()
- Promotional banner component
- Features:
  - Orange gradient background
  - Title and subtitle text
  - "Shop Now" button
  - Image on the right side

#### 5. ProductRow()
- Handles displaying 1-2 products side by side
- Manages spacing and alignment

### 🔄 Smart List Processing

The `processItemsForDisplay()` function intelligently handles:
- **Product Pairing**: Groups products into rows of 2
- **Banner Insertion**: Properly inserts banners while maintaining product pairing
- **Odd Product Handling**: Creates single-product rows when needed
- **Layout Optimization**: Ensures banners don't break product flow

```kotlin
// Example flow:
// Input: [Product1, Product2, Product3, Banner, Product4, Product5]
// Output: [Row(Product1, Product2), Row(Product3, null), Banner, Row(Product4, Product5)]
```

### 🎭 Sample Data
The implementation includes sample data matching the Figma design:
- 6 sample products (vegetables, fruits, etc.)
- 1 promotional banner ("Just Arrived! Badami")
- Proper Indian Rupee pricing (₹66 format)

### 🎨 Styling
- Uses existing project theme (`ComposeTutorialsTheme`)
- Colors: Green for "Add" buttons, Orange for banners
- Typography: Various font sizes for hierarchy
- Spacing: Consistent 16dp padding and 12dp gaps

### 📱 Preview Functions
Includes three preview functions:
1. `ProductListWithBannerPreview()` - Full screen preview
2. `ProductCardPreview()` - Individual product card
3. `BannerCardPreview()` - Individual banner card

## Usage

To use this implementation:

1. **Import the file** into your Compose project
2. **Add image resources** for products and banners
3. **Call the main composable**:
   ```kotlin
   ProductListWithBannerScreen()
   ```

## Customization

### Adding New Products
```kotlin
Product(
    id = 7,
    name = "New Product",
    weight = "2 Kg",
    price = "₹99",
    imageRes = R.drawable.new_product_image
)
```

### Adding New Banners
```kotlin
Banner(
    id = 2,
    title = "Special Offer!",
    subtitle = "Fresh Mangoes",
    buttonText = "Order Now",
    backgroundColor = Red400,
    imageRes = R.drawable.mango_banner
)
```

### Modifying Layout
- Adjust spacing in `ProductListContent()` with `verticalArrangement`
- Change card styling in `ProductCard()` and `BannerCard()`
- Modify colors using the existing theme system

## Key Benefits

1. **Efficient Rendering**: Uses LazyColumn for performance
2. **Flexible Layout**: Easy to add/remove products and banners
3. **Responsive Design**: Handles various screen sizes
4. **Type Safety**: Uses sealed classes for type-safe list items
5. **Material Design**: Follows Material Design principles
6. **Preview Support**: Multiple preview functions for development

This implementation provides a solid foundation for a grocery shopping app UI that matches the provided Figma design while being extensible and maintainable.