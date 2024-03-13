package com.example.parky;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardItemsAdapter extends RecyclerView.Adapter<DashboardItemsAdapter.ItemsViewHolder> {

    ArrayList<DashboardItems> items;

    public DashboardItemsAdapter(ArrayList<DashboardItems> items){
        this.items = items;
    }

    @NonNull
    @Override
    public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent,false);
        return new ItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardItemsAdapter.ItemsViewHolder holder, int position) {
        DashboardItems item = items.get(position);


        holder.price.setText(item.getPrice());
        holder.address.setText(item.getAddress());
        holder.locality.setText(item.getLocality());

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            final Bitmap bitmap = getBitmapFromURL(item.getImage());
            handler.post(() -> holder.image.setImageBitmap(bitmap));
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ItemsViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView locality, address, price;
        public ItemsViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            locality = itemView.findViewById(R.id.locality);
            address = itemView.findViewById(R.id.address);
            price = itemView.findViewById(R.id.price);

        }
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
