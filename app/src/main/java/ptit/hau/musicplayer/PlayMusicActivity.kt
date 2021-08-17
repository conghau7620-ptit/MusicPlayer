package ptit.hau.musicplayer

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_play_music.*
import java.io.File
import java.util.ArrayList

class PlayMusicActivity : AppCompatActivity() {

    companion object{
        var mediaPlayer : MediaPlayer? = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        if (barVisual != null) {
            barVisual.release()
        }
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_music)

        if (mediaPlayer!=null){
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
        }
        
        var pos = intent.getIntExtra("pos",0)
        var mySongs = intent.getSerializableExtra("songs") as ArrayList<File>
        var uri = Uri.parse(mySongs[pos].toString())

        tvSongName.text = mySongs[pos].name.replace(".mp3","")
            .replace(".wav","")
        tvSongName.isSelected = true

        mediaPlayer = MediaPlayer.create(this, uri)
        mediaPlayer!!.start()

        var audioSessionId = mediaPlayer!!.audioSessionId
        if (audioSessionId!=-1) {
            barVisual.setAudioSessionId(audioSessionId)
        }

        btnPlay.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                if (mediaPlayer!!.isPlaying){
                    btnPlay.setImageResource(R.drawable.ic_play_foreground)
                    mediaPlayer!!.pause()
                }
                else {
                    btnPlay.setImageResource(R.drawable.ic_pause_foreground)
                    mediaPlayer!!.start()
                }
            }
        })

        btnNext.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                btnPlay.setImageResource(R.drawable.ic_pause_foreground)
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
                pos = if (pos+1==mySongs.size) 0 else pos+1
                tvSongName.text = mySongs[pos].name.replace(".mp3","")
                    .replace(".wav","")
                uri = Uri.parse(mySongs[pos].toString())
                mediaPlayer = MediaPlayer.create(this@PlayMusicActivity, uri)
                mediaPlayer!!.start()
                audioSessionId = mediaPlayer!!.audioSessionId
                if (audioSessionId!=-1) {
                    barVisual.setAudioSessionId(audioSessionId)
                }
            }
        })

        btnPrevious.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                btnPlay.setImageResource(R.drawable.ic_pause_foreground)
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
                pos = if (pos-1<0) mySongs.size-1 else pos-1
                tvSongName.text = mySongs[pos].name.replace(".mp3","")
                    .replace(".wav","")
                uri = Uri.parse(mySongs[pos].toString())
                mediaPlayer = MediaPlayer.create(this@PlayMusicActivity, uri)
                mediaPlayer!!.start()
                audioSessionId = mediaPlayer!!.audioSessionId
                if (audioSessionId!=-1) {
                    barVisual.setAudioSessionId(audioSessionId)
                }
            }
        })

        mediaPlayer!!.setOnCompletionListener(object: MediaPlayer.OnCompletionListener{
            override fun onCompletion(p0: MediaPlayer?) {
                btnNext.performClick()
            }
        })

        tvTimeEnd.text = createTime(mediaPlayer!!.duration)
        var updateSeekbar = object: Thread() {
            override fun run() {
                super.run()
                val totalDuration = mediaPlayer!!.duration
                var currentPos = 0

                while (currentPos<totalDuration){
                    try{
                        sleep(500)
                        currentPos = mediaPlayer!!.currentPosition
                        seekBar.setProgress(currentPos)
                        tvTimeStart.text = createTime(currentPos)
                    }
                    catch (e: Exception){
                        e.printStackTrace()
                    }
                }
            }
        }
        seekBar.max = mediaPlayer!!.duration
        updateSeekbar.start()

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mediaPlayer!!.seekTo(seekBar!!.progress)
            }

        })


    }

    fun createTime(miliSecond: Int) : String{
        var min = "" + miliSecond/1000/60
        var second = miliSecond/1000%60
        var time = min + ":" + (if (second<10) "0" + second else second)
        return time
    }


}