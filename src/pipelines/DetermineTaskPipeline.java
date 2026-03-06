package pipelines;

import framework.LoopInterceptor;
import framework.Pipeline;
import loopinterceptors.DetermineTaskLI;

public class DetermineTaskPipeline extends Pipeline {

    public DetermineTaskPipeline() {
        super(new LoopInterceptor[] { new DetermineTaskLI()});
    }

    @Override
    public int run() {
        return loopInterceptors[0].handle();
    }

    @Override
    public void shuffleLoopInterceptors() {

    }
}
