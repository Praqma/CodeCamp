/*
 * The MIT License
 *
 * Copyright 2014 Mads.
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

import hudson.Extension;
import hudson.model.TaskListener;
import hudson.model.AbstractBuild;
import hudson.model.listeners.RunListener;

import java.net.UnknownHostException;
import java.util.Collection;

/**
 *
 * @author Mads
 */
@Extension
public class GuessingRunListener extends RunListener<AbstractBuild<?,?>> {

    @Override
    public void onCompleted(AbstractBuild<?,?> r, TaskListener tl) {
        try {
            Collection<GuessingBuildAction> actions = r.getActions(GuessingBuildAction.class);
            GuessingDataStorageProvider storage = GuessingDataStorageProvider.getInstance();
			for (GuessingBuildAction action : actions) {
                storage.store(action);
                tl.getLogger().println(action);
			}
            tl.getLogger().println(String.format("%s answers stored in total", storage.count()));
            tl.getLogger().println(String.format("%s correct answers", storage.countCorrect()));
            tl.getLogger().println(String.format("%s incorrect answers", storage.countIncorrect()));
            super.onCompleted(r, tl);
        } catch (UnknownHostException ex) {
            ex.printStackTrace(tl.getLogger());
        }
    }

}
