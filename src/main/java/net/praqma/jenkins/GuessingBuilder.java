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

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.ParametersAction;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/**
 * The Builder for our project. We extend from Builder which implements BuildStep,
 * which has a perform method we will use. Additionally all {@link hudson.tasks.BuildStep}s have a prebuild method to
 * override, which runs before the build step.
 *
 * The main purpose of this example builder is to extract environment information from the executing slaves, and make
 * this data available for the view we wish to present it in.
 *
 * During execution we re-use already added actions, and add the discovered data to the already existing build action.
 *
 * @author Praqma
 */
public class GuessingBuilder extends Builder {

	public final Integer lower,upper;

    /**
     * Required static constructor. This is used to create 'One Project Builder' BuildStep in the list-box item on your jobs
     * configuration page.
     */
    @Extension
    public static class GuessingBuilderImpl extends BuildStepDescriptor<Builder> {

        /**
         * This is used to determine if this build step is applicable for your chosen project type. (FreeStyle, MultiConfiguration, Maven)
         * Some plugin build steps might be made to be only available to MultiConfiguration projects.
         *
         * Required. In our example we require the project to be a free-style project.
         *
         * @param proj The current project
         * @return a boolean indicating whether this build step can be chose given the project type
         */
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> proj) {
            return true;
        }
        /**
         * Required method.
         *
         * @return The text to be displayed when selecting your BuildStep, in the project
         */
        @Override
        public String getDisplayName() {
            return "Guess a number";
        }
        
        public FormValidation doCheckUpper(@QueryParameter Integer upper) {
            if(upper != null && upper > 6) {
                return FormValidation.error("Has to be between 1 and 6");
            }
            return FormValidation.ok();
        }
    }

    @DataBoundConstructor
    public GuessingBuilder(final Integer lower, final Integer upper) {
        this.upper = upper;
        this.lower = lower;
    }

    /**
     * Override this method to get your operation done in the build step. When invoked, it is up to you, as a plugin developer
     * to add your actions, and/or perform the operations required by your plugin in this build step. Equally, it is up
     * to the developer to make the code run on the slave(master or an actual remote). This must be done given the builds
     * workspace, as in build.getWorkspace(). The workspace is the link to the slave, as it is the representation of the
     * remotes file system.
     *
     * Build steps as you add them to your job configuration are executed sequentially, and the return value for your
     * builder should indicate whether to execute the next build step in the list.
     *
     * @param build the current build
     * @param launcher the current launcher
     * @param listener the build listener
     * @return a boolean indicating wheather to proceed with the next buildstep
     * @throws InterruptedException
     * @throws IOException
     */
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        listener.getLogger().println(String.format("Guessing a number between %s and %s", lower, upper));

        int guess = -1;

        List<ParametersAction> actions = build.getActions(ParametersAction.class);

        if(actions.isEmpty()) {
            guess = new Random().nextInt(upper - lower + 1)+lower;
        } else {
            for(ParametersAction act : actions) {
                guess = Integer.parseInt(act.getParameter("guess").createVariableResolver(build).resolve("guess"));
            }
        }

        int random = new Random().nextInt(upper - lower + 1)+lower;

        //Add the action to jenkins. This way we can reuse the data.
        int currentGuessingBuildActions = build.getActions(GuessingBuildAction.class).size();
        build.addAction(new GuessingBuildAction(currentGuessingBuildActions + 1, guess, random, guess == random));

        // Add a GuessingRecorder if not already done
        AbstractProject<?,?> project = build.getProject();
		if(project.getPublishersList().getAll(GuessingRecorder.class).isEmpty()) {
			project.getPublishersList().add(new GuessingRecorder());
			project.save();
        }

        //return true (we summarize results in post build)
        return true;
    }

}
