package com.example.detecto.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.detecto.R;
import com.example.detecto.TextActivity;

import java.util.ArrayList;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {
    ArrayList<Integer> ids;
    ArrayList<String> Titles;
    ArrayList<String> body;
    Listener listener;
    public RVAdapter(ArrayList<Integer> i,ArrayList<String> T,ArrayList<String> D){
        ids=i;
        Titles=T;
        body=D;
    }
    public interface Listener{
        void onClickRV(int position);
    }

    public void setListener(Listener l){
        listener=l;
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        public ViewHolder(@NonNull CardView v) {
            super(v);
            cardView=v;
        }
    }

    @NonNull
    @Override
    public RVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cv=(CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view,parent,false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(@NonNull RVAdapter.ViewHolder holder, int position) {
        CardView cv=holder.cardView;
        TextView idtext=cv.findViewById(R.id.card_view_id);
        TextView titletext=cv.findViewById(R.id.card_view_Title);
        TextView bodytext=cv.findViewById(R.id.card_view_body);
        idtext.setText(""+ids.get(position));
        titletext.setText(Titles.get(position));
        bodytext.setText(body.get(position));
        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(), TextActivity.class);
                Bundle extras=new Bundle();
                extras.putString("TITLE",Titles.get(position));
                extras.putString("BODY",body.get(position));
                extras.putInt("id",ids.get(position));
                intent.putExtras(extras);
                v.getContext().startActivity(intent);
            }
        });
        Button delButton=cv.findViewById(R.id.card_view_del);
        delButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                listener.onClickRV(ids.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return ids.size();
    }
}
