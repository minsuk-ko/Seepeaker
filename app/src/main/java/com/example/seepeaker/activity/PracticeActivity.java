package com.example.seepeaker.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;
import androidx.core.app.ActivityCompat;

import com.example.seepeaker.R;
import com.example.seepeaker.controller.UserController;
import com.example.seepeaker.model.Word;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

// 연습하기 부분 (MainActivity의 HomeFragment 부분의 "연습 하기"를 누르면 나오는 Activity)
public class PracticeActivity extends AppCompatActivity {
    private ImageButton imageButton;
    private ProgressBar progressBar;
    private TextView textViewRecordingCheckText;
    private TextView textViewSttResult;
    private TextView textViewQuestions;
    private boolean isTextVisible = false;
    private Handler blinkingHandler = new Handler();
    private SpeechRecognizer mRecognizer;
    private Intent intent;
    private final int PERMISSION = 1;
    private FirebaseFirestore firestore;
    private List<String> wordList;
    private List<String> spokenWords = new ArrayList<>();
    private List<Boolean> answerResults = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pratice);
        firestore = FirebaseFirestore.getInstance();
        fetchWordList();

        imageButton = findViewById(R.id.imageButton);
        progressBar = findViewById(R.id.progressBar);
        textViewRecordingCheckText = findViewById(R.id.recordingCheckText);
        textViewSttResult = findViewById(R.id.userAnswer);
        textViewQuestions = findViewById(R.id.pronunciationQuestion);

        progressBar.setMax(100);
        progressBar.setVisibility(View.GONE);
        textViewRecordingCheckText.setVisibility(View.GONE);

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO}, PERMISSION);
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButton.setVisibility(View.GONE); // 이미지 버튼 숨기기
                textViewRecordingCheckText.setVisibility(View.VISIBLE); // "듣고있어요" 보이기
                startBlinkingText(); // 0.5초마다 "듣고있어요" 텍스트 깜빡이게 하기
                startPractice();
            }
        });
    }
    private void fetchWordList() {
        // 여기에 Firestore에서 단어 목록을 가져오는 코드를 구현해야 합니다.
        // Firestore에서 데이터를 읽어와 wordList에 저장하는 부분
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference wordsCollection = db.collection("Words");
        wordsCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    wordList = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String word = documentSnapshot.getString("word");
                        if (word != null) {
                            wordList.add(word);
                        }
                    }
                } else {
                    // 데이터가 없는 경우에 대한 처리
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // 데이터를 가져오는 데 실패한 경우에 대한 처리
            }
        });
        // Firestore에서 데이터를 읽어오는 방법은 Firestore 문서를 참조하여 구현해야 합니다.
        // 예시로 고정된 단어 목록을 사용하겠습니다.
        // firestore.collection("words").get() 등을 사용하여 데이터를 가져옵니다.
        // 가져온 데이터를 wordList에 저장합니다.
    }


    private Set<String> selectedWords = new ArraySet<>();

    private void startPractice() {
        int recordingDuration = 8000;
        // ProgressBar 표시

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        mRecognizer.setRecognitionListener(listener); // Listener 등록
        mRecognizer.startListening(intent);
        progressBar.setProgress(0); // 프로그래스 바 초기값 설정
        progressBar.setVisibility(View.VISIBLE);
        simulateProgressBarProgress();

        int randomIndex;
        if (wordList != null && wordList.size() >= 10) {
            while (selectedWords.size() < 10) {
                randomIndex = new Random().nextInt(wordList.size());
                String randomWord = wordList.get(randomIndex);
                if (!selectedWords.contains(randomWord)) {
                    selectedWords.add(randomWord);
                    textViewQuestions.setText(randomWord);
                    break;
                }
            }
        } else {
            Toast.makeText(this, "단어 목록을 가져오지 못했거나 부족합니다.", Toast.LENGTH_SHORT).show();
        }


    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Toast.makeText(getApplicationContext(),"음성인식을 시작합니다.",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int error) {
            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }
            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message,Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            StringBuilder combinedMatches = new StringBuilder();
            for (String match : matches) {
                combinedMatches.append(match);
            }

            String question = textViewQuestions.getText().toString().replaceAll(" ", "");
            String answer = combinedMatches.toString().replaceAll(" ", "");

            boolean isCorrect = question.equals(answer);

            if (isCorrect) {
                showToast("정답!");
            } else {
                showToast("오답");
            }

            spokenWords.add(combinedMatches.toString());
            answerResults.add(isCorrect);
            textViewSttResult.setText(combinedMatches.toString());

            // 모든 단어를 말한 경우
            if (spokenWords.size() == 9) {
                // 이제 여기서 리스트뷰로 표시하거나 다른 화면으로 이동하는 로직을 추가하면 됩니다.
                Intent intent = new Intent(PracticeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        }

        // (나머지 RecognitionListener의 메서드들)

        @Override
        public void onPartialResults(Bundle partialResults) {}

        @Override
        public void onEvent(int eventType, Bundle params) {}
    };

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void startBlinkingText() {
        isTextVisible = true;
        blinkingHandler.post(blinkingRunnable);
    }

    private void stopBlinkingText() {
        isTextVisible = false;
        blinkingHandler.removeCallbacks(blinkingRunnable);
    }

    private Runnable blinkingRunnable = new Runnable() {
        @Override
        public void run() {
            if (isTextVisible) {
                textViewRecordingCheckText.setVisibility(View.INVISIBLE);
            } else {
                textViewRecordingCheckText.setVisibility(View.VISIBLE);
            }
            isTextVisible = !isTextVisible;
            blinkingHandler.postDelayed(this, 500);
        }
    };

    private void simulateProgressBarProgress() {
        final int updateInterval = 50; // 0.05초마다 업데이트
        final int maxProgress = 1000;
        final int progressIncrement = (maxProgress * updateInterval) / 8000;
        final Handler progressHandler = new Handler();
        final Runnable progressRunnable = new Runnable() {
            int progress = 0;

            @Override
            public void run() {
                if (progress < maxProgress) {
                    progress += progressIncrement;
                    progressBar.setProgress(progress);
                    textViewRecordingCheckText.setText("Recording " + (progress / 1000) + "s"); // 예시: "녹음 중 3초"
                    progressHandler.postDelayed(this, updateInterval);
                } else {
                    progressBar.setVisibility(View.GONE);
                    stopBlinkingText();
                    textViewRecordingCheckText.setVisibility(View.GONE);
                    imageButton.setVisibility(View.VISIBLE);
                    // 여기에서 다음 작업 수행
                    // 예를 들어, 결과를 처리하는 코드를 호출하거나 다음 단계로 이동하는 코드를 여기에 추가할 수 있습니다.
                }
            }
        };

        progressBar.setMax(maxProgress);
        progressHandler.post(progressRunnable);
    }

}
