package ayalma.ir.epandablenavigationview;

/**
 * holder type enum.
 * Created by alimohammadi on 5/3/16.
 *
 * @author alimohammadi.
 */
enum ViewType {
    /**
     * header view type
     */
    Header(1),
    /**
     * group view type
     */
     Group(2),
    /**
     * item view type
     */
    Item(3);

    private int value;

    /**
     * @param value value of enum.
     */
    ViewType(int value) {
        this.value = value;
    }

    /**
     * @return value of enum.
     */
    public int getValue() {
        return value;
    }
}
