package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        JobPostingEntity::class,
        JobPlatformEntity::class,
        JobApplicationEntity::class,
        UserProfileEntity::class,
        AutomationLogEntity::class,
        AutoApplyConfigEntity::class,
        UserAccountEntity::class,
        SentResumeEmailEntity::class,
        NotificationReminderConfigEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun jobPostingDao(): JobPostingDao
    abstract fun jobPlatformDao(): JobPlatformDao
    abstract fun jobApplicationDao(): JobApplicationDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun automationLogDao(): AutomationLogDao
    abstract fun autoApplyConfigDao(): AutoApplyConfigDao
    abstract fun userAccountDao(): UserAccountDao
    abstract fun sentResumeEmailDao(): SentResumeEmailDao
    abstract fun notificationReminderConfigDao(): NotificationReminderConfigDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "auto_job_apply.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
