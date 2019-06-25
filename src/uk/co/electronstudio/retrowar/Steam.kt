package uk.co.electronstudio.retrowar

import com.codedisaster.steamworks.SteamAPI
import com.codedisaster.steamworks.SteamAuth
import com.codedisaster.steamworks.SteamID
import com.codedisaster.steamworks.SteamLeaderboardEntriesHandle
import com.codedisaster.steamworks.SteamLeaderboardHandle
import com.codedisaster.steamworks.SteamResult
import com.codedisaster.steamworks.SteamUser
import com.codedisaster.steamworks.SteamUserCallback
import com.codedisaster.steamworks.SteamUserStats
import com.codedisaster.steamworks.SteamUserStatsCallback
import com.codedisaster.steamworks.SteamUtils
import com.codedisaster.steamworks.SteamUtilsCallback
import java.lang.Exception

class Steam() {
    fun shutDown() {
        log("shutdown steam")
        SteamAPI.shutdown()
    }

    var gamesPlayed: Int = 0
    val stats: SteamUserStats
    val user: SteamUser
    val utils: SteamUtils
    val appId: Long

    init {
        SteamAPI.loadLibraries()

        if (!SteamAPI.init()) {
            log("steam error")
            SteamAPI.printDebugInfo(System.err)
            throw Exception("couldnt initialize steam")
        }


        user = SteamUser(MySteamUserCallback())
        log("steam user id ${user?.steamID}")

        utils = SteamUtils(MySteamUtilsCallBack())
        appId = utils.appID.toLong()



        stats = SteamUserStats(MySteamUserStatsCallBack(this))
        stats.requestCurrentStats()






        SteamAPI.printDebugInfo(System.out)

    }

    fun setAchievementLOADED(){
        stats.setAchievement("LOADED")
        stats.storeStats()
    }

    fun incGamesPlayed(){
        gamesPlayed++
        stats.setStatI("gamesPlayed", gamesPlayed)
        stats.storeStats()
        log("Steam","gamesPlayed: $gamesPlayed")
    }

    fun resetAllStats(){
        stats.resetAllStats(true)
        gamesPlayed=0
    }

    fun runCallBacks() {
        SteamAPI.runCallbacks()
    }
}

class MySteamUtilsCallBack : SteamUtilsCallback {
    override fun onSteamShutdown() {
        log("steam shutdown")
    }

}


class MySteamUserStatsCallBack(private val steam: Steam) : SteamUserStatsCallback {
    override fun onUserStatsReceived(gameId: Long, steamIDUser: SteamID?, result: SteamResult?) {
        log("SteamUserStats", "onUserStatsReceived $steamIDUser $result")
        if(gameId != steam.appId){
            log(("wrong steam appid"))
            return
        }
        if(result != SteamResult.OK){
            log(("steam result not OK"))
            return
        }
        steam.gamesPlayed = steam.stats.getStatI("gamesPlayed",0)
    }

    override fun onGlobalStatsReceived(gameId: Long, result: SteamResult?) {
        log("SteamUserStats", "onGlobalStatsReceived  $result")
    }

    override fun onUserAchievementStored(gameId: Long, isGroupAchievement: Boolean, achievementName: String?,
                                         curProgress: Int, maxProgress: Int) {
        log("SteamUsersStats","achievementstored $achievementName $curProgress $maxProgress")

    }

    override fun onLeaderboardScoreUploaded(success: Boolean, leaderboard: SteamLeaderboardHandle?, score: Int,
                                            scoreChanged: Boolean, globalRankNew: Int, globalRankPrevious: Int) {
    }

    override fun onLeaderboardScoresDownloaded(leaderboard: SteamLeaderboardHandle?,
                                               entries: SteamLeaderboardEntriesHandle?, numEntries: Int) {

    }

    override fun onLeaderboardFindResult(leaderboard: SteamLeaderboardHandle?, found: Boolean) {

    }

    override fun onUserStatsUnloaded(steamIDUser: SteamID?) {
        log("SteamUserStats", "onGlobalStatsUnloaded")
    }

    override fun onUserStatsStored(gameId: Long, result: SteamResult?) {
        log("SteamUserStats", "onUserStatsStored  $result")
    }
}


class MySteamUserCallback : SteamUserCallback {
    override fun onEncryptedAppTicket(result: SteamResult?) {

    }

    override fun onValidateAuthTicket(steamID: SteamID?, authSessionResponse: SteamAuth.AuthSessionResponse?,
                                      ownerSteamID: SteamID?) {

    }

    override fun onMicroTxnAuthorization(appID: Int, orderID: Long, authorized: Boolean) {

    }

}
