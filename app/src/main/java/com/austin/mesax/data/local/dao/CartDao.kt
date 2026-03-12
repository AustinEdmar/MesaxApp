package com.austin.mesax.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.austin.mesax.data.local.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Query("""
        SELECT * FROM cart_items
        WHERE orderId = :orderId
    """)
    fun getCart(orderId: Int?): Flow<List<CartItemEntity>>



    @Query("""
        SELECT * FROM cart_items
        WHERE orderId = :orderId AND productId = :productId
        LIMIT 1
    """)
    suspend fun getItem(
        orderId: Int,
        productId: Int
    ): CartItemEntity?


    @Insert
    suspend fun insert(item: CartItemEntity)

    //1️⃣ Query para decrementar quantidade

    @Query("""
    UPDATE cart_items
    SET quantity = quantity - 1,
        pendingSync = 1
    WHERE id = :itemId
""")
    suspend fun decrementQuantity(itemId: Int)

//2️⃣ Query para remover quando chegar a 0
//
//Você também precisa remover o item quando a quantidade for zero
    @Query("""
    DELETE FROM cart_items
    WHERE id = :itemId
""")
    suspend fun deleteItem(itemId: Int)


    @Update
    suspend fun update(item: CartItemEntity)



    @Query("""
        SELECT * FROM cart_items
        WHERE pendingSync = 1
    """)
    suspend fun getPendingItems(): List<CartItemEntity>


}