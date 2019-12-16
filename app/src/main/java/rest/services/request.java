package rest.services;

import activity.Loginactivity;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface request {

    @POST("login")
    Call<Integer>signin(@Body Loginactivity.Userinfo userinfo);
}
