package ptit.hau.musicplayer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class ListSongAdapter(val context: Context, val items: MutableList<String>) : BaseAdapter() {

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(p0: Int): Any {
        return items[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertview: View?, parent: ViewGroup?): View {
        val view = convertview?: LayoutInflater.from(context).inflate(
            R.layout.list_item, parent, false
        )
        view.let{
            val tvSong : TextView = view.findViewById(R.id.tvSong)
            tvSong.text = items[position]
            tvSong.isSelected = true
        }

        return view
    }
}