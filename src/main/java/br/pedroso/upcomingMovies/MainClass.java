package br.pedroso.upcomingMovies;

import br.pedroso.upcomingMovies.rest.MoviesService;
import br.pedroso.upcomingMovies.rest.model.MovieInfo;
import br.pedroso.upcomingMovies.rest.model.MovieResults;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

/**
 * Created by felip on 18/08/2016.
 */
public class MainClass {

    public static void main(String[] args) {
        Interceptor interceptor = new Interceptor() {
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                HttpUrl originalUrl = originalRequest.url();

                HttpUrl urlWithApiKey = originalUrl.newBuilder()
                        .addQueryParameter(MoviesService.API_KEY_QUERY_PARAMETER, "<YOUR-API-KEY>")
                        .build();

                Request modifiedRequest = originalRequest.newBuilder()
                        .url(urlWithApiKey)
                        .build();

                okhttp3.Response response = chain.proceed(modifiedRequest);

                return response;
            }
        };

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(interceptor);
        OkHttpClient httpClient = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MoviesService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        MoviesService moviesService = retrofit.create(MoviesService.class);

        moviesService.listUpcomingMovies().enqueue(new Callback<MovieResults>() {
            public void onResponse(Call<MovieResults> call, Response<MovieResults> response) {
                MovieResults results = response.body();

                for (MovieInfo movieInfo : results.getResults()){
                    System.out.println(movieInfo.toString());
                }
            }

            public void onFailure(Call<MovieResults> call, Throwable throwable) {
                System.out.println("Failed to list upcoming movies.");
            }
        });
    }
}
