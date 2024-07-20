package org.bohdan.storetest

data class Product (
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val stock: Int = 0,
    val imageUrl: String = "",
    val category: String = "",
    val discount: Boolean = false,
)