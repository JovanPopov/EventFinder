package ftn.eventfinder.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.activeandroid.query.Select;

import java.io.IOException;
import java.util.List;

import ftn.eventfinder.RetrofitInt.EventsInterface;
import ftn.eventfinder.entities.Tag_db;
import ftn.eventfinder.model.TagsFromServer;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jovan on 2.7.2016.
 */
public class UpVoteTag extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public UpVoteTag(String name) {
        super(name);
    }
    public UpVoteTag() {
        super("UpVoteTag");
    }
    @Override
    protected void onHandleIntent(Intent intent) {

        int id = (int)intent.getExtras().get("id");
        try {
            Log.i("tags","Usao u tags servis");
            upVote(id);

            Intent intent1 = new Intent("tagsResponse");
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent1);

        } catch (IOException e) {
            Log.e("SYNC", "SyncTask", e);
        }
    }

    private void upVote( int id) throws IOException{

        final String BASE_URL = "http://188.2.87.248:4000/rest/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        EventsInterface service = retrofit.create(EventsInterface.class);
        Call<TagsFromServer> call = service.upVoteTag(id);

        Tag_db tag= new Select().from(Tag_db.class).where("tagId = ?", id).executeSingle();
        tag.setVote(true);
        tag.save();

    }
}
