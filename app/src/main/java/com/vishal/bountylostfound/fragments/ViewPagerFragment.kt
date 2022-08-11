package com.vishal.bountylostfound.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.vishal.bountylostfound.adapter.ViewPagerAdapter
import com.vishal.bountylostfound.dao.found
import com.vishal.bountylostfound.databinding.FragmentViewPagerBinding
import com.vishal.bountylostfound.viewModel.SharedViewmodel


class ViewPagerFragment : Fragment() {

    private var _binding: FragmentViewPagerBinding? = null
    private val binding get()= _binding!!
    private val viewmodel: SharedViewmodel by activityViewModels()
    lateinit var z:List<MutableLiveData<ArrayList<found>>>



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewPagerBinding.inflate(inflater, container, false)
        val view = binding.root
        z = listOf(viewmodel.userList,viewmodel.userList2)
        val adapter = ViewPagerAdapter(z)
        binding.viewPager.adapter = adapter
        val tabs = listOf<String>("Found","Lost")
        TabLayoutMediator(binding.tablayout,binding.viewPager) {
                tab,position -> tab.text = tabs[position].toString()
        }.attach()

        binding.tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        }
        )
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton4.setOnClickListener {
            val action = ViewPagerFragmentDirections.actionViewPagerFragmentToFoundFragment()
            view.findNavController().navigate(action)
        }
    }
}