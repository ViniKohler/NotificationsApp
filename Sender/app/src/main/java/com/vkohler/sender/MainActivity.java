package com.vkohler.sender;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.Toast;

import com.vkohler.sender.databinding.ActivityMainBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {



    private static final String TAG = "MainActivity";
    public static final String TOPIC = "topics/deals";
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = binding.title.getText().toString();
                String content = binding.content.getText().toString();
                if (!title.isEmpty() && !content.isEmpty()) {
                    Notification message = new Notification(title, content);
                    PushNotification data = new PushNotification(message, TOPIC);
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(Constants.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    NotificationAPI apiService =
                            retrofit.create(NotificationAPI.class);
                    Call<Response> call = apiService.postNotification(data);
                    call.enqueue(new Callback<Response>() {
                        @Override
                        public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                            if (!response.isSuccessful()) {
                                Log.d(TAG, String.valueOf(response.code()));
                                return;
                            }
                            binding.title.setText("");
                            binding.content.setText("");
                            Toast.makeText(MainActivity.this, "Message pushed", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Response> call, Throwable t) {
                            Log.d(TAG, t.getMessage());
                        }
                    });
                }
            }
        });
    }
}