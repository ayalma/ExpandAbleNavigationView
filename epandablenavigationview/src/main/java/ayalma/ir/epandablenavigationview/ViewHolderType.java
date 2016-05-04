package ayalma.ir.epandablenavigationview;

/**
 * Created by alimohammadi on 5/3/16.
 *
 * @author alimohammadi.
 */
public enum ViewHolderType
{
    Header(1),Group(2),Item(3);

    private int value;

    ViewHolderType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
