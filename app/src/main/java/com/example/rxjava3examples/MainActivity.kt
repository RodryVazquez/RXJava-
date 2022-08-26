package com.example.rxjava3examples

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.rxjava3examples.databinding.ActivityMainBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.functions.Action
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

const val TAG: String = "MAIN_ACTIVITY"

class MainActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCreateObservable.setOnClickListener {
            callObservableCreate()
        }

        binding.btnJustObservable.setOnClickListener {
            callObservableJust()
        }

        binding.btnIterableObservable.setOnClickListener {
            callObservableIterable()
        }

        binding.btnRangeObservable.setOnClickListener {
            callObservableRange()
        }

        binding.btnIntervalObservable.setOnClickListener {
            callObservableInterval()
        }

        binding.btnTimerObservable.setOnClickListener {
            callObservableTimer()
        }

        binding.btnCompletableObservable.setOnClickListener {
            callCompletableObservable()
        }

        binding.btnSingleObservable.setOnClickListener {
            callSingleObservable()
        }

        binding.btnMaybeObservable.setOnClickListener {
            callMaybeObservable()
        }

        binding.btnSubscribeOn.setOnClickListener {
            subscribeOnExample()
        }

        binding.btnObserveOn.setOnClickListener {
            observeOnExample(binding)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    private fun callObservableCreate(
    ) {
        val observable = Observable.create<Int> { emitter ->
            emitter.onNext(1)
            emitter.onNext(2)
            emitter.onNext(3)
            emitter.onComplete()
        }
        val disposable = observable.subscribe({ item ->
            Log.i(TAG, "$item")
        }, { throwable ->
            Log.e(TAG, "${throwable.message}")
        }, {
            Log.i(TAG, "OnCompleted")
        })

        compositeDisposable.add(disposable)
    }

    /**
     * We can emit at max 10 items
     */
    private fun callObservableJust() {
        val justObservable = Observable.just("1 just", "2 just", "3 just")
        justObservable.subscribe({ item ->
            println(item)
        }, { error ->
            println(error.message)
        }, { println("OnCompleted") }
        )
    }

    /**
     * Emit items one by one
     */
    private fun callObservableIterable() {
        val list = arrayListOf("1 iterable", "2 iterable")
        val iterableObservable = Observable.fromIterable(list)
        iterableObservable.subscribe({ item ->
            println(item)
        }, { error -> println(error) },
            { println("OnCompleted") })

    }

    private fun callObservableRange() {
        val rangeObservable = Observable.range(2, 5)
        rangeObservable.subscribe({ item ->
            println(item)
        }, { error -> println(error) },
            { println("OnCompleted") })
    }

    private fun callObservableInterval() {
        val observableInterval = Observable.interval(2, TimeUnit.SECONDS)
        observableInterval.subscribe({ item ->
            println(item)
        }, { error -> println(error) },
            { "OnCompleted" })
    }

    private fun callObservableTimer() {
        val timerObservable = Observable.timer(2, TimeUnit.SECONDS)
        timerObservable.subscribe(
            { item -> println(item) },
            { error -> println(error) },
            { println("OnCompleted") })
    }

    private fun callCompletableObservable() {
        val action = Action { println("Testing Observable") }
        val completable = Completable.fromAction(action)
        completable.subscribe { println("Completed!") }
    }

    //////////////////////////////////////////////SINGLE
    private fun callSingleObservable() {
        val singleObservable = Single.create<String> { emitter ->
            val user = fetchDummyUser()
            if (user.isEmpty()) {
                emitter.onSuccess(user)
            } else {
                emitter.onError(Exception("Single Error!!"))
            }
        }

        val disposable = singleObservable.subscribe({ item ->
            println(item)
        }, { error -> println(error) })
        compositeDisposable.add(disposable)
    }

    private fun fetchDummyUser(): String {
        return "John Doe"
    }

    /////////////////////////////////////////////////MAYBE
    private fun callMaybeObservable() {
        val maybeObservable = Maybe.create<String> { emitter ->
            val data = readFile()
            if (data.isEmpty()) {
                emitter.onComplete()
            } else {
                emitter.onSuccess(data)
            }
        }

        val disposable = maybeObservable.subscribe({ item ->
            println(item)
        }, { error -> println(error) }, { println("OnCompleted") })
        compositeDisposable.add(disposable)
    }

    private fun readFile(): String {
        return "Dummy Content"
    }

    //////////////////////////////////////////////////SUBSCRIBE ON

    private fun subscribeOnExample() {
        Observable.just(1, 2, 3, 4, 5)
            .subscribeOn(Schedulers.computation())
            .doOnNext { item ->
                println("Pushing Item " + item + "on" + Thread.currentThread().name + "Thread")
            }
            .subscribe { item ->
                println("Received Item" + item + "on" + Thread.currentThread().name + "Thread")
            }
    }

    private fun observeOnExample(binding: ActivityMainBinding) {
        Observable.just(1, 2, 3, 4, 5)
            .subscribeOn(Schedulers.io())
            .doOnNext { item ->
                println("Pushing Item " + item + "on" + Thread.currentThread().name + "Thread")
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { item ->
                binding.btnObserveOn.text = "Observe On $item"
                println("Received Item" + item + "on" + Thread.currentThread().name + "Thread")
            }
    }



}