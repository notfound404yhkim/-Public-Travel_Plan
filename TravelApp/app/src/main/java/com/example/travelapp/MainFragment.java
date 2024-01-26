package com.example.travelapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.core.util.Pair;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MainFragment extends Fragment {

    Button btn;
    TextView textView;
    TextView txtRegion,txtDate;
    RelativeLayout RegionLayout; //지역 선택부분 레이아웃
    RelativeLayout DateLayout;
    LinearLayout BottomLayout; //하단 레이아웃
    ListView listRegion; //지역 리스트 뷰
    Calendar calendar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);  //프레그먼트 레이아웃지정.

        btn = view.findViewById(R.id.btn_select);
        textView = view.findViewById(R.id.txt_more);
        RegionLayout = view.findViewById(R.id.RegionLayout);
        BottomLayout = view.findViewById(R.id.BottomLayout);
        DateLayout = view.findViewById(R.id.DateLayout);
        listRegion = view.findViewById(R.id.listRegion);
        txtRegion = view.findViewById(R.id.txtRegion);
        txtDate = view.findViewById(R.id.txtDate);
        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        //오늘 날짜
        Long today = MaterialDatePicker.todayInUtcMilliseconds();


        //지역 선택 부분    프레그먼트이므로 getActivity()
        final String[] region = {"서울","인천","대전","대구","광주","부산","제주"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, region);

        listRegion.setAdapter(adapter);
        RegionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomLayout.setVisibility(View.GONE);
                listRegion.setVisibility(View.VISIBLE);
            }
        });

        listRegion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0,View arg1, int arg2, long arg3) {
                //Toast.makeText(getActivity(),region[arg2],Toast.LENGTH_SHORT).show();
                txtRegion.setText(region[arg2]); //선택한지역값은 arg2에있음.
                BottomLayout.setVisibility(View.VISIBLE);
                listRegion.setVisibility(View.GONE);
            }
        });


        //날짜 선택부분
        DateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();

                builder.setTitleText("Date Picker");
                builder.setSelection(Pair.create(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds()));

                MaterialDatePicker materialDatePicker = builder.build();

                materialDatePicker.show(requireFragmentManager(), "DATE_PICKER");
                //확인버튼
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date date1 = new Date();
                        Date date2 = new Date();

                        date1.setTime(selection.first);
                        date2.setTime(selection.second);

                        String dateString1 = simpleDateFormat.format(date1);
                        String dateString2 = simpleDateFormat.format(date2);

                        txtDate.setText(dateString1 + " ~ " + dateString2);
                    }
                });
            }
        });



        // 선택 완료 눌렀을때.
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ListActivity.class);
                startActivity(intent);

            }
        });



        // 축제 및 행사 리스트 더보기
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PlaceActivity.class);
                startActivity(intent);

            }
        });


        return view;
    }

}

