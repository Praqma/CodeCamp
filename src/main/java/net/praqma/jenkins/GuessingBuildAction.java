/*
 * The MIT License
 *
 * Copyright 2013 Praqma.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.praqma.jenkins;

import hudson.model.Action;


/**
 *
 * A class representing an action performed in a build step(It can be used in all parts of the build).
 * These actions are added to the build. These actions can contain business logic, data etc.
 * Builds can have multiple actions of the same type.
 *
 * This data can the be extracted for use in the various views that Jenkins offers.
 *
 * In our example we will re-use the same action through the entire build pipeline.
 *
 * @author Praqma
 */
public class GuessingBuildAction implements Action {

	private int index;
    private int guess;
    private int number;
    private boolean correct;

    public GuessingBuildAction() { }

    public GuessingBuildAction(int index, int guess, int number, boolean correct) {
    	this.index = index;
        this.guess = guess;
        this.number = number;
        this.correct = correct;
    }

     /**
     *
     * @return the path to the icon file to be used by Jenkins. If null, no link will be generated
     */
    @Override
    public String getIconFileName() {
        return "/plugin/guessing-game/images/64x64/guess.png";
    }

    @Override
    public String getDisplayName() {
        return "Guess "+index;
    }

    @Override
    public String getUrlName() {
        return "guess" + index;
    }

    /**
     * @return the correct
     */
    public boolean isCorrect() {
        return correct;
    }

    /**
     * @param correct the correct to set
     */
    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    /**
     * @return the guess
     */
    public int getGuess() {
        return guess;
    }

    /**
     * @param guess the guess to set
     */
    public void setGuess(int guess) {
        this.guess = guess;
    }

    /**
     * @return the number
     */
    public int getNumber() {
        return number;
    }

    /**
     * @param number the number to set
     */
    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return String.format("%s - %s - %s", guess, number, correct);
    }

}
