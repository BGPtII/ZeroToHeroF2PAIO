package nodes;

import framework.Node;
import framework.SCScript;

public class SpinningBallsOfWool implements Node {

    @Override
    public int loop() {


        return SCScript.SECURE_RANDOM.nextInt(800 - 400 + 1) + 400;
    }

}
