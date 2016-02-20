package com.flybbird.hellorxandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.flybbird.hellorxandroid.data.WeatherApi;
import com.flybbird.hellorxandroid.data.WeatherEntity;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.jakewharton.rxbinding.widget.CompoundButtonCheckedChangeEvent;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.converter.GsonConverter;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.PUSH_BUTTON) Button push;
    @Bind(R.id.RESULT_TEXTVIEW) TextView result;

    // EDIT
    @Bind(R.id.INPUT_EDITTEXT) EditText inputText;
    @Bind(R.id.RESULT_TEXTVIEW2) TextView result2;


    // COMBO
    @Bind(R.id.TEST_CHECKBOX)
    CheckBox testCheckbox;

    @Bind(R.id.RESULT_TEXTVIEW4)
    TextView resutl4;



    private Observable<CharSequence> _changTextObservable;
    private Observable<CompoundButtonCheckedChangeEvent> _checkedChangeObservable;
    private ConnectableObservable<CompoundButtonCheckedChangeEvent> _connectableObservable;
    private int totalCount = 0;

    private WeatherApi _weatherApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

// flybbird 20160219 Rxbinding 쓰고 .. 안쓰고 적용해보자
//        _changTextObservable = RxTextView.textChanges(inputText).skip(1);
//        _changTextObservable.subscribe(new Action1<CharSequence>() {
//            @Override
//            public void call(CharSequence charSequence) {
//                     result2.setText(charSequence.toString());
//            }
//        });

        _checkedChangeObservable = RxCompoundButton.checkedChangeEvents(testCheckbox).skip(1);
        _connectableObservable = _checkedChangeObservable.publish();

        _connectableObservable.subscribe(checkedChangeEventSubscribe1);
        _connectableObservable.subscribe(checkedChangeEventSubscribe2);
        _connectableObservable.connect();


        initRetrofit();
//        connectableObservable.subscribe(checkedChangeEventSubscribe1);

//        subscriberArrayList.add(item1);

//        _checkedChangeObservable.subscribe(checkedChangeEventSubscribe2);
//        _checkedChangeObservable.subscribe(new Action1<CompoundButtonCheckedChangeEvent>() {
//            @Override
//            public void call(CompoundButtonCheckedChangeEvent compoundButtonCheckedChangeEvent) {
//                Boolean isChekced = compoundButtonCheckedChangeEvent.isChecked();
//
//                Log.d("DEBUG","# ISCHECKED=" + isChekced);
//
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);


        if ( !checkedChangeEventSubscribe2.isUnsubscribed() )
            checkedChangeEventSubscribe2.unsubscribe();;
    }

    @OnClick(R.id.PUSH_BUTTON)
    public void clikedPushButton(Button button) {
        Log.d("DEBUG", "Push Push");

        Observable.just(totalCount)
                .map(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        totalCount++;
                        return totalCount+"";
                    }
                })
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.d("DEBUG", "* onComplted!!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("DEBUG", "* onError!! ");
                    }

                    @Override
                    public void onNext(String s) {
                        //    Log.d("DEBUG", "* onNext Integer = " + integer);
                        result.setText(s);
                    }


                });
    }

    @OnClick(R.id.NETWORK_BUTTON)
    public void clickedNetworkButton(Button button){

        final Observer<WeatherEntity> subscriber = new Observer<WeatherEntity>() {
            @Override
            public void onCompleted() {
                Log.d("DEBUG", "onCompleted() ");
            }

            @Override
            public void onError(Throwable e) {
                Log.d("DEBUG", "onError() Error=" + e.toString());
            }

            @Override
            public void onNext(WeatherEntity weatherEntity) {
                //Log.d("DEBUG", "onNext() =" + weatherEntity.weather.get(0).main);
                WeatherEntity.Weather weather = weatherEntity.weather.get(0);
                String showText = weather.main + "/" + weather.description + "/" + weather.icon;

                resutl4.setText(showText);
            }
        };

        _weatherApi.get("weather","Seoul", "18efefa96be311d832f357d0b54393b7")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }



    public void initRetrofit(){
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint("http://api.openweathermap.org")
                .setConverter(new GsonConverter(gson))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new AndroidLog("DEBUG"))
                .build();

        _weatherApi = adapter.create(WeatherApi.class);


//        adapter.create(WeatherApi.class).get("weather", "Seoul")
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<WeatherEntity>() {
//                    @Override
//                    public void onCompleted() {
//                        Log.d("DEBUG", "onCompleted()");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e("DEBUG", "Error : " + e.toString());
//                    }
//
//                    @Override
//                    public void onNext(WeatherEntity weather) {
//                        Log.d("DEBUG", "onNext() =" + weather.weather.get(0).main);
//                        if (weather != null) {
//                            //((TextView) findViewById(R.id.text)).setText(weather.weather.get(0).main);
//                        }
//                    }
//                });
    }




//    @OnTextChanged(R.id.INPUT_EDITTEXT)
//    public void onTextChanged(CharSequence text) {
//        Log.d("DEBUG","# onTextChanged =" + text);
//
//
//
//    }



//    private Action1<CompoundButtonCheckedChangeEvent> checkedChangeEventSubscribe1 = new Action1<CompoundButtonCheckedChangeEvent>() {
//        @Override
//        public void call(CompoundButtonCheckedChangeEvent compoundButtonCheckedChangeEvent) {
//            Log.d("DEBUG","+ checkedChangeEventSubscribe1 = " + compoundButtonCheckedChangeEvent.isChecked());
//        }
//    };
//
//
//    private Action1<CompoundButtonCheckedChangeEvent> checkedChangeEventSubscribe2 = new Action1<CompoundButtonCheckedChangeEvent>() {
//        @Override
//        public void call(CompoundButtonCheckedChangeEvent compoundButtonCheckedChangeEvent) {
//            Log.d("DEBUG","+ checkedChangeEventSubscribe2 = " + compoundButtonCheckedChangeEvent.isChecked());
//        }
//    };


    private Subscriber<CompoundButtonCheckedChangeEvent> checkedChangeEventSubscribe1  = new Subscriber<CompoundButtonCheckedChangeEvent>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(CompoundButtonCheckedChangeEvent compoundButtonCheckedChangeEvent) {
            Log.d("DEBUG","* checkedChangeEventSubscribe1 =" + compoundButtonCheckedChangeEvent.isChecked());
        }
    };


    private Subscriber<CompoundButtonCheckedChangeEvent> checkedChangeEventSubscribe2  = new Subscriber<CompoundButtonCheckedChangeEvent>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(CompoundButtonCheckedChangeEvent compoundButtonCheckedChangeEvent) {
            Log.d("DEBUG","* checkedChangeEventSubscribe2 =" + compoundButtonCheckedChangeEvent.isChecked());
        }
    };


}
