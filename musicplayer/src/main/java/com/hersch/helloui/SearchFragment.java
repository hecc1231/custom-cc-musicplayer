package com.hersch.helloui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.hersch.musicplayer.R;

/**
 * search lyrics Page
 */
public class SearchFragment extends Fragment {
    private Context context;
    private Button searchBtn;
    private EditText songText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.hello_fragment_search,container,false);
        searchBtn = (Button)view.findViewById(R.id.searchBtn);
        songText  = (EditText)view.findViewById(R.id.songText);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(songText.getText()!=null){
                    String songName = songText.getText().toString();
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
