package pipelines;

import framework.LoopInterceptor;
import framework.Pipeline;

public class BasicTaskPipeline extends Pipeline {

    public BasicTaskPipeline(LoopInterceptor[] loopInterceptors) {
        super(loopInterceptors);
    }

}
