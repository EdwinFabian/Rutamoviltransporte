package com.example.rutamoviltransporte;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;

public abstract class SimplifiedCallback implements Callback<DirectionsResponse> {
    @Override
    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
        Timber.e(throwable, throwable.getMessage());
    }
}
