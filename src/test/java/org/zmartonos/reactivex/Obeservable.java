package org.zmartonos.reactivex;

import org.testng.annotations.Test;

import rx.Observable;
import rx.Subscriber;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Test
public class Obeservable {

    @Test
    public void observableCreate() {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> observer) {
                try {
                    if (!observer.isUnsubscribed()) {
                        for (int i = 1; i < 5; i++) {
                            observer.onNext(i);
                        }
                        observer.onCompleted();
                    }
                } catch (Exception e) {
                    observer.onError(e);
                }
            }
        }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onNext(Integer item) {
                System.out.println("Next: " + item);
            }

            @Override
            public void onError(Throwable error) {
                System.err.println("Error: " + error.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Sequence complete.");
            }
        });
    }

    @Test
    public void observableJust() {
        Observable.just(1, 2, 3)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onNext(Integer item) {
                        System.out.println("Next: " + item);
                    }

                    @Override
                    public void onError(Throwable error) {
                        System.err.println("Error: " + error.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("Sequence complete.");
                    }
                });
    }

    @Test
    public void observableInterval(){
        Observable.interval(0, 0L, TimeUnit.SECONDS).asObservable()
                .subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {
                System.out.println("Value: ");
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onNext(Long aLong) {

            }
        });
    }

    @Test
    public void observableRange(){
        Observable.from(Arrays.asList("a", "b", "c", "a", "b", "c", "a", "b", "c", "a", "b", "c", "a", "b", "c", "a", "b", "c"))
                .groupBy(n -> n)
                .flatMap(g -> {
                    return g.take(3).reduce((s, s2) -> s + s2);
                }).forEach(System.out::println);
    }
}
