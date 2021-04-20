package com.example.homerenting_prototype_one.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homerenting_prototype_one.BuildConfig;
import com.example.homerenting_prototype_one.R;
import com.example.homerenting_prototype_one.adapter.re_adpater.SwipeDeleteAdapter;
import com.example.homerenting_prototype_one.adapter.re_adpater.TextAdapter;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.homerenting_prototype_one.show.global_function.getCompany_id;

public class RecyclerViewAction extends ItemTouchHelper.Callback {
    Context context;
    SwipeDeleteAdapter s_adapter;
    TextAdapter t_adapter;
    int current = 0;
    String TAG = "RecyclerViewAction ";

    public RecyclerViewAction(Context context, SwipeDeleteAdapter adapter){
        this.context = context;
        s_adapter = adapter;
        current = 1;
    }

    public RecyclerViewAction(Context context, TextAdapter adapter){
        this.context = context;
        t_adapter = adapter;
        current = 2;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.LEFT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("確定要刪除?");
        builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (current) {
                    case 1:
                        deleteOrderData(position);
                        s_adapter.deleteItem(position);
                        break;
                    case 2:
                        deleteTextData(position);
                        t_adapter.deleteItem(position);
                        break;
                }

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (current) {
                    case 1:
                        s_adapter.notifyItemChanged(position);
                        break;
                    case 2:
                        t_adapter.notifyItemChanged(position);
                        break;
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){
        String warning_color = "#B63434";
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addBackgroundColor(Color.parseColor(warning_color))
                .addActionIcon(R.drawable.ic_baseline_delete_24)
                .create()
                .decorate();
    }

    private void deleteOrderData(int position){
        String order_id = t_adapter.getItem(position)[0];

        String function_name = "delete_order";
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("order_id", order_id)
                .build();
        Log.i(TAG, "delete order: "+order_id);

        Request request = new Request.Builder()
                .url(BuildConfig.SERVER_URL+"/functional.php")
                .post(body)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Failed: " + e.getMessage()); //顯示錯誤訊息
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                Log.d(TAG,"responseData of delete order: "+responseData); //顯示資料
            }
        });
    }

    private void deleteTextData(int position){
        String id = t_adapter.getItem(position)[0];
        String table = "staff";
        if(t_adapter.getItem(0).length > 2) table = "vehicle";
        final String finalTable = table;

        String function_name = "delete_staff_vehicle";
        RequestBody body = new FormBody.Builder()
                .add("function_name", function_name)
                .add("table", table)
                .add("id", id)
                .build();
        Log.i(TAG, "delete: "+table+" "+id);

        Request request = new Request.Builder()
                .url(BuildConfig.SERVER_URL+"/functional.php")
                .post(body)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Failed: " + e.getMessage()); //顯示錯誤訊息
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                Log.d(TAG,"responseData of delete "+finalTable+": "+responseData); //顯示資料
            }
        });
    }
}
