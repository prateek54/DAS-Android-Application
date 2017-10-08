package com.downloadanysong.dev.prateek.musicplayerdas.Adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.downloadanysong.dev.prateek.musicplayerdas.MainActivity;
import com.downloadanysong.dev.prateek.musicplayerdas.Models.Favourite;
import com.downloadanysong.dev.prateek.musicplayerdas.Models.ImageBitmap;
import com.downloadanysong.dev.prateek.musicplayerdas.Models.SongInfo;
import com.downloadanysong.dev.prateek.musicplayerdas.NavBar.PlayerActivity;
import com.downloadanysong.dev.prateek.musicplayerdas.R;
import com.downloadanysong.dev.prateek.musicplayerdas.Sqlite.FavDatabaseHandler;
import com.downloadanysong.dev.prateek.musicplayerdas.utils.ImageCache;
import com.downloadanysong.dev.prateek.musicplayerdas.utils.UtilFunctions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

/**
 * Created by prateek on 09-06-2017.
 */
public class SongAdapter  extends RecyclerView.Adapter<SongAdapter.MyViewHolder> {
    private ArrayList<SongInfo> listsong;
    private FragmentActivity fragment;
    private PlayerActivity playerActivity;
    private LayoutInflater inflater;
    private String title_fav;
    private String song_url_fav;
    private String artist_fav;
    private long album_id;
     private SongInfo song;
    private String title_song, artist_song, url_song;


    int song_position;
    FavDatabaseHandler db ;
    ImageBitmap imageBitmap;
    ArrayList<ImageBitmap> imageBitmapArrayList= new ArrayList<ImageBitmap>();

    private Button play;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView artist;
        //private TextView download;
        //private TextView audio;
        public TextView title;
        public ImageView overflow;
        public ProgressBar progressBar;
        public View vw;               // <----- here



        public MyViewHolder(View view) {
            super(view);
            this.vw = view;            // <----- here
            artist = (TextView) view.findViewById(R.id.artist_name_tv);
            title = (TextView) view.findViewById(R.id.song_name_tv);
            overflow = (ImageView) view.findViewById(R.id.ovflow);
            db=new FavDatabaseHandler(fragment);


        }

    }
    public SongAdapter(FragmentActivity fragment, ArrayList<SongInfo> listsong,PlayerActivity playerActivity) {
        this.listsong = listsong;
        this.fragment = fragment;
        this.playerActivity=playerActivity;
    }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_row, null);
            return new MyViewHolder(itemView);
        }


    @Override
    public void onBindViewHolder(final SongAdapter.MyViewHolder holder, final int position) {

        song = listsong.get(position);
        title_song = song.getSongname();
        artist_song = song.getArtistname();
        url_song = song.getSongUrl();
        album_id = song.getId();
        final boolean filter = song.getFilter();

        holder.title.setText(title_song);
        Log.d("SA", "ARTIST NAME" + artist_song);
        if (!artist_song.equalsIgnoreCase("<unknown>")) {
            holder.artist.setText(artist_song);
        } else {
            holder.artist.setText("");

        }
        //BEST AND WORKING METHOD

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showPopupMenu(holder.overflow, position, title_song, artist_song);
            }
        });
        holder.vw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filter != true) {
                    playerActivity.func_pos(position);
                } else {
                    playerActivity.playSongurl(url_song, title_song);


                }

            }
        });
    }


    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view, int position, String songname, String artistname) {

        //Get fav item title url ...etc

        title_fav=songname;
        artist_fav=artistname;
        //song_url_fav=urlsong;
        song_position=position;



        // inflate menu
        PopupMenu popup = new PopupMenu(fragment, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_song, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();

    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    /**
                     * CRUD Operations
                     * */
                    // Inserting Favourite()s
                    Favourite fav_search;
                    fav_search = db.searchfav(song_url_fav);
                    if (fav_search!=null){
                     //   Log.d("FAV ", "ALREADY IN FAVOURITE SONG NAME::"+fav_search.gettitle()+"::ARTISTNAME"+artist_fav+"::SONGURL"+song_url_fav);
                       /* List<Favourite> lf = db.getAllFavourites();
                        for (int i = 0; i <lf.size() ; i++) {
                            Log.d("FAV ", "ALL ALREADY IN FAVOURITE SONG LIST::"+lf.get(i).gettitle());

                        }*/
                    }
                    else {
                        //Log.d("FAV ", "Inserting ..SONG NAME::"+title_fav+"::ARTISTNAME"+artist_fav+"::SONGURL"+song_url_fav);

                        Favourite fv = new Favourite(title_fav,song_url_fav,artist_fav);

                        db.addFavourite(fv);
                        Toast.makeText(fragment, "Add to favourite", Toast.LENGTH_SHORT).show();

                }
                    return true;
                case R.id.action_play_next:
                    Toast.makeText(fragment, "Play next", Toast.LENGTH_SHORT).show();
                    playerActivity.func_play_next(song_position);

                    return true;
                case R.id.action_add_playlist:
                    Toast.makeText(fragment, "PLAYLIST", Toast.LENGTH_SHORT).show();
                    //playerActivity.func_play_next(song_position);

                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return listsong.size();
    }


}















/*//ORIGNALS
public class SongAdapter extends BaseAdapter {
    ArrayList<SongInfo> listsong;
    FragmentActivity fragment;
    PlayerActivity playerActivity;
    private LayoutInflater inflater;
    TextView artist;
    TextView download;
    TextView audio;
    TextView title;





    public SongAdapter(FragmentActivity fragment, ArrayList<SongInfo> listsong,PlayerActivity playerActivity) {
        this.listsong = listsong;
        this.fragment = fragment;
        this.playerActivity=playerActivity;
    }


    @Override
    public int getCount() {
        return listsong.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //final View view = activity.getLayoutInflater().inflate(R.layout.list_row, null);

        if (inflater == null) {
            inflater = (LayoutInflater) fragment.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null)
            convertView = inflater.inflate(R.layout.row_song, null);
        final Button play = (Button) convertView.findViewById(R.id.btn_action);
        artist = (TextView) convertView.findViewById(R.id.artist_name_tv);
        title = (TextView) convertView.findViewById(R.id.song_name_tv);


        SongInfo song  = listsong.get(position);
        //name
        title.setText(song.getSongname());
        //email

        artist.setText(song.getArtistname());
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //THIS IS MY ADAPTER WHERE I GET POSITION FOR SELECTED SONG INMY LIST IN POSITION VARIABLE
                Toast.makeText(fragment, "BUTTON CLICKED FOR POS("+position+")", Toast.LENGTH_SHORT).show();
                playerActivity.func_pos(position);


            }
        });

        return convertView;
    }

}*/
