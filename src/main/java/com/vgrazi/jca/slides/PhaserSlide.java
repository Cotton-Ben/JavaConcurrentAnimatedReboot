package com.vgrazi.jca.slides;

import com.vgrazi.jca.sprites.ThreadSprite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.Phaser;

@Component
public class PhaserSlide extends Slide {

    @Autowired
    private ApplicationContext applicationContext;

    private Phaser phaser = new Phaser(4);

    public void run() {
        reset();
        threadContext.addButton("awaitAdvance()", ()->{
            ThreadSprite sprite = (ThreadSprite) applicationContext.getBean("threadSprite");
            sprite.setAction("awaitAdvance");
            setCssSelected("awaitAdvance");
            addRunnable(phaser, sprite);
        });

        threadContext.addButton("arrive()", ()->{
            ThreadSprite sprite = (ThreadSprite) applicationContext.getBean("threadSprite");
            sprite.setAction("arrive");
            setCssSelected("arrive");
            addRunnable(phaser, sprite);
        });

        threadContext.addButton("arriveAndAwaitAdvance()", ()->{
            ThreadSprite sprite = (ThreadSprite) applicationContext.getBean("threadSprite");
            sprite.setAction("arriveAndAwaitAdvance");
            setCssSelected("arriveAndAwaitAdvance");
            addRunnable(phaser, sprite);
        });

        threadContext.addButton("register()", ()->{
            // todo: register should set a message on the UI message area, indicating the number
            //  of permits. No need to create a thread
            ThreadSprite sprite = (ThreadSprite) applicationContext.getBean("threadSprite");
            sprite.setAction("register");
            setCssSelected("register");
            addRunnable(phaser, sprite);
        });

        threadContext.addButton("reset", this::reset);
        threadContext.setVisible();
    }

    public void reset() {
        super.reset();
        threadContext.setSlideLabel("Phaser");
        phaser = new Phaser(4);
        setSnippetFile("phaser.html");
    }

    private void addRunnable(Phaser phaser, ThreadSprite sprite) {
        sprite.attachAndStartRunnable(() -> {
            int phase =0;
            while (sprite.isRunning()) {
                if ("release".equals(sprite.getAction())) {
                    threadContext.stopThread(sprite);
                    break;
                }
                switch (sprite.getAction()) {
                    case "awaitAdvance":
                        phase = phaser.awaitAdvance(phase);
                        setMessage("Phase: " + phase);
                        sprite.setAction("release");
                        break;
                    case "arrive":
                        phase = phaser.arrive();
                        setMessage("Phase: " + phase);
                        sprite.setAction("release");
                        break;
                    case "arriveAndAwaitAdvance":
                        phase = phaser.arriveAndAwaitAdvance();
                        setMessage("Phase: " + phase);
                        sprite.setAction("release");
                        break;
                    case "register":
                        phase = phaser.register();
                        setMessage("Phase: " + phase);
                        sprite.setAction("release");
                        break;
                    case "default":
                        Thread.yield();
                        break;
                }
            }
            System.out.println(sprite + " exiting");
        });
        threadContext.addSprite(sprite);
    }
}
