package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface JobPostingDao {
    @Query("SELECT * FROM job_postings ORDER BY postedTimestamp DESC")
    fun getAllJobPostings(): Flow<List<JobPostingEntity>>

    @Query("SELECT * FROM job_postings WHERE status = :status ORDER BY postedTimestamp DESC")
    fun getJobPostingsByStatus(status: String): Flow<List<JobPostingEntity>>

    @Query("SELECT * FROM job_postings WHERE source = :source ORDER BY postedTimestamp DESC")
    fun getJobPostingsBySource(source: String): Flow<List<JobPostingEntity>>

    @Query("SELECT * FROM job_postings WHERE appliedTimestamp IS NOT NULL ORDER BY appliedTimestamp DESC")
    fun getAppliedJobPostings(): Flow<List<JobPostingEntity>>

    @Query("SELECT * FROM job_postings WHERE id = :id LIMIT 1")
    suspend fun getJobPostingById(id: Long): JobPostingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobPosting(jobPosting: JobPostingEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobPostings(jobPostings: List<JobPostingEntity>)

    @Update
    suspend fun updateJobPosting(jobPosting: JobPostingEntity)

    @Query("UPDATE job_postings SET status = :status, appliedTimestamp = :appliedTimestamp, lastUpdatedTimestamp = :lastUpdatedTimestamp WHERE id = :id")
    suspend fun updateStatusAndTimestamp(
        id: Long,
        status: String,
        appliedTimestamp: Long?,
        lastUpdatedTimestamp: Long = System.currentTimeMillis()
    )

    @Query("DELETE FROM job_postings WHERE id = :id")
    suspend fun deleteJobPostingById(id: Long)

    @Delete
    suspend fun deleteJobPosting(jobPosting: JobPostingEntity)

    @Query("SELECT COUNT(*) FROM job_postings")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM job_postings WHERE status = :status")
    fun getCountByStatus(status: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM job_postings WHERE source = :source")
    fun getCountBySource(source: String): Flow<Int>
}

@Dao
interface JobPlatformDao {
    @Query("SELECT * FROM job_platforms ORDER BY name ASC")
    fun getAllPlatforms(): Flow<List<JobPlatformEntity>>

    @Query("SELECT * FROM job_platforms WHERE isConnected = 1")
    fun getConnectedPlatforms(): Flow<List<JobPlatformEntity>>

    @Query("SELECT * FROM job_platforms WHERE id = :id LIMIT 1")
    suspend fun getPlatformById(id: String): JobPlatformEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePlatforms(platforms: List<JobPlatformEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(platform: JobPlatformEntity)

    @Update
    suspend fun updatePlatform(platform: JobPlatformEntity)

    @Query("UPDATE job_platforms SET isConnected = :connected, accountEmail = :email, lastSyncedTime = :time, statusMessage = :status WHERE id = :id")
    suspend fun updateConnectionStatus(id: String, connected: Boolean, email: String, time: Long, status: String)

    @Query("UPDATE job_platforms SET applicationsToday = applicationsToday + 1 WHERE id = :id")
    suspend fun incrementApplicationsToday(id: String)
}

@Dao
interface JobApplicationDao {
    @Query("SELECT * FROM job_applications ORDER BY appliedDate DESC")
    fun getAllApplications(): Flow<List<JobApplicationEntity>>

    @Query("SELECT * FROM job_applications WHERE status = :status ORDER BY appliedDate DESC")
    fun getApplicationsByStatus(status: String): Flow<List<JobApplicationEntity>>

    @Query("SELECT * FROM job_applications WHERE id = :id LIMIT 1")
    suspend fun getApplicationById(id: Long): JobApplicationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(application: JobApplicationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplications(applications: List<JobApplicationEntity>)

    @Update
    suspend fun updateApplication(application: JobApplicationEntity)

    @Query("UPDATE job_applications SET status = :newStatus, lastUpdatedDate = :updatedDate WHERE id = :id")
    suspend fun updateStatus(id: Long, newStatus: String, updatedDate: Long = System.currentTimeMillis())

    @Query("UPDATE job_applications SET interviewDate = :interviewDate, notes = :notes, lastUpdatedDate = :updatedDate WHERE id = :id")
    suspend fun updateInterviewDetails(id: Long, interviewDate: Long?, notes: String, updatedDate: Long = System.currentTimeMillis())

    @Query("DELETE FROM job_applications WHERE id = :id")
    suspend fun deleteApplicationById(id: Long)

    @Query("SELECT COUNT(*) FROM job_applications")
    fun getTotalApplicationsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM job_applications WHERE status = :status")
    fun getCountByStatus(status: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM job_applications WHERE appliedDate >= :startOfDay")
    fun getApplicationsCountToday(startOfDay: Long): Flow<Int>
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileSync(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET resumeFileName = :fileName, resumeFileSize = :fileSize, resumeUploadedDate = :uploadDate, resumeRawText = :rawText, resumeAtsScore = :atsScore WHERE id = 1")
    suspend fun updateResumeInfo(fileName: String, fileSize: String, uploadDate: Long, rawText: String, atsScore: Int)

    @Query("UPDATE user_profile SET isLoggedIn = :isLoggedIn WHERE id = 1")
    suspend fun setLoginStatus(isLoggedIn: Boolean)
}

@Dao
interface UserAccountDao {
    @Query("SELECT * FROM user_accounts ORDER BY createdAt DESC")
    fun getAllAccounts(): Flow<List<UserAccountEntity>>

    @Query("SELECT * FROM user_accounts WHERE email = :email LIMIT 1")
    suspend fun getAccountByEmail(email: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts WHERE isCurrent = 1 LIMIT 1")
    suspend fun getCurrentAccount(): UserAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: UserAccountEntity)

    @Query("UPDATE user_accounts SET isCurrent = 0")
    suspend fun clearCurrentAccountFlag()

    @Query("UPDATE user_accounts SET isCurrent = 1 WHERE email = :email")
    suspend fun setCurrentAccountFlag(email: String)

    @Query("DELETE FROM user_accounts WHERE email = :email")
    suspend fun deleteAccount(email: String)
}

@Dao
interface SentResumeEmailDao {
    @Query("SELECT * FROM sent_resume_emails ORDER BY timestamp DESC")
    fun getAllSentEmails(): Flow<List<SentResumeEmailEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSentEmail(emailEntity: SentResumeEmailEntity): Long

    @Query("DELETE FROM sent_resume_emails WHERE id = :id")
    suspend fun deleteSentEmail(id: Long)

    @Query("DELETE FROM sent_resume_emails")
    suspend fun clearSentEmails()
}

@Dao
interface AutomationLogDao {
    @Query("SELECT * FROM automation_logs ORDER BY timestamp DESC LIMIT 100")
    fun getRecentLogs(): Flow<List<AutomationLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AutomationLogEntity)

    @Query("DELETE FROM automation_logs")
    suspend fun clearLogs()
}

@Dao
interface AutoApplyConfigDao {
    @Query("SELECT * FROM auto_apply_config WHERE id = 1 LIMIT 1")
    fun getConfig(): Flow<AutoApplyConfigEntity?>

    @Query("SELECT * FROM auto_apply_config WHERE id = 1 LIMIT 1")
    suspend fun getConfigSync(): AutoApplyConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateConfig(config: AutoApplyConfigEntity)

    @Query("UPDATE auto_apply_config SET isAutoApplyActive = :isActive WHERE id = 1")
    suspend fun toggleAutoApply(isActive: Boolean)
}

@Dao
interface NotificationReminderConfigDao {
    @Query("SELECT * FROM notification_reminder_config WHERE id = 1 LIMIT 1")
    fun getConfig(): Flow<NotificationReminderConfigEntity?>

    @Query("SELECT * FROM notification_reminder_config WHERE id = 1 LIMIT 1")
    suspend fun getConfigSync(): NotificationReminderConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateConfig(config: NotificationReminderConfigEntity)

    @Query("UPDATE notification_reminder_config SET isEnabled = :isEnabled WHERE id = 1")
    suspend fun setEnabled(isEnabled: Boolean)

    @Query("UPDATE notification_reminder_config SET reminderHour = :hour, reminderMinute = :minute WHERE id = 1")
    suspend fun updateTime(hour: Int, minute: Int)

    @Query("UPDATE notification_reminder_config SET includeApplicationStats = :includeStats, notifyOnInterviewAlerts = :notifyInterviews, notifyOnJobMatches = :notifyJobMatches WHERE id = 1")
    suspend fun updatePreferences(includeStats: Boolean, notifyInterviews: Boolean, notifyJobMatches: Boolean)

    @Query("UPDATE notification_reminder_config SET lastNotifiedTimestamp = :timestamp, lastNotifiedMessage = :message WHERE id = 1")
    suspend fun updateLastNotified(timestamp: Long, message: String)
}

