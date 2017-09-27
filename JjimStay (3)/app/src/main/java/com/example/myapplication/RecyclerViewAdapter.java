package com.example.myapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Currency;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Public on 2016-07-20.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<Spa> mItems;
    private int item_layout;
    private DecimalFormat money_format;

    public RecyclerViewAdapter(Context mContext, List<Spa> items, int item_layout) {
        mItems = new LinkedList<Spa>();
        this.mContext = mContext;
        this.mItems = items;
        this.item_layout = item_layout;

        money_format = new DecimalFormat("###,###,###");
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_card, parent, false);
        return new ViewHolder(mView);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Spa item = mItems.get(position);
        holder.name.setText(item.getSpaName());
        holder.price.setText(" " + Currency.getInstance(Locale.KOREA).getSymbol() + ' ' + money_format.format(Integer.parseInt(item.getMinPrice())) + " ~ ");
        holder.address.setText(item.getSimpleAddress());
        holder.backImage.setBackground(item.getMainImage());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //public ImageView mImageView;
        RelativeLayout backImage;
        public TextView name;
        TextView price;
        TextView address;

        ViewHolder(View itemView) {
            super(itemView);
            backImage = (RelativeLayout) itemView.findViewById(R.id.backImage);
            name = (TextView) itemView.findViewById(R.id.cardview_name);
            price = (TextView) itemView.findViewById(R.id.cardview_price);
            address = (TextView) itemView.findViewById(R.id.cardview_address);
        }
    }
}
