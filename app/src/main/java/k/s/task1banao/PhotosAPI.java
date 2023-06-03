package k.s.task1banao;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PhotosAPI {
    @GET("/services/rest")
    Call<PhotoResBean> getPhotos(@Query("method") String method, @Query("per_page") int per_page, @Query("page") int page, @Query("api_key") String api_key,
    @Query("format") String format, @Query("nojsoncallback") int nojsoncallback, @Query("extras") String extras);
}
