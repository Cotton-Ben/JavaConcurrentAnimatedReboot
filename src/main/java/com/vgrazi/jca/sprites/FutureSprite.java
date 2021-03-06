package com.vgrazi.jca.sprites;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

import static com.vgrazi.jca.util.Parsers.parseColor;
import static com.vgrazi.jca.util.Parsers.parseFont;

/**
 * A ThreadSprite represents one thread, and retains all of the state related to that thread,
 * including the Java thread itself, the shape, xPosition, and the action, which is called by slide,
 * and is used to change the state
 * Note: We should really create the thread in the constructor, but its Runnable needs access to this class's
 * running flag. So construct the sprite, then add the Runnable.
 */
public class FutureSprite extends Sprite implements InitializingBean {
    private Color futureDefaultColor;
    private Color futureDoneColor;
    private Color futureTextColor;
    private Font futureTextFont;
    @Value("${monolith-left-border}")
    private int leftBorder;

    @Value("${monolith-right-border}")
    private int rightBorder;

    @Value("${future-width}")
    private int width;

    private CompletableFuture future;
    @Value("${future-height}")
    private int height;

    public void setNextXPosition() {
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getYCenter() {
        return getYPosition() - getYMargin() + (height + getYMargin() * 2)/2;
    }

    @Override
    public void render(Graphics2D graphics) {
        if (future != null) {
            graphics.setColor(Color.black);
            graphics.drawRect(getXPosition() - getXMargin() , getYPosition() - getYMargin() -1, width + getXMargin() + getXRightMargin(), height + getYMargin() * 2);
            graphics.setColor(future.isDone() ? futureDoneColor : futureDefaultColor);
            graphics.fill3DRect(getXPosition() - getXMargin() , getYPosition() - getYMargin() -1, width + getXMargin() + getXRightMargin(), height + getYMargin() * 2, true);
            if(future.isDone()) {
                String value = String.valueOf(future.join());
                graphics.setColor(futureTextColor);
                graphics.setFont(futureTextFont);
                FontMetrics fm = graphics.getFontMetrics();
                int xDelta = (width + getXMargin() + getXRightMargin() - fm.stringWidth(value))/2;
                int yDelta = (this.height + getYMargin() * 2 - fm.getHeight())/2;
                graphics.drawString(value,getXPosition() - getXMargin() + xDelta,getYPosition() - getYMargin() -1 + this.height + yDelta);
            }
        }
    }

    @Value("${FUTURE-DEFAULT-COLOR}")
    private void setFutureDefaultColor(String color) {
        futureDefaultColor = parseColor(color);
    }

    @Value("${FUTURE-DONE-COLOR}")
    private void setFutureDoneColor(String color) {
        futureDoneColor = parseColor(color);
    }

    @Value("${FUTURE-TEXT-COLOR}")
    private void setFutureTextColor(String color) {
        futureTextColor = parseColor(color);
    }

    @Value("${FUTURE-TEXT-FONT}")
    private void setFutureTextFont(String font) {
        futureTextFont = parseFont(font);
    }

    @Override
    public void afterPropertiesSet() {
        setXPosition((rightBorder + leftBorder - width) / 2);
    }

    public int getHeight() {
        return height;
    }

    /**
     * Override the default height
     * @param height
     */
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "FutureSprite{" +
                "ID=" + getID() +
//                ", x-position=" + xPosition +
//                ", y-position=" + yPosition +
                ", relative_position=" + getRelativePosition() +
                '}';
    }

    public CompletableFuture getFuture() {
        return future;
    }

    public void setFuture(CompletableFuture future) {
        this.future = future;
    }

    /**
     * Returns true of this future has been marked as complete
     *
     * @return
     */
    public boolean isDone() {
        return future.isDone();
    }
}
