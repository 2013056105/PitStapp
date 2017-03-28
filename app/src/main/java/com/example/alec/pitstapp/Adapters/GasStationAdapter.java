package com.example.alec.pitstapp.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alec.pitstapp.Fragments.NearbyGasStationFragment;
import com.example.alec.pitstapp.MainActivity;
import com.example.alec.pitstapp.R;

import java.util.Collections;
import java.util.List;

public class GasStationAdapter extends RecyclerView.Adapter<GasStationAdapter.MyNearbyHolder>{
    private GasStation gasStation;
    private LayoutInflater inflater;
    private Context context;
    private List<GasStation> list = Collections.emptyList();

    public GasStationAdapter(Context context, List<GasStation> list) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    @Override
    public MyNearbyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_result_item, parent, false);
        MyNearbyHolder holder = new MyNearbyHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyNearbyHolder holder, int position) {
        GasStation current = list.get(position);

        holder.gasStationName.setText(current.getGasStationName());
        holder.gasStationVicinity.setText(current.getGasStationVicinity());
        holder.gasStationPlaceID.setText(current.getGasStationPlaceID());
        holder.gasStationLatitude.setText(current.getGasStationLatitude());
        holder.gasStationLongitude.setText(current.getGasStationLongitude());
    }

    class MyNearbyHolder extends RecyclerView.ViewHolder {

        TextView gasStationName;
        TextView gasStationVicinity;
        TextView gasStationPlaceID;
        TextView gasStationLatitude;
        TextView gasStationLongitude;
        View view;

        public MyNearbyHolder(View itemView) {
            super(itemView);
            view = itemView;

            gasStationName = (TextView) view.findViewById(R.id.textView_gasStationName);
            gasStationVicinity = (TextView) view.findViewById(R.id.textView_vicinity);
            gasStationPlaceID = (TextView) view.findViewById(R.id.textview_placeid);
            gasStationLatitude = (TextView) view.findViewById(R.id.textview_latitude);
            gasStationLongitude = (TextView) view.findViewById(R.id.textview_longitude);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String chosenGasStation = ((TextView)v.findViewById(R.id.textView_gasStationName)).getText().toString();
                    String chosenGasStationPlaceID = ((TextView)v.findViewById(R.id.textview_placeid)).getText().toString();
                    String chosenGasStationLat = ((TextView)v.findViewById(R.id.textview_latitude)).getText().toString();
                    String chosenGasStationLng = ((TextView)v.findViewById(R.id.textview_longitude)).getText().toString();

                    String chosenGasStationVicinity = ((TextView)v.findViewById(R.id.textView_vicinity)).getText().toString();

                    NearbyGasStationFragment nearbyGasStationInformation = new NearbyGasStationFragment();
                    FragmentTransaction fragmentTransaction = ((MainActivity)context).getSupportFragmentManager().beginTransaction();
                    Bundle args = new Bundle();
                    args.putString("chosenGasStation" , chosenGasStation);
                    args.putString("chosenGasStationPlaceID" , chosenGasStationPlaceID);
                    args.putString("chosenGasStationLat", chosenGasStationLat);
                    args.putString("chosenGasStationLng", chosenGasStationLng);
                    args.putString("chosenGasStationVicinity", chosenGasStationVicinity);

                    nearbyGasStationInformation.setArguments(args);

                    fragmentTransaction.add(nearbyGasStationInformation, "nearbyGasStationInformation")
                            .replace(R.id.fragment_container, nearbyGasStationInformation)
                            .addToBackStack("nearbyGasStationInformation")
                            .commit();
                }
            });
        }
    }
}
