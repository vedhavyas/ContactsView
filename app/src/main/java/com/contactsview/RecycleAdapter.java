package com.contactsview;

import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import at.markushi.ui.RevealColorView;

/**
 * Authored by vedhavyas.singareddi on 23-12-2014.
 */
public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ContactViewHolder> {

    private List<ContactInfo> contactList;
    private OnItemClickListener onItemClickListener;
    private RevealColorView ripple;

    public RecycleAdapter(List<ContactInfo> contactList) {
        this.contactList = contactList;
    }

    @Override
    public RecycleAdapter.ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_view, viewGroup, false);

        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecycleAdapter.ContactViewHolder holder, int position) {
        ContactInfo ci = contactList.get(position);
        holder.title.setText(ci.getName());
        holder.profilePic.setImageBitmap(ci.getImage());
    }

    @Override
    public int getItemCount() {
        if(contactList != null){
            return contactList.size();
        }
        return 0;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public  class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ImageView profilePic;
        protected TextView title;

        public ContactViewHolder(View v) {
            super(v);
            profilePic = (ImageView) v.findViewById(R.id.profilePic);
            title = (TextView) v.findViewById(R.id.title);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(onItemClickListener != null){
                onItemClickListener.onItemClick(view,getPosition());
            }
        }
    }

}
