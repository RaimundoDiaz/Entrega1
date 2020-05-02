package com.example.todoapp

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.DTO.ToDo
import kotlinx.android.synthetic.main.activity_dashboard.*
import java.util.*

class DashboardActivity : AppCompatActivity() {

    lateinit var dbHandler: DBHandler
    var list: MutableList<ToDo>? = null
    var adapter: DashboardAdapter? = null
    var touchHelper: ItemTouchHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dashboard)
        setSupportActionBar(dashboard_toolbar)
        title = "Dashboard"
        dbHandler = DBHandler(this)
        recyclerview_dashboard.layoutManager = LinearLayoutManager(this)

        buttonAddDashboard.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Add new list")
            val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
            val toDoName = view.findViewById<EditText>(R.id.editText_todo)
            dialog.setView(view)
            dialog.setPositiveButton("Add") { _: DialogInterface, _: Int ->
                if (toDoName.text.isNotEmpty()) {
                    val toDo = ToDo()
                    toDo.name = toDoName.text.toString()
                    dbHandler.addToDo(toDo)
                    refreshList()
                }
            }
            dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

            }
            dialog.show()
        }
        touchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
            override fun onMove(
                p0: RecyclerView,
                p1: RecyclerView.ViewHolder,
                p2: RecyclerView.ViewHolder
            ): Boolean {
                val sourcePosition = p1.adapterPosition
                val targetPosition = p2.adapterPosition
                Collections.swap(list, sourcePosition, targetPosition)
                adapter?.notifyItemMoved(sourcePosition, targetPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                TODO("Not yet implemented")
            }

        })
        touchHelper?.attachToRecyclerView(recyclerview_dashboard)
    }

    fun updateToDo(toDo: ToDo) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Edit")
        val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
        val toDoName = view.findViewById<EditText>(R.id.editText_todo)
        toDoName.setText(toDo.name)
        dialog.setView(view)
        dialog.setPositiveButton("Update") { _: DialogInterface, _: Int ->
            if (toDoName.text.isNotEmpty()) {
                toDo.name = toDoName.text.toString()
                dbHandler.updateToDo(toDo)
                refreshList()
            }
        }
        dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

        }
        dialog.show()
    }

    override fun onResume() {
        refreshList()
        super.onResume()
    }

    private fun refreshList() {
        list = dbHandler.getToDos()
        adapter = DashboardAdapter(this, list!!)
        recyclerview_dashboard.adapter = adapter
    }


    class DashboardAdapter(val activity: DashboardActivity, val list: MutableList<ToDo>) :
        RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(activity)
                    .inflate(R.layout.recyclerview_child_dashboard, p0, false)
            )
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
            holder.toDoName.text = list[p1].name

            holder.toDoName.setOnClickListener {
                val intent = Intent(activity, ItemActivity::class.java)
                intent.putExtra(INTENT_TODO_ID, list[p1].id)
                intent.putExtra(INTENT_TODO_NAME, list[p1].name)
                activity.startActivity(intent)
            }

            holder.menu.setOnClickListener {
                val popup = PopupMenu(activity, holder.menu)
                popup.inflate(R.menu.dashboard_child)
                popup.setOnMenuItemClickListener {

                    when (it.itemId) {
                        R.id.menu_edit -> {
                            activity.updateToDo(list[p1])
                        }
                        R.id.menu_delete -> {
                            val dialog = AlertDialog.Builder(activity)
                            dialog.setTitle("Are you sure")
                            dialog.setMessage("Do you want to delete this task?")
                            dialog.setPositiveButton("Continue") { _: DialogInterface, _: Int ->
                                activity.dbHandler.deleteToDo(list[p1].id)
                                activity.refreshList()
                            }
                            dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

                            }
                            dialog.show()
                        }
                        R.id.menu_mark_as_completed -> {
                            activity.dbHandler.updateToDoItemCompletedStatus(list[p1].id, true)
                        }
                        R.id.menu_reset -> {
                            activity.dbHandler.updateToDoItemCompletedStatus(list[p1].id, false)
                        }
                    }

                    true
                }
                popup.show()
            }

            holder.move.setOnTouchListener { v, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    activity.touchHelper?.startDrag(holder)
                }
                false
            }
        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val toDoName: TextView = v.findViewById(R.id.textview_todo_name)
            val menu: ImageView = v.findViewById(R.id.imageview_menu)
            val move: ImageView = v.findViewById(R.id.imageview_move2)

        }
    }

    fun OnLogoutButtonPressed(view: View) {
        val myIntent = Intent(view.context, LoginActivity::class.java)
        startActivity(myIntent)
    }

}