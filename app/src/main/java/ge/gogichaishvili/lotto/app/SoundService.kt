package ge.gogichaishvili.lotto.app

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import ge.gogichaishvili.lotto.R

class SoundService : Service() {
    var player: MediaPlayer? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        player = MediaPlayer.create(this, R.raw.casino)
        player!!.isLooping = true //set looping
        player!!.setVolume(0.1f, 0.1f)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        player!!.start()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        player!!.stop()
        player!!.release()
        stopSelf()
        super.onDestroy()
    }
}