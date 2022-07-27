package com.example.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.asteroidradar.R
import com.example.asteroidradar.databinding.FragmentMainBinding
import com.example.asteroidradar.util.AsteroidDateFilter

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        val adapter = AsteroidListAdapter(
            listener = AsteroidListener { asteroid ->
                findNavController().navigate(MainFragmentDirections.actionShowDetail(asteroid))
            }
        )

        binding.asteroidRecycler.adapter = adapter

        viewModel.asteroidList.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it)
            }
        }

        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().addMenuProvider(
            MainMenuProvider {
                val filter = when (it.itemId) {
                    R.id.show_today_menu -> AsteroidDateFilter.ViewToday
                    R.id.show_saved_menu -> AsteroidDateFilter.ViewSaved
                    R.id.show_week_menu -> AsteroidDateFilter.ViewWeek
                    else -> AsteroidDateFilter.ViewSaved
                }
                viewModel.filterAsteroidList(filter)
            }, viewLifecycleOwner
        )
    }

}

class MainMenuProvider(val onItemClicked: (menuItem: MenuItem) -> Unit) : MenuProvider {
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main_overflow_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        onItemClicked(menuItem)
        return true
    }
}


