package com.bs.util

import androidx.room.*

@Entity
data class User(
        @PrimaryKey val userid: Int,
        val username: String
        )

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE userid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: List<User>)
}

@Database(entities = arrayOf(User::class), version = 1)
abstract class database : RoomDatabase() {
    abstract fun userDao(): UserDao
}