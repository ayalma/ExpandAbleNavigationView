package ayalma.ir.epandablenavigationview;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * space itemDecoration class . that implement spacing gor @link{ayalma.ir.epandablenavigationview.NavigationView}
 * Created by alimohammadi on 5/5/16.
 *
 * @author alimohammadi.
 */
class SpaceItemDecoration extends RecyclerView.ItemDecoration
{
    private int space;

    public SpaceItemDecoration(int space)
    {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        if (parent.getChildAdapterPosition(view) == 0)
        {
            outRect.top =  space;
            outRect.bottom =0;

        }

    }
}
