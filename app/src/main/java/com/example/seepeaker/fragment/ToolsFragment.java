package com.example.seepeaker.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.seepeaker.activity.PronunciationHelpActivity;
import com.example.seepeaker.R;
import com.example.seepeaker.controller.UserController;

import java.util.ArrayList;
import java.util.List;

// 하단 바의 기타 부분
public class ToolsFragment extends Fragment {

    private Button buttonReviewNoteReset, buttonPronunciationPositionImageOpen, buttonPronunciationHelp;
    private ImageView imageViewPronunciationPosition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tools, container, false);

        // 여기서 XML 레이아웃의 요소들을 참조하고 조작할 수 있습니다.
        buttonReviewNoteReset = view.findViewById(R.id.buttonReviewNoteReset);
        buttonPronunciationPositionImageOpen = view.findViewById(R.id.buttonPronunciationPositionImageOpen);
        buttonPronunciationHelp = view.findViewById(R.id.buttonPronunciationHelp);
        imageViewPronunciationPosition = view.findViewById(R.id.imagePronunciationPosition);

        // 오답노트 초기화
        buttonReviewNoteReset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"오답노트를 초기화하였습니다.",Toast.LENGTH_LONG).show();
            }
        });

        // 발음 위치 이미지 파일 열기
        buttonPronunciationPositionImageOpen.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (imageViewPronunciationPosition.getVisibility() == View.VISIBLE) {
                    imageViewPronunciationPosition.setVisibility(View.INVISIBLE);
                } else {
                    imageViewPronunciationPosition.setVisibility(View.VISIBLE);
                }
            }
        });

        // 발음 도움말(pronunciation help) Activity 로 전환
        buttonPronunciationHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PronunciationHelpActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
