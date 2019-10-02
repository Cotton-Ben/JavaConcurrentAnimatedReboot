package com.vgrazi.jca.slides;

import com.vgrazi.jca.context.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.vgrazi.jca.util.Logging.log;

@Component
public class CompletableFutureSlide extends Slide {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    ThreadContext threadContext;

    @Value("${completable-future-height}")
    private int completableFutureHeight;

    private final List<CompletableFuture> completableFutures = new ArrayList<>();

    /**
     * The first thread we create will be contained in the future
     * Then every time we create a new future, we set firstThread = null
     * so that the next thread will be the first thread, to be used in the next future
     */
    private ThreadSprite firstThread;
    private int threadCount;
    private final List<FutureSprite> bigFutures = new ArrayList<>();

    @Value("${future-top-margin}")
    private int futureTopMargin;
    @Value("${pixels-per-y-step}")
    private int pixelsPerYStep;

    public CompletableFutureSlide() {
    }

    public static void main(String[] args) {
        new CompletableFutureSlide().run();
    }

    public void run() {
//        try {
//            threadContext.colorByThreadInstance();

        threadContext.addButton("runAsync", () -> {
            ThreadSprite threadSprite = (ThreadSprite) applicationContext.getBean("threadSprite");
            if(firstThread == null) {
                firstThread = threadSprite;
            }
            threadCount++;
            // we need to create a future, a thread to attach to it, and sprites for each of those
            FutureSprite futureSprite = (FutureSprite) applicationContext.getBean("futureSprite");
            futureSprite.setYPosition(threadSprite.getYPosition() - futureTopMargin);
            // the holder contains the running status. When it is done, will be set to false
            threadSprite.setHolder(true);
            Runnable runnable = () -> {
                while (((Boolean) threadSprite.getHolder())) {
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                threadContext.stopThread(threadSprite);
            };
            CompletableFuture future = CompletableFuture.runAsync(runnable);
            completableFutures.add(future);
            futureSprite.setFuture(future);
            futureSprite.setHeight(completableFutureHeight);
            threadContext.addSprite(0, futureSprite);
            threadSprite.attachAndStartRunnable(() -> {
                while (threadSprite.isRunning()) {
                    Thread.yield();
                }
                System.out.println(threadSprite + " exiting");
            });
            threadContext.addSprite(threadSprite);
        });

        threadContext.addButton("CompletableFuture.allOf()", () -> {
            FutureSprite futureSprite = (FutureSprite) applicationContext.getBean("futureSprite");
            CompletableFuture future = CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0]));
            futureSprite.setFuture(future);
            futureSprite.setYPosition(firstThread.getYPosition());
            futureSprite.setHeight((threadCount-1) * pixelsPerYStep + futureTopMargin);
            int width = futureSprite.getWidth();
            futureSprite.setWidth(width +5);
            futureSprite.setYMargin(15);
            futureSprite.setXMargin(5);
            // waste a space
            threadContext.getNextYPosition();
//            futureSprite.setXRightMargin(0);
            firstThread = null;
            threadCount = 0;
            threadContext.addSprite(0, futureSprite);
            bigFutures.add(futureSprite);
        });
        threadContext.addButton("CompletableFuture.anyOf()", () -> {
            FutureSprite futureSprite = (FutureSprite) applicationContext.getBean("futureSprite");
            CompletableFuture future = CompletableFuture.anyOf(completableFutures.toArray(new CompletableFuture[0]));
            futureSprite.setFuture(future);
            futureSprite.setYPosition(firstThread.getYPosition());
            futureSprite.setHeight((threadCount-1) * pixelsPerYStep + futureTopMargin);
            int width = futureSprite.getWidth();
            futureSprite.setWidth(width +5);
            futureSprite.setYMargin(15);
            futureSprite.setXMargin(5);
            // waste a space
            threadContext.getNextYPosition();
//            futureSprite.setXRightMargin(0);
            firstThread = null;
            threadCount = 0;
            threadContext.addSprite(0, futureSprite);
            bigFutures.add(futureSprite);
        });


        threadContext.addButton("get()", () -> {
            if(!bigFutures.isEmpty()) {
                GetterThreadSprite getter = (GetterThreadSprite) applicationContext.getBean("getterSprite");
                FutureSprite futureSprite = bigFutures.remove(0);
                getter.setYPosition(futureSprite.getYCenter());
                getter.attachAndStartRunnable(()->{
                    CompletableFuture future = futureSprite.getFuture();
                    System.out.println("Getter attached to " + future);
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                });
                threadContext.addSprite(getter);
            }
        });

        threadContext.addButton("Done", () -> {
            ThreadSprite runningSprite = threadContext.getRunningThread();
            if (runningSprite != null) {
                runningSprite.setHolder(false);
//                runningSprite.setAction("release");
            }
            log("Set release on ", runningSprite);
        });

        threadContext.addButton("Reset",()-> threadContext.reset());

        threadContext.setVisible();

//            scheduledExecutor.schedule(()-> {
//                completableFuture1.complete("value1");
//                completableFuture2.complete("value2");
//            }, 2, TimeUnit.SECONDS);
//            log("getting allOf...");
//            CompletableFuture<Void> completableFuture = CompletableFuture.allOf(completableFuture1, completableFuture2);
//            completableFuture.get();
        log("got allOf...");
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
    }

}
