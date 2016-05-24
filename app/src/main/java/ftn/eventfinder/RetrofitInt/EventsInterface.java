package ftn.eventfinder.RetrofitInt;

import java.util.List;

import ftn.eventfinder.model.EventsResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;

/**
 * Created by Jovan on 20.5.2016.
 */
public interface EventsInterface {
    // Request method and URL specified in the annotation
    // Callback for the parsed response is the last parameter

    @GET("events?lat=45.254294&lng=19.842446&distance=2500&sort=venue&access_token=263841713956622|l2uKn8wbzuk3OpKM7BzwB7y1VvY")
    Call<EventsResponse> getEvents();
}
