package com.example.yrsappserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.yrsappserver.Common.Common;
import com.example.yrsappserver.Common.StatisticsHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Statistics extends AppCompatActivity {

    FirebaseDatabase db;
    DatabaseReference users;
    TextView totalClients;
    Button monthlyGraphBtn, weeklyGraphBtn;

    FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
    BarChart barChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        setContentView(R.layout.activity_statistics);

        monthlyGraphBtn = (Button) findViewById(R.id.monthlyGraph);
        weeklyGraphBtn = (Button) findViewById(R.id.weeklyGraph);
        barChart = (BarChart) findViewById(R.id.barChart);

        totalClients = (TextView) findViewById(R.id.totalClients);
        Typeface face = Typeface.createFromAsset(getAssets(),"fonts/NABILA.TTF");
        totalClients.setTypeface(face);

        //Init Firebase
        db = FirebaseDatabase.getInstance();
        users = db.getReference("User");

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int counter = (int) dataSnapshot.getChildrenCount();

                String usersCounter = "Registered customers to your restaurant app: "+String.valueOf(counter);
                SpannableString ss1=  new SpannableString(usersCounter);
                ss1.setSpan(new RelativeSizeSpan(2f), usersCounter.length()-2,usersCounter.length(), 0); // set size
                ss1.setSpan(new ForegroundColorSpan(0xFFFCFF48), usersCounter.length()-2,usersCounter.length(), 0);// set color
                ss1.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), usersCounter.length()-2,usersCounter.length(), 0); // set style

                totalClients.setText(ss1);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*
        // שליחה נתונים שבועיים לפונקצייה שמקימה את הגרף

        getWeeklyActivity(new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList<StatisticsHelper> data) {

                createWeeklyGraph(data);

            }
        });
        */

        /*
        // שליחה נתונים חודשיים לפונקצייה שמקימה את הגרף
        getMonthlyActivity(new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList<StatisticsHelper> data) {
                createMonthlyGraph(data);
            }
        });
        */


        monthlyGraphBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMonthlyActivity(new FirebaseCallback() {
                    @Override
                    public void onCallback(ArrayList<StatisticsHelper> data) {
                        createMonthlyGraph(data);
                    }
                });
            }
        });

        weeklyGraphBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWeeklyActivity(new FirebaseCallback() {
                    @Override
                    public void onCallback(ArrayList<StatisticsHelper> data) {
                        createWeeklyGraph(data);
                    }
                });
            }
        });



    }

    // יצירת גרף עבור ממצאים שבועיים כולל כניסות והירשמויות
    private void createWeeklyGraph(ArrayList<StatisticsHelper> list) {


        int len = list.size();


        ArrayList<String> dates = new ArrayList<>();
        ArrayList<BarEntry> logins = new ArrayList<>();
        ArrayList<BarEntry> registered = new ArrayList<>();



        //initialize bars for the bar graph
        for(int i = 0 ; i < len ; i++){
            dates.add(list.get(i).getDate());
            logins.add( new BarEntry(i,list.get(i).getLogin()));
            registered.add(new BarEntry(i,list.get(i).getRegister()));

        }

        BarDataSet barDataSet1 = new BarDataSet(logins,"Weekly Logins");
        barDataSet1.setColor(Color.RED);
        barDataSet1.setValueTextSize(12);

        BarDataSet barDataSet2 = new BarDataSet(registered,"Weekly Registered");
        barDataSet2.setColor(Color.BLUE);
        barDataSet2.setValueTextSize(12);



        //give the bar its data bars
        BarData data = new BarData(barDataSet1,barDataSet2);
        barChart.setData(data);

        //ui related x positions and ui configuration of the bar
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setTextSize(11);
        xAxis.setGranularityEnabled(true);

        barChart.setDragEnabled(true);
        barChart.setVisibleXRangeMaximum(4);

        float barSpace = 0.18f;
        float groupSpace = 0.14f;
        data.setBarWidth(0.10f);

        Legend legend = barChart.getLegend();
        legend.setTextSize(16);

        barChart.getXAxis().setAxisMinimum(0);
        barChart.getXAxis().setAxisMaximum(0+barChart.getBarData().getGroupWidth(groupSpace,barSpace)*7);
        barChart.getAxisLeft().setAxisMinimum(0);

        barChart.groupBars(0,groupSpace,barSpace);
        barChart.invalidate();

    }

    // יצירת גרף עבור ממצאים חודשיים כולל כניסות והירשמויות
    private void createMonthlyGraph(ArrayList<StatisticsHelper> list) {


        int len = list.size();


        ArrayList<String> dates = new ArrayList<>();
        ArrayList<BarEntry> logins = new ArrayList<>();
        ArrayList<BarEntry> registered = new ArrayList<>();



        //initialize bars for the bar graph
        for(int i = 0 ; i < len ; i++){
            dates.add(list.get(i).getDate());
            logins.add( new BarEntry(i,list.get(i).getLogin()));
            registered.add(new BarEntry(i,list.get(i).getRegister()));

        }

        BarDataSet barDataSet1 = new BarDataSet(logins,"Montly Logins");
        barDataSet1.setColor(Color.RED);
        barDataSet1.setValueTextSize(12);

        BarDataSet barDataSet2 = new BarDataSet(registered,"Montly Registered");
        barDataSet2.setColor(Color.BLUE);
        barDataSet2.setValueTextSize(12);



        //give the bar its data bars
        BarData data = new BarData(barDataSet1,barDataSet2);
        barChart.setData(data);

        //ui related x positions and ui configuration of the bar
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setTextSize(11);
        xAxis.setGranularityEnabled(true);

        barChart.setDragEnabled(true);
        barChart.setVisibleXRangeMaximum(4);

        float barSpace = 0.18f;
        float groupSpace = 0.14f;
        data.setBarWidth(0.10f);

        Legend legend = barChart.getLegend();
        legend.setTextSize(16);

        barChart.getXAxis().setAxisMinimum(0);
        barChart.getXAxis().setAxisMaximum(0+barChart.getBarData().getGroupWidth(groupSpace,barSpace)*7);
        barChart.getAxisLeft().setAxisMinimum(0);

        barChart.groupBars(0,groupSpace,barSpace);
        barChart.invalidate();

    }

    //קבלת המידע מן ה- firestore שבועי
    private void getWeeklyActivity(final FirebaseCallback firebaseCallback){

        firestoreDB.collection("STATISTICS").document("users").collection("userWeekly").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    ArrayList<StatisticsHelper> list = new ArrayList<>();

                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                            StatisticsHelper helper = new StatisticsHelper();

                            helper.setDate(documentSnapshot.getId());
                            helper.setLogin(Integer.parseInt(documentSnapshot.get("logins").toString()));
                            helper.setRegister(Integer.parseInt(documentSnapshot.get("registered").toString()));

                            list.add(helper);
                    }

                    firebaseCallback.onCallback(list);


                }
            }
        });


    }

    //קבלת המידע מן ה- firestore חודשי
    private void getMonthlyActivity(final FirebaseCallback firebaseCallback){

        firestoreDB.collection("STATISTICS").document("users").collection("userMonthly").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    ArrayList<StatisticsHelper> list = new ArrayList<>();

                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        StatisticsHelper helper = new StatisticsHelper();

                        helper.setDate(documentSnapshot.getId());
                        helper.setLogin(Integer.parseInt(documentSnapshot.get("logins").toString()));
                        helper.setRegister(Integer.parseInt(documentSnapshot.get("registered").toString()));

                        list.add(helper);
                    }

                    firebaseCallback.onCallback(list);


                }
            }
        });


    }

    //interface for talking with firebase and getting an answer
    private interface FirebaseCallback{
        void onCallback(ArrayList<StatisticsHelper> data);
    }




}