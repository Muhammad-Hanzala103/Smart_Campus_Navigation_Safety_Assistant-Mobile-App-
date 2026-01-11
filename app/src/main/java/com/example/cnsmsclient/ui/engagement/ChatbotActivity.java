package com.example.cnsmsclient.ui.engagement;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cnsmsclient.R;

import java.util.ArrayList;
import java.util.List;

public class ChatbotActivity extends AppCompatActivity {

    private RecyclerView chatRecycler;
    private EditText inputMessage;
    private ChatAdapter adapter;
    private List<ChatMessage> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        chatRecycler = findViewById(R.id.chatRecycler);
        inputMessage = findViewById(R.id.inputMessage);

        messages = new ArrayList<>();
        messages.add(new ChatMessage(
                "Hello! I am your AI Assistant. Ask me about your classes, fees, or campus locations.", false));

        adapter = new ChatAdapter(messages);
        chatRecycler.setLayoutManager(new LinearLayoutManager(this));
        chatRecycler.setAdapter(adapter);

        findViewById(R.id.btnSend).setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String query = inputMessage.getText().toString().trim();
        if (query.isEmpty())
            return;

        messages.add(new ChatMessage(query, true));
        adapter.notifyItemInserted(messages.size() - 1);
        inputMessage.setText("");
        chatRecycler.smoothScrollToPosition(messages.size() - 1);

        // Simulate AI typing delay
        new Handler().postDelayed(() -> {
            String reply = getBotReply(query);
            messages.add(new ChatMessage(reply, false));
            adapter.notifyItemInserted(messages.size() - 1);
            chatRecycler.smoothScrollToPosition(messages.size() - 1);
        }, 1000);
    }

    private String getBotReply(String query) {
        query = query.toLowerCase();
        if (query.contains("fee") || query.contains("chalan"))
            return "You can view your Fee Chalan in the Financial Dashboard. You have pending dues of PKR 15,000.";
        if (query.contains("location") || query.contains("where"))
            return "Use the AR Navigation feature to find your way around campus!";
        if (query.contains("exam") || query.contains("date"))
            return "Mid-term exams start from October 15th.";
        if (query.contains("library"))
            return "The library is open from 8 AM to 8 PM. You can check book availability in the Library section.";
        return "I'm still learning! Try asking about fees, exams, or locations.";
    }
}
