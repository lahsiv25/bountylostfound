package com.vishal.bountylostfound.fragments

import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.vishal.bountylostfound.R
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator





abstract class SwipeToDelete(context: Context):ItemTouchHelper.SimpleCallback
    (0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    val delColor = ContextCompat.getColor(context, R.color.red)
    val editColor = ContextCompat.getColor(context, R.color.salmonpinkSecondarylight)
    val delIcon = R.drawable.ic_baseline_delete_outline_24
    val editIcon = R.drawable.ic_baseline_edit_note_24

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        RecyclerViewSwipeDecorator.Builder(
            c,
            recyclerView,
            viewHolder,
            dX,
            dY,
            actionState,
            isCurrentlyActive
        )
            .addSwipeLeftBackgroundColor(delColor)
            .addSwipeLeftActionIcon(delIcon)
            .addSwipeLeftLabel("Delete")
            .addSwipeRightBackgroundColor(editColor)
            .addSwipeRightActionIcon(editIcon)
            .addSwipeRightLabel("Edit")
            .create()
            .decorate()
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}