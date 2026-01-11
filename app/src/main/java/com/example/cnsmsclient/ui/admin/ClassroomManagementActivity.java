package com.example.cnsmsclient.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cnsmsclient.R;

import java.util.Arrays;
import java.util.List;

public class ClassroomManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom_management);

        RecyclerView recycler = findViewById(R.id.roomsRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        List<ClassRoom> rooms = Arrays.asList(
                new ClassRoom("Room 101", "Computer Science", "Occupied"),
                new ClassRoom("Room 102", "Software Eng", "Free"),
                new ClassRoom("Auditorium", "General", "Booked for Event"));

        recycler.setAdapter(new RoomAdapter(rooms));
    }

    // Mock Model
    static class ClassRoom {
        String name, dept, status;

        ClassRoom(String n, String d, String s) {
            name = n;
            dept = d;
            status = s;
        }
    }

    // Inner Adapter Class
    class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
        List<ClassRoom> list;

        RoomAdapter(List<ClassRoom> l) {
            list = l;
        }

        @NonNull
        @Override
        public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent,
                    false);
            return new RoomViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
            ClassRoom r = list.get(position);
            holder.text1.setText(r.name + " (" + r.status + ")");
            holder.text2.setText(r.dept);
            holder.itemView.setOnClickListener(
                    v -> Toast.makeText(getApplicationContext(), "Managing " + r.name, Toast.LENGTH_SHORT).show());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class RoomViewHolder extends RecyclerView.ViewHolder {
            TextView text1, text2;

            RoomViewHolder(View v) {
                super(v);
                text1 = v.findViewById(android.R.id.text1);
                text2 = v.findViewById(android.R.id.text2);
            }
        }
    }
}
