package com.techjd.hubu.fragments;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.techjd.hubu.LocationService;
import com.techjd.hubu.MainActivity;
import com.techjd.hubu.R;
import com.techjd.hubu.cards.arrayAdapter;
import com.techjd.hubu.cards.cards;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class CardsFragment extends Fragment {


    ListView listView;
    List<cards> rowItems;
    private cards[] cards_data;
    private ArrayAdapter arrayAdapter;
    private int i;
    private Toolbar toolbar_home;
    private FirebaseAuth mAuth;
    private String currentUId;
    private DatabaseReference usersDb;
    private BottomNavigationView mMainNav;
    private String mUserSex, mOppositeSex;
    public CardsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cards, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();

        currentUId = mAuth.getCurrentUser().getUid();

        checkUserSex();


        rowItems = new ArrayList<>();


        arrayAdapter = new arrayAdapter(getActivity(), R.layout.item, rowItems);

        final SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) view.findViewById(R.id.frame);

        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {

            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {

                cards card = (cards) dataObject;
                String userId = card.getUserId();

                usersDb.child(userId).child("connections")
                        .child("nope").child(currentUId).setValue(true);

                Toast.makeText(getActivity(), "nope", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {

                cards card = (cards) dataObject;
                String userId = card.getUserId();

                usersDb.child(userId).child("connections")
                        .child("yes").child(currentUId).setValue(true);
                isConnectionMatch(userId);
                Toast.makeText(getActivity(), "yes", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
//                View view = flingContainer.getSelectedView();
//                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
//                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }


        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(getActivity(), "Item clicked", Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton swiperight = view.findViewById(R.id.swiperigt);
        swiperight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flingContainer.getTopCardListener().selectRight();
            }
        });
        ImageButton swipeleft = view.findViewById(R.id.swipeleft);
        swipeleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flingContainer.getTopCardListener().selectLeft();
            }
        });





    }

    private void isConnectionMatch(String userId) {

        DatabaseReference currentUserConnectionsDB = usersDb.child(currentUId)
                .child("connections").child("yes").child(userId);

        currentUserConnectionsDB.addListenerForSingleValueEvent(new ValueEventListener() {

            // Keeps looking for change of data in Firebase database content
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    Toast.makeText(getActivity(), "A match has been made!", Toast.LENGTH_LONG).show();

                    // This won't create a child inside chat but it will give the key for that chat.
                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();

                    usersDb.child(dataSnapshot.getKey())
                            .child("connections").child("matches")
                            .child(currentUId).child("Chatid").setValue(key);

                    usersDb.child(currentUId)
                            .child("connections").child("matches")
                            .child(dataSnapshot.getKey()).child("Chatid").setValue(key);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkUserSex() {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userDb = usersDb.child(user.getUid());
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {

            // keeps looking for changes in the Firebase database
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("sex").getValue() != null) {
                        mUserSex = dataSnapshot.child("sex").getValue().toString();

                        // Temporary preference assignment
                        if(mUserSex.equals("Male")) {
                            mOppositeSex = "Female";
                        }
                        if(mUserSex.equals("Female")) {
                            mOppositeSex = "Male";
                        }
                        getOppositeSex();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getOppositeSex() {
        usersDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                // if(dataSnapshot.child(FirebaseEntry.COLUMN_SEX).getValue() != null)

                // Check whether the database exists and also check if the user hasn't already swiped the matches left or right
                if(dataSnapshot.exists()
                        && !dataSnapshot.child("connections").child("nope").hasChild(currentUId)
                        && !dataSnapshot.child("connections").child("yes").hasChild(currentUId)
                        && dataSnapshot.child("sex").getValue().toString().equals(mOppositeSex)) {


                    String profileImageUrl = "default";

                    // If user has assigned an image on registration, assign it to profileImageUrl
                    if(!dataSnapshot.child("profileImageUrl").getValue().equals("default")) {
                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                    }

                    cards item = new cards(dataSnapshot.getKey(),
                            dataSnapshot.child("name").getValue().toString(),
                            profileImageUrl);

                    rowItems.add(item);
                    arrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}


