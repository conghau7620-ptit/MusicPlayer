package ptit.hau.musicplayer

import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.ArrayList
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    var mySongs = arrayListOf<File>()

    var items = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        runtimePermission()

        listView.setOnItemClickListener { adapterView, view, pos, l ->
            val intent = Intent(this, PlayMusicActivity::class.java).apply {
                putExtra("songs", mySongs)
                putExtra("pos", pos)
            }
            startActivity(intent)
        }
    }

    fun runtimePermission(){
        Dexter.withContext(this).withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.RECORD_AUDIO)
            .withListener(object : MultiplePermissionsListener{
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    displaySong()
                }

                override fun onPermissionRationaleShouldBeShown(
                    list: MutableList<PermissionRequest>?,
                    permissionToken: PermissionToken?
                ) {
                    permissionToken!!.continuePermissionRequest()
                }
            }).check()
    }

    fun findSong(file: File): ArrayList<File>{
        var arrayList = arrayListOf<File>()
        var files = file.listFiles()
        for (f in files){
            if (f.isDirectory && !f.isHidden){
                arrayList.addAll(findSong(f))
            }
            else {
                if (f.name.endsWith(".mp3") || f.name.endsWith(".wav")){
                    arrayList.add(f)
                }
            }
        }
        return arrayList
    }

    fun displaySong(){
        mySongs = findSong(Environment.getExternalStorageDirectory())

        mySongs.sortBy{ it.name }
        for (i in 0..mySongs.size-1){
            items.add(mySongs[i].name.toString().replace(".mp3","")
                .replace(".wav",""))

        }
        var listSongAdapter = ListSongAdapter(this, items)
        listView.adapter = listSongAdapter
    }


}