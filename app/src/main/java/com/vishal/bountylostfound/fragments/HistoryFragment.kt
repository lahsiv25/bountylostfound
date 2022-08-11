package com.vishal.bountylostfound.fragments

import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vishal.bountylostfound.Constants
import com.vishal.bountylostfound.dao.found
import com.vishal.bountylostfound.adapter.MyHistoryAdapter2
import com.vishal.bountylostfound.databinding.FragmentHistoryBinding
import com.vishal.bountylostfound.viewModel.SharedViewmodel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HistoryFragment:Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    val database = Firebase.database(Constants.REFERENCE_URL)
    private val viewmodel: SharedViewmodel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        binding
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var userlist = arrayListOf<found>()
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("uploading")
        progressDialog.setCancelable(false)
        progressDialog.show()
        var favlistf = arrayListOf<String>()


        database.getReference("Favourites")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("foundEntry")
            .get().addOnSuccessListener {
                for (i in it.children) {
                    val u = i.getValue(found::class.java)
                    if (u != null) {
                        favlistf.add(u.orderId!!)
                    }
                }
                database.getReference("Favourites")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid).child("lostEntry")
                    .get().addOnSuccessListener {
                        for (i in it.children) {
                            val u = i.getValue(found::class.java)
                            if (u != null) {
                                favlistf.add(u.orderId!!)
                            }
                        }
                        database.getReference("History")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnSuccessListener {
                                for (userSnapshot in it.children){
                                    val user = userSnapshot.getValue(found::class.java)
                                    if (favlistf.contains(user!!.orderId)) {
                                        user.favourite = true
                                        userlist.add(user!!)
                                    }else userlist.add(user!!)
                                }
                                if (userlist.isEmpty()){
                                    if(progressDialog.isShowing){
                                        progressDialog.dismiss()
                                    }
                                    binding.emptycard.isVisible = true
                                }else{
                                    var adapter1 = MyHistoryAdapter2(userlist)
                                    binding.recyclerViewhistory.adapter = adapter1

                                if(progressDialog.isShowing){
                                    progressDialog.dismiss()
                                }
                                //Toast.makeText(context, "Swipe to Edit or Delete", Toast.LENGTH_LONG).show()
                                val swipe = object : SwipeToDelete(requireContext()){
                                    @RequiresApi(Build.VERSION_CODES.O)
                                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                                        when(direction){
                                            ItemTouchHelper.LEFT -> {
                                                //Toast.makeText(requireContext(), "Delete", Toast.LENGTH_SHORT).show()
                                                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                                    .setTitle("Delete entry?")
                                                    .setCancelable(false)
                                                    .setMessage("Are you sure? Your entry will be permenently deleted and if you've fixed any reward/bounty we will refund it to you within 2 days")
                                                    .setNegativeButton(
                                                        ("No"),
                                                        DialogInterface.OnClickListener {
                                                                arg0, arg1 ->
                                                            adapter1.notifyDataSetChanged()
                                                            //Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show()
                                                        })
                                                    .setPositiveButton(
                                                        ("yes"),
                                                        DialogInterface.OnClickListener {
                                                                arg0, arg1 -> database.getReference("allData").child("allUser")
                                                            .child(userlist[viewHolder.absoluteAdapterPosition]
                                                                .lostOrFound!!).child(userlist[viewHolder.absoluteAdapterPosition]
                                                                .orderId!!).removeValue().addOnCompleteListener {
                                                                database.getReference("History")
                                                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                                    .child(userlist[viewHolder.absoluteAdapterPosition]
                                                                        .orderId!!).removeValue().addOnCompleteListener {
                                                                        val time = date()
                                                                        database.getReference("Deleted")
                                                                            .child("${time} ${FirebaseAuth.getInstance().currentUser!!.uid}").setValue(
                                                                                "Deleted ${userlist[viewHolder.absoluteAdapterPosition]}"
                                                                            ).addOnCompleteListener {
                                                                                database.getReference("Favourites").child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                                                    .child(userlist[viewHolder.absoluteAdapterPosition]
                                                                                        .lostOrFound!!).child(userlist[viewHolder.absoluteAdapterPosition]
                                                                                        .orderId!!).removeValue().addOnCompleteListener {
                                                                                        userlist.removeAt(viewHolder.absoluteAdapterPosition)
                                                                                        adapter1.notifyDataSetChanged()
                                                                                        //Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show()
                                                                                    }
                                                                            }
                                                                    }
                                                            }
                                                        })
                                                    .create().show()
                                            }
                                            ItemTouchHelper.RIGHT -> {
                                                //Toast.makeText(requireContext(), "Edit", Toast.LENGTH_SHORT).show()
                                                val posit = viewHolder.absoluteAdapterPosition
                                                adapter1.notifyDataSetChanged()
                                                val navController = Navigation.findNavController(requireView())
                                                val action = HistoryFragmentDirections
                                                    .actionHistoryFragmentToEditFragment(userlist[posit])
                                                navController.navigate(action)
                                            }
                                        }
                                    }
                                }
                                val itemTouchHelper = ItemTouchHelper(swipe)
                                itemTouchHelper.attachToRecyclerView(binding.recyclerViewhistory)

                            }
                            }
                    }
            }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun date():String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddhhmmSS")
        val formatted = current.format(formatter)
        return formatted.toString()
    }
}