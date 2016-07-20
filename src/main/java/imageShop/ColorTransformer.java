package imageShop;

import javafx.scene.paint.Color;

/**
 * Created by RAM0N on 7/19/16.
 */
interface ColorTransformer {
    Color apply(int x, int y, Color colorAtXY);
}
