package ftn.eventfinder.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.activeandroid.query.Select;

import java.io.IOException;
import java.util.List;

import ftn.eventfinder.RetrofitInt.EventsInterface;
import ftn.eventfinder.entities.Event_db;
import ftn.eventfinder.entities.Tag_db;
import ftn.eventfinder.model.TagsFromServer;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jovan on 2.7.2016.
 */
public class GetTagsService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GetTagsService(String name) {
        super(name);
    }
    public GetTagsService() {
        super("GetTagsService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {

        String value = (String ) intent.getExtras().get("value");
        String eventId = (String )intent.getExtras().get("eventId");
        try {
            Log.i("tags","Usao u tags servis");
            getSetTags(value, eventId);

            Intent intent1 = new Intent("tagsResponse");
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent1);

        } catch (IOException e) {
            Log.e("SYNC", "SyncTask", e);
        }
    }

    private void getSetTags( String value, String eventId) throws IOException {


        final String BASE_URL = "http://188.2.87.248:4000/rest/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        EventsInterface service = retrofit.create(EventsInterface.class);
        Call<List<TagsFromServer>> call = service.getallTags();
        List<TagsFromServer> tags=call.execute().body();
        Log.i("tags", "tags found " + String.valueOf(tags.size()));





        for (TagsFromServer tag : tags) {

            List<Event_db> eee=new Select().from(Event_db.class).where("eventId = ?", tag.getEventId()).execute();
            if(eee.size()>0) {
                Event_db e=eee.get(0);
                List<Tag_db> ttt = new Select().from(Tag_db.class).where("tagId = ?", tag.getTagId()).execute();
                if (ttt.size() == 0) {

                    Tag_db t = new Tag_db();
                    t.setValue(tag.getValue());
                    t.setWeight(tag.getWeight());
                    t.setTagId(tag.getTagId());
                    t.setEvent_db(e);

                    if(tag.getValue().equals(value) && tag.getEventId().equals(eventId)) t.setVote(true);

                    t.save();
                    e.save();
                    Log.i("tags", "tag saved");
                }else {

                    Tag_db ts = ttt.get(0);
                    ts.setWeight(tag.getWeight());

                    if(tag.getValue().equals(value) && tag.getEventId().equals(eventId)) ts.setVote(true);

                    ts.save();
                }
            }

        }
    }
}
