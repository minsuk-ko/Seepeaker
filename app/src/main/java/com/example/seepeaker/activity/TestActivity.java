package com.example.seepeaker.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;
import androidx.core.app.ActivityCompat;

import com.example.seepeaker.R;
import com.example.seepeaker.model.Question;
import com.example.seepeaker.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class TestActivity extends AppCompatActivity {
    private ImageButton imageButton;
    TextView score;
    private Handler progressHandler;
    private Runnable progressRunnable;
    private ProgressBar progressBar;
    private TextView textViewRecordingCheckText;
    private TextView textViewSttResult;
    private TextView textViewQuestions;
    private boolean isTextVisible = false;
    private Handler blinkingHandler = new Handler();
    private SpeechRecognizer mRecognizer;
    private Intent intent;
    private final int PERMISSION = 1;
    private List<String> wordList;
    private List<String> selectedWords = new ArrayList<>(); // 선택된 word 저장용
    private List<String> wordIdList = new ArrayList<>(); // wordID 저장용
    private List<String> selectedWordId = new ArrayList<>(); // 선택된 wordID 저장용
    private List<String> spokenWords = new ArrayList<>(); // 발음한 단어 저장용
    private List<Boolean> answerResults = new ArrayList<>(); // 발음한 결과 저장용
    FirebaseUser userAccount = FirebaseAuth.getInstance().getCurrentUser(); // 로그인된 유저 정보 가져오기
    FirebaseFirestore firebaseDB = FirebaseFirestore.getInstance();
    String userId; //유저id
    private long avgScore = 0; // 평균점수
    private long maxScore = 0; // 최고점수
    private long minScore = 0; // 최저점수
    private long testTime = 0; // 시험횟수
    private int spokenWordCount = 0;
    boolean isRecognitionError = false; // 음성인식 오류 확인값, 기본 값은 false. 즉, 오류 없음.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        fetchWordList();
        fetchUserInfo();

        imageButton = findViewById(R.id.imageButton);
        progressBar = findViewById(R.id.progressBar);
        textViewRecordingCheckText = findViewById(R.id.recordingCheckText);
        textViewSttResult = findViewById(R.id.userAnswer);
        textViewQuestions = findViewById(R.id.pronunciationQuestion);
        score = findViewById(R.id.score);

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
                textViewSttResult.setText("");
                imageButton.setVisibility(View.GONE); // 이미지 버튼 숨기기
                textViewRecordingCheckText.setVisibility(View.VISIBLE); // "듣고있어요" 보이기
                startBlinkingText(); // 0.5초마다 "듣고있어요" 텍스트 깜빡이게 하기
                spokenWordCount++;
                score.setText(spokenWordCount + "/10");
                startPractice();
            }
        });
    }
    private void fetchWordList() {
        // 여기에 Firestore에서 단어 목록을 가져오는 코드를 구현해야 합니다.
        // Firestore에서 데이터를 읽어와 wordList에 저장하는 부분
        CollectionReference wordsCollection = firebaseDB.collection("Words");
        wordsCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    wordList = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        String word = documentSnapshot.getString("word");
                        if (word != null) {
                            wordList.add(word);
                        }
                        String wordId = documentSnapshot.getString("wordId");
                        if (wordId != null) {
                            wordIdList.add(wordId);
                        }
                    }
                } else {
                    // 데이터가 없는 경우에 대한 처리
                }
            }
        });
        // Firestore에서 데이터를 읽어오는 방법은 Firestore 문서를 참조하여 구현해야 합니다.
        // 예시로 고정된 단어 목록을 사용하겠습니다.
        // firestore.collection("words").get() 등을 사용하여 데이터를 가져옵니다.
        // 가져온 데이터를 wordList에 저장합니다.
    }

    private void fetchUserInfo() {
        if (userAccount != null) {
            userId = userAccount.getUid();
            CollectionReference wordsCollection = firebaseDB.collection("Users");
            wordsCollection.whereEqualTo("userId", userId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            avgScore = documentSnapshot.getLong("avgScore"); // firestore의 유형 number는 long 형식임.
                            maxScore = documentSnapshot.getLong("maxScore"); // firestore의 유형 number는 long 형식임.
                            minScore = documentSnapshot.getLong("minScore"); // firestore의 유형 number는 long 형식임.
                            testTime = documentSnapshot.getLong("testTime"); // firestore의 유형 number는 long 형식임.
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
        }
    }


    private void startPractice() {
        int recordingDuration = 8000;

        // Stop the existing progress bar and blinking text
        stopProgressBar();
        stopBlinkingText();

        // ProgressBar 표시
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        mRecognizer.setRecognitionListener(listener); // Listener 등록
        mRecognizer.startListening(intent);

        // 기존 진행 중인 프로그래스 바 초기화
        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);
        textViewRecordingCheckText.setVisibility(View.GONE);

        // 새로운 프로그래스 바 시작
        simulateProgressBarProgress();

        if (!isRecognitionError) {
            // 음성 인식이 오류가 아닌 경우만(즉, isRecognitionError = false) 다음 단어를 가져옴.
            int randomIndex;
            if (wordList != null && wordList.size() >= 10) {
                while (selectedWords.size() < 10) {
                    randomIndex = new Random().nextInt(wordList.size());
                    String randomWord = wordList.get(randomIndex);
                    if (!selectedWords.contains(randomWord)) {
                        selectedWords.add(randomWord);
                        textViewQuestions.setText(randomWord);
                        selectedWordId.add(wordIdList.get(randomIndex));
                        break;
                    }
                }
            } else {
                Toast.makeText(this, "죄송합니다. 단어 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // 음성 인식이 오류인 경우(즉, isRecognitionError = true) 다음 단어를 가져오지 않고 현재 단어를 다시 반복
            isRecognitionError = false; // 오류 확인값을 기본값으로 되돌려줌.
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
            progressBar.setVisibility(View.GONE);
            if (imageButton.getVisibility() == View.INVISIBLE || imageButton.getVisibility() == View.GONE) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imageButton.setVisibility(View.VISIBLE);
                    }
                }, 3000);
            }


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
            Toast.makeText(getApplicationContext(), "에러: " + message,Toast.LENGTH_SHORT).show();
            isRecognitionError = true; // 음성 인식기가 오류가 발생하여, 오류 인식값을 true로 바꿔줌.
            spokenWordCount--;
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


            //Toast.makeText(getApplicationContext(), "avgScore: " + avgScore, Toast.LENGTH_SHORT).show();
            //Toast.makeText(getApplicationContext(), "maxScore: " + maxScore, Toast.LENGTH_SHORT).show();
            //Toast.makeText(getApplicationContext(), "minScore: " + minScore, Toast.LENGTH_SHORT).show();
            //Toast.makeText(getApplicationContext(), "testTime: " + testTime, Toast.LENGTH_SHORT).show();

            // 모든 단어를 말한 경우
            if (spokenWords.size() == 10) {

                long score = 0;
                if(testTime == 0){
                    for(int i = 0; i < 10; i++){
                        if(answerResults.get(i)){
                            score = score + 10;
                        }
                    }
                    avgScore = score;
                    minScore = score;
                    maxScore = score;
                    testTime = 1;
                }
                else {
                    for(int i = 0; i < 10; i++){
                        if(answerResults.get(i)){
                            score = score + 10;
                        }
                    }
                    if(score > maxScore){
                        maxScore = score;
                    }
                    else if (score < minScore){
                        minScore = score;
                    }
                    avgScore = ((avgScore * testTime) + score) / (testTime + 1); // ( (지난 시험까지의 총점) + (이번 시험점수) ) / (시험 횟수)
                    testTime = testTime + 1;
                }
                CollectionReference CollectionReference = firebaseDB.collection("Users");
                CollectionReference.document(userId).update("testTime",testTime);
                CollectionReference.document(userId).update("avgScore",avgScore);
                CollectionReference.document(userId).update("maxScore",maxScore);
                CollectionReference.document(userId).update("minScore",minScore);

                for(int i = 0; i < 10; i++){
                    Question newQuestion = new Question();
                    newQuestion.setWord(selectedWords.get(i));
                    newQuestion.setUserId(userId);
                    newQuestion.setWordId(selectedWordId.get(i));
                    newQuestion.setTestId(testTime);
                    newQuestion.setIsCorrect(answerResults.get(i));
                    firebaseDB.collection("Questions").document().set(newQuestion);
                }
                Intent intent = new Intent(TestActivity.this, TestResultActivity.class);
                intent.putExtra("testId",testTime);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Activity 꼬임? 방지 - 자세한 내용은 직접 찾아 볼것.
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // Activity 가 스택에 남아 있으면 onCreate 를하지 않음.
                startActivity(intent);
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
        progressHandler = new Handler();
        progressRunnable = new Runnable() {
            int progress = 0;

            @Override
            public void run() {
                if (progress < maxProgress) {
                    int secondsRemaining = (maxProgress - progress) / 1000;
                    textViewRecordingCheckText.setText("Recording " + secondsRemaining + "s"); // 카운트 다운
                    progress += progressIncrement;
                    progressBar.setProgress(progress);
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
    private void stopProgressBar() {
        if (progressHandler != null) {
            progressHandler.removeCallbacks(progressRunnable);
        }
    }


}
