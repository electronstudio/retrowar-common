package uk.co.electronstudio.retrowar

import com.codedisaster.steamworks.SteamID
import com.codedisaster.steamworks.SteamLeaderboardEntriesHandle
import com.codedisaster.steamworks.SteamLeaderboardHandle
import com.codedisaster.steamworks.SteamResult
import com.codedisaster.steamworks.SteamUserStatsCallback

class SteamStatsCallBack:SteamUserStatsCallback {
    override fun onUserStatsReceived(gameId: Long, steamIDUser: SteamID?, result: SteamResult?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onGlobalStatsReceived(gameId: Long, result: SteamResult?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onUserAchievementStored(gameId: Long, isGroupAchievement: Boolean, achievementName: String?,
                                         curProgress: Int, maxProgress: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLeaderboardScoreUploaded(success: Boolean, leaderboard: SteamLeaderboardHandle?, score: Int,
                                            scoreChanged: Boolean, globalRankNew: Int, globalRankPrevious: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLeaderboardScoresDownloaded(leaderboard: SteamLeaderboardHandle?,
                                               entries: SteamLeaderboardEntriesHandle?, numEntries: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLeaderboardFindResult(leaderboard: SteamLeaderboardHandle?, found: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onUserStatsUnloaded(steamIDUser: SteamID?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onUserStatsStored(gameId: Long, result: SteamResult?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}