package edu.bsu.dlts.capstone;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GroupLobby extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView GroupLobbyRecyclerList;
    private DatabaseReference UsersRef;
    private String currentGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_lobby);

        currentGroupName = getIntent().getExtras().get("groupName").toString();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName).child("groupmembers");

        GroupLobbyRecyclerList = (RecyclerView)findViewById(R.id.group_lobby_recycler_list);
        GroupLobbyRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        mToolbar = (Toolbar)findViewById(R.id.find_friends_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Group Lobby");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options
                = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(UsersRef,Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,GroupLobby.GroupLobbyViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, GroupLobby.GroupLobbyViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull GroupLobby.GroupLobbyViewHolder holder, int position, @NonNull Contacts model) {

                        holder.userName.setText(model.getName());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public GroupLobby.GroupLobbyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                        GroupLobby.GroupLobbyViewHolder viewHolder = new GroupLobbyViewHolder(view);
                        return viewHolder;
                    }
                };

        GroupLobbyRecyclerList.setAdapter(adapter);
        adapter.startListening();

    }

    public static class GroupLobbyViewHolder extends RecyclerView.ViewHolder{

        TextView userName;


        public GroupLobbyViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
        }
    }
}
