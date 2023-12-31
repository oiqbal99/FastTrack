package com.ccps406.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.SimpleFormatter;

public class History extends AppCompatActivity {

    TextView mTest, mDescription, mDate;
    ArrayList<Double> amounts = new ArrayList<>();
    ArrayList<String> descriptions = new ArrayList<>();
    ArrayList<String> dates = new ArrayList<>();
    ArrayList<String>  types = new ArrayList<>();
    public final String wantedFormat = "MM/dd/yyyy";

    Calendar newDate = Calendar.getInstance();

    FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    CollectionReference historyRef = mStore.collection("users").document(user.getUid()).collection("History");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mTest = findViewById(R.id.empty_title);
        amountGet();

    }

    public void amountGet() {
        historyRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        amounts.add(Math.round(document.getDouble("amount")*100)/100.0);
                        descriptions.add(document.getString("description"));
                        dates.add(convertStr(document.getDate("startDate")));
                        types.add(document.getString("type"));
                        mTest.setText("");
                        recyclerMethod();
                    }
                }else{
                    mTest.setText("Looks like there's nothing here yet");
                }
            }
        });

    }

    public String convertStr(Date date)  {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat(wantedFormat); //Changed
        return formatter.format(date);
    }



    private void recyclerMethod(){
        RecyclerView rView = findViewById(R.id.history_view);
        RecyclerViewAdpater adpater = new RecyclerViewAdpater(amounts, descriptions, dates, types);
        rView.setAdapter(adpater);
        rView.setLayoutManager(new LinearLayoutManager(this));
    }
}