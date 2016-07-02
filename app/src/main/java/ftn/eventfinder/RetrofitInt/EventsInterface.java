package ftn.eventfinder.RetrofitInt;

import java.util.List;

import ftn.eventfinder.model.EventsResponse;
import ftn.eventfinder.model.Tag;
import ftn.eventfinder.model.TagsFromServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Jovan on 20.5.2016.
 */
public interface EventsInterface {
    // Request method and URL specified in the annotation
    // Callback for the parsed response is the last parameter

    @GET("events?distance=2500&sort=time&access_token=263841713956622|l2uKn8wbzuk3OpKM7BzwB7y1VvY")
    Call<EventsResponse> getEvents(
            @Query("lat") Double lat,
            @Query("lng") Double lng);

    @POST("rest/tags/createTag")
    Call<Tag> addTag(@Body Tag tag);

    @GET("rest/tags/getAllTags")
    Call<List<TagsFromServer>> getallTags();

    @POST("rest/tags/increaseWeight/{id}")
    Call<TagsFromServer> upVoteTag(
            @Path("id") int id);
}
