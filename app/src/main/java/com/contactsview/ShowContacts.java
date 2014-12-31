package com.contactsview;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class ShowContacts extends ActionBarActivity {
    private RecyclerView recList;
    private  SwipeRefreshLayout swipeRefresh;
    private List<ContactInfo> fullContactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_contacts);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Pick a contact");
        setSupportActionBar(toolbar);

        recList = (RecyclerView) findViewById(R.id.cardList);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setColorSchemeResources(R.color.teal_500,
                R.color.material_deep_teal_200,
                R.color.teal_900);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               new RefreshTask().execute();
            }
        });
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                toolbar.setTitle("Pick a contact");
                showContacts(fullContactList);
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbar.setTitle("");
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String data) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String data) {
                if(!data.isEmpty()){
                    showMatchingContacts(data);
                }else{
                    showContacts(fullContactList);
                }
                return false;
            }
        });

        new GetContacts(this).execute();
    }

    private void showContacts(final List<ContactInfo> contacts){
        Collections.sort(contacts,new Comparator<ContactInfo>() {
            @Override
            public int compare(ContactInfo contactInfo1, ContactInfo contactInfo2) {
                return contactInfo1.getName().compareTo(contactInfo2.getName());
            }
        });
        RecycleAdapter adapter = new RecycleAdapter(contacts);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.i("click", contacts.get(position).getName());
                String[] emails = contacts.get(position).getEmails();
                for (String email : emails) {
                    Log.i("Email", email);
                }

            }
        });
        recList.setAdapter(adapter);
    }

    private void showMatchingContacts(String data){
        List<ContactInfo> matchList = new ArrayList<>();
        for(ContactInfo contactInfo : fullContactList){
            if(contactInfo.getName().toLowerCase().contains(data.toLowerCase())){
                matchList.add(contactInfo);
            }
        }
        showContacts(matchList);

    }

    private class GetContacts extends AsyncTask<Void, Void, List<ContactInfo>>{
        SweetAlertDialog pDialog;
        Activity activity;
        private GetContacts(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Fetching Contacts...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected List<ContactInfo> doInBackground(Void... voids) {
            List<ContactInfo> contactInfoList = new ArrayList<>();
            ContentResolver cr = getContentResolver();
            Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    Cursor emailCursor = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + id, null, null);
                    if(emailCursor.getCount() > 0){
                        ContactInfo contactInfo = new ContactInfo(id, name, getResources().getDrawable(R.drawable.user));
                        while (emailCursor.moveToNext()){
                            contactInfo.addEmail(emailCursor.getString(emailCursor
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));
                        }
                        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(id));
                        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
                        Cursor picCursor = getContentResolver().query(photoUri,
                                new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
                        if (picCursor.moveToFirst()){
                            contactInfo.setImage(picCursor.getBlob(0));
                        }
                        picCursor.close();

                        contactInfoList.add(contactInfo);

                    }
                    emailCursor.close();
                }
            }

            cursor.close();
            return contactInfoList;
        }

        @Override
        protected void onPostExecute(final List<ContactInfo> contactInfoList) {
            super.onPostExecute(contactInfoList);
            pDialog.cancel();
            fullContactList = contactInfoList;
            showContacts(contactInfoList);
        }
    }


    private class RefreshTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(swipeRefresh.isRefreshing()){
                swipeRefresh.setRefreshing(false);
            }
        }
    }
}
