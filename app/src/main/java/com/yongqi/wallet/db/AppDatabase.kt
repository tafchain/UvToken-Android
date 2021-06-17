package com.yongqi.wallet.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.yongqi.wallet.bean.*
import com.yongqi.wallet.db.address.AddressDao
import com.yongqi.wallet.db.coin.CoinDao
import com.yongqi.wallet.db.record.RecordDao
import com.yongqi.wallet.db.trade.TradeDao
import com.yongqi.wallet.db.wallet.WalletDao

/**
 * author ：SunXiao
 * date : 2021/1/18 21:41
 * package：com.yongqi.wallet.db
 * description :
 */
@Database(
    entities = [Wallet::class, Coin::class, Address::class, Record::class, Trade::class],
    version = 5,
    exportSchema = false
) //, Address::class
abstract class AppDatabase : RoomDatabase() {

    abstract fun walletDao(): WalletDao
    abstract fun coinDao(): CoinDao
    abstract fun addressDao(): AddressDao
    abstract fun recordDao(): RecordDao
    abstract fun tradeDao(): TradeDao

    companion object {
        private var instance: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wallet.db" //数据库名称
                )
                    //可以强制在主线程运行数据库操作
                    .allowMainThreadQueries()
                    //强制升级
//                    .fallbackToDestructiveMigration()
                    //正常的应该用这个
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4,MIGRATION_4_5)
                    .build()
            }
            return instance as AppDatabase
        }

        /**
         * 数据库升级
         */
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {

            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("alter table record add column miner_fee TEXT not null default '0'")

            }
        }

        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("alter table record add column memo TEXT ")
                database.execSQL("alter table record add column startTime integer not null default 0")
                database.execSQL("alter table record add column valid TEXT")
            }
        }
        val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("alter table Coin add column coin integer not null default 0x80000000")
                database.execSQL("alter table Coin add column account integer not null default 0x80000000")
                database.execSQL("alter table Coin add column change integer not null default 0")
                database.execSQL("alter table Coin add column  'index' integer not null default 0")
                database.execSQL("alter table Coin add column  decimals integer  default 0")

                database.execSQL("create table trade_temp (uid integer primary key not null ,address text  default '',status text  default '',type text  default '',des text  default '')")
                database.execSQL("insert into trade_temp (uid,address,status)" + "select uid,address,status from Trade")
                database.execSQL("drop table Trade")
                database.execSQL("alter table trade_temp rename to Trade")

                database.execSQL("alter table record add column  confirmations TEXT  default '1'")

            }
        }

        private val MIGRATION_4_5: Migration = object : Migration(4, 5) {//TODO 新增

            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("alter table record add column gasPrice TEXT  default '-1'")
                database.execSQL("alter table record add column nonce TEXT")

            }
        }

    }


}